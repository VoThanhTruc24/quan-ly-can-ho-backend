package com.example.backend.controller;

import com.example.backend.entity.Apartment;
import com.example.backend.entity.Contract;
import com.example.backend.entity.Invoice;
import com.example.backend.entity.User;

import com.example.backend.repository.ApartmentRepository;
import com.example.backend.repository.ContractRepository;
import com.example.backend.repository.InvoiceRepository;
import com.example.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner")
@CrossOrigin(origins = "http://localhost:5173")
public class OwnerController {

    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;


    public OwnerController(
            UserRepository userRepository,
            ApartmentRepository apartmentRepository,
            ContractRepository contractRepository,
            InvoiceRepository invoiceRepository
    ) {
        this.userRepository = userRepository;
        this.apartmentRepository = apartmentRepository;
        this.contractRepository = contractRepository;
        this.invoiceRepository = invoiceRepository;
    }


    // =====================================================
    // LẤY OWNER ĐANG ĐĂNG NHẬP
    // =====================================================

    private User getCurrentOwner() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (
                authentication == null
                        ||
                        !authentication.isAuthenticated()
                        ||
                        authentication.getName() == null
                        ||
                        authentication.getName()
                                .equals("anonymousUser")
        ) {

            throw new RuntimeException(
                    "Chưa đăng nhập"
            );
        }


        String username =
                authentication.getName();


        User owner =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy Owner: "
                                                + username
                                )
                        );


        if (
                !"OWNER".equalsIgnoreCase(
                        owner.getRole()
                )
        ) {

            throw new RuntimeException(
                    "Tài khoản không phải Owner"
            );
        }


        return owner;
    }


    // =====================================================
    // GET OWNER INFO
    // GET /api/owner/me
    // =====================================================

    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo() {

        User owner =
                getCurrentOwner();

        return ResponseEntity.ok(owner);
    }


    // =====================================================
    // GET APARTMENTS
    // GET /api/owner/me/apartments
    // =====================================================

    @GetMapping("/me/apartments")
    public ResponseEntity<List<Apartment>>
    getMyApartments() {

        User owner =
                getCurrentOwner();


        List<Apartment> apartments =
                apartmentRepository
                        .findByOwner_Id(
                                owner.getId().longValue()
                        );


        return ResponseEntity.ok(
                apartments
        );
    }


    // =====================================================
    // GET CONTRACTS
    // GET /api/owner/me/contracts
    // =====================================================

    @GetMapping("/me/contracts")
    public ResponseEntity<List<Contract>>
    getMyContracts() {

        User owner =
                getCurrentOwner();


        List<Contract> contracts =
                contractRepository.findByUserId(
                        owner.getId().longValue()
                );


        return ResponseEntity.ok(
                contracts
        );
    }


    // =====================================================
    // GET INVOICES
    // GET /api/owner/me/invoices
    // =====================================================

    @GetMapping("/me/invoices")
    public ResponseEntity<List<Invoice>>
    getMyInvoices() {

        User owner =
                getCurrentOwner();


        List<Contract> contracts =
                contractRepository.findByUserId(
                        owner.getId().longValue()
                );


        List<Invoice> invoices =
                new ArrayList<>();


        for (Contract contract :
                contracts) {

            if (contract.getId() == null) {
                continue;
            }


            List<Invoice> contractInvoices =
                    invoiceRepository
                            .findByContractId(
                                    contract.getId()
                            );


            invoices.addAll(
                    contractInvoices
            );
        }


        // ---------------------------------------------
        // SẮP XẾP:
        // NĂM GIẢM DẦN
        // THÁNG GIẢM DẦN
        // ---------------------------------------------

        invoices.sort(
                (a, b) -> {

                    int yearA =
                            a.getYear() == null
                                    ? 0
                                    : a.getYear();

                    int yearB =
                            b.getYear() == null
                                    ? 0
                                    : b.getYear();


                    if (yearA != yearB) {

                        return Integer.compare(
                                yearB,
                                yearA
                        );
                    }


                    int monthA =
                            a.getMonth() == null
                                    ? 0
                                    : a.getMonth();

                    int monthB =
                            b.getMonth() == null
                                    ? 0
                                    : b.getMonth();


                    return Integer.compare(
                            monthB,
                            monthA
                    );
                }
        );


        return ResponseEntity.ok(
                invoices
        );
    }


    // =====================================================
    // THANH TOÁN HÓA ĐƠN
    //
    // PUT
    // /api/owner/invoices/{invoiceId}/pay
    //
    // CHỈ HỖ TRỢ VNPAY
    // =====================================================

    @PutMapping("/invoices/{invoiceId}/pay")
    public ResponseEntity<?> payInvoice(
            @PathVariable Long invoiceId,
            @RequestBody PaymentRequest request
    ) {

        try {

            // =================================================
            // 1. OWNER ĐANG ĐĂNG NHẬP
            // =================================================

            User owner =
                    getCurrentOwner();


            // =================================================
            // 2. TÌM HÓA ĐƠN
            // =================================================

            Invoice invoice =
                    invoiceRepository
                            .findById(invoiceId)
                            .orElse(null);


            if (invoice == null) {

                return ResponseEntity
                        .status(
                                HttpStatus.NOT_FOUND
                        )
                        .body(
                                Map.of(
                                        "message",
                                        "Không tìm thấy hóa đơn"
                                )
                        );
            }


            // =================================================
            // 3. KHÔNG CHO THANH TOÁN LẠI
            // =================================================

            if (
                    "PAID".equalsIgnoreCase(
                            invoice.getStatus()
                    )
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Hóa đơn này đã được thanh toán"
                                )
                        );
            }


            // =================================================
            // 4. KIỂM TRA REQUEST
            // =================================================

            if (
                    request == null
                            ||
                            request.getPaymentMethod() == null
                            ||
                            request.getPaymentMethod()
                                    .trim()
                                    .isEmpty()
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Vui lòng chọn phương thức thanh toán"
                                )
                        );
            }


            String paymentMethod =
                    request.getPaymentMethod()
                            .trim()
                            .toUpperCase();


            // =================================================
            // 5. CHỈ CHO VNPAY
            // =================================================

            if (
                    !"VNPAY".equals(
                            paymentMethod
                    )
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Phương thức thanh toán này hiện chưa được hỗ trợ"
                                )
                        );
            }


            // =================================================
            // 6. KIỂM TRA CONTRACT ID
            // =================================================

            if (
                    invoice.getContractId() == null
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Hóa đơn chưa được liên kết với hợp đồng"
                                )
                        );
            }


            // =================================================
            // 7. LẤY CONTRACT CỦA OWNER
            // =================================================

            List<Contract> contracts =
                    contractRepository.findByUserId(
                            owner.getId().longValue()
                    );


            // =================================================
            // 8. KIỂM TRA INVOICE THUỘC OWNER
            //
            // Dùng longValue() để tránh:
            // Integer.equals(Long) = false
            // =================================================

            boolean belongsToOwner =
                    contracts.stream()
                            .anyMatch(
                                    contract ->

                                            contract.getId() != null

                                                    &&

                                                    invoice.getContractId() != null

                                                    &&

                                                    contract.getId()
                                                            .longValue()
                                                            ==
                                                            invoice.getContractId()
                                                                    .longValue()
                            );


            // =================================================
            // 9. KHÔNG THUỘC OWNER
            // =================================================

            if (!belongsToOwner) {

                return ResponseEntity
                        .status(
                                HttpStatus.FORBIDDEN
                        )
                        .body(
                                Map.of(
                                        "message",
                                        "Bạn không có quyền thanh toán hóa đơn này"
                                )
                        );
            }


            // =================================================
            // 10. CẬP NHẬT THANH TOÁN
            // =================================================

            invoice.setStatus("PAID");


            invoice.setPaidDate(
                    LocalDate.now()
            );


            invoice.setPaymentMethod(
                    "VNPAY"
            );


            // =================================================
            // 11. LƯU DATABASE
            // =================================================

            Invoice savedInvoice =
                    invoiceRepository.save(
                            invoice
                    );


            // =================================================
            // 12. TRẢ KẾT QUẢ
            // =================================================

            return ResponseEntity.ok(
                    savedInvoice
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage() != null
                                            ? e.getMessage()
                                            : "Có lỗi xảy ra khi thanh toán"
                            )
                    );
        }
    }


    // =====================================================
    // PAYMENT REQUEST
    // =====================================================

    public static class PaymentRequest {

        private String paymentMethod;


        public PaymentRequest() {
        }


        public String getPaymentMethod() {

            return paymentMethod;
        }


        public void setPaymentMethod(
                String paymentMethod
        ) {

            this.paymentMethod =
                    paymentMethod;
        }
    }
}