package com.example.backend.service;

import com.example.backend.dto.ContractRequest;
import com.example.backend.entity.Apartment;
import com.example.backend.entity.Contract;
import com.example.backend.entity.Customer;
import com.example.backend.entity.Invoice;
import com.example.backend.entity.User;

import com.example.backend.repository.ApartmentRepository;
import com.example.backend.repository.ContractRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.InvoiceRepository;
import com.example.backend.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final CustomerRepository customerRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ContractService(
            ContractRepository contractRepository,
            CustomerRepository customerRepository,
            ApartmentRepository apartmentRepository,
            UserRepository userRepository,
            InvoiceRepository invoiceRepository
    ) {
        this.contractRepository = contractRepository;
        this.customerRepository = customerRepository;
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
    }

    // =========================================================
    // GET ALL
    // =========================================================

    public List<Contract> getAllContracts() {

        return contractRepository.findAll();
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    public Contract getContractById(Long id) {

        return contractRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy hợp đồng có ID: " + id
                        )
                );
    }

    // =========================================================
    // CREATE CONTRACT
    // =========================================================

    @Transactional
    public Contract createContract(
            ContractRequest request
    ) {

        // -----------------------------------------------------
        // 1. TÌM CUSTOMER
        // -----------------------------------------------------

        Customer customer = customerRepository
                .findByName(request.getCustomerName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy khách hàng: "
                                        + request.getCustomerName()
                        )
                );

        // -----------------------------------------------------
        // 2. TÌM APARTMENT
        // -----------------------------------------------------

        Apartment apartment = apartmentRepository
                .findByName(request.getApartmentName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy căn hộ: "
                                        + request.getApartmentName()
                        )
                );

        // -----------------------------------------------------
        // 3. LẤY USER ĐANG ĐĂNG NHẬP
        // -----------------------------------------------------

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().equals("anonymousUser")) {

            throw new RuntimeException(
                    "Không xác định được người dùng đang đăng nhập"
            );
        }

        String username = authentication.getName();

        // -----------------------------------------------------
        // 4. TÌM USER
        // -----------------------------------------------------

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy user: " + username
                        )
                );

        // -----------------------------------------------------
        // 5. KIỂM TRA TIỀN THUÊ
        // -----------------------------------------------------

        if (request.getMonthlyRent() == null) {

            throw new RuntimeException(
                    "Tiền thuê hàng tháng không được để trống"
            );
        }

        if (request.getMonthlyRent() <= 0) {

            throw new RuntimeException(
                    "Tiền thuê hàng tháng phải lớn hơn 0"
            );
        }

        // -----------------------------------------------------
        // 6. TẠO CONTRACT
        // -----------------------------------------------------

        Contract contract = new Contract();

        contract.setUserId(
                user.getId().longValue()
        );

        contract.setApartmentId(
                apartment.getId()
        );

        contract.setCustomerName(
                customer.getName()
        );

        contract.setApartmentName(
                apartment.getName()
        );

        contract.setStartDate(
                request.getStartDate()
        );

        contract.setEndDate(
                request.getEndDate()
        );

        contract.setMonthlyRent(
                request.getMonthlyRent()
        );

        contract.setStatus(
                request.getStatus()
        );

        // -----------------------------------------------------
        // 7. LƯU CONTRACT
        // -----------------------------------------------------

        Contract savedContract =
                contractRepository.save(contract);

        // =====================================================
        // 8. TỰ ĐỘNG TẠO INVOICE
        // =====================================================

        LocalDate today = LocalDate.now();

        Invoice invoice = new Invoice();

        // -----------------------------------------------------
        // Liên kết invoice với contract
        // -----------------------------------------------------

        invoice.setContractId(
                savedContract.getId()
        );

        // -----------------------------------------------------
        // Tháng hiện tại
        // -----------------------------------------------------

        invoice.setMonth(
                today.getMonthValue()
        );

        // -----------------------------------------------------
        // Năm hiện tại
        // -----------------------------------------------------

        invoice.setYear(
                today.getYear()
        );

        // -----------------------------------------------------
        // QUAN TRỌNG:
        // Contract.monthlyRent = Double
        // Invoice.amount = BigDecimal
        //
        // Chuyển Double -> BigDecimal
        // -----------------------------------------------------

        invoice.setAmount(
                BigDecimal.valueOf(
                        savedContract.getMonthlyRent()
                )
        );

        // -----------------------------------------------------
        // HẠN THANH TOÁN
        // Ngày cuối tháng hiện tại
        // -----------------------------------------------------

        LocalDate endOfMonth =
                today.withDayOfMonth(
                        today.lengthOfMonth()
                );

        invoice.setDueDate(
                endOfMonth
        );

        // -----------------------------------------------------
        // ĐÁNH DẤU ĐÃ THANH TOÁN
        // Dashboard sẽ tính khoản này vào doanh thu
        // -----------------------------------------------------

        invoice.setStatus(
                "PAID"
        );

        // -----------------------------------------------------
        // LƯU INVOICE
        // -----------------------------------------------------

        invoiceRepository.save(invoice);

        // -----------------------------------------------------
        // 9. TRẢ CONTRACT VỀ FRONTEND
        // -----------------------------------------------------

        return savedContract;
    }

    // =========================================================
    // UPDATE CONTRACT
    // =========================================================

    public Contract updateContract(
            Long id,
            ContractRequest request
    ) {

        // -----------------------------------------------------
        // 1. LẤY CONTRACT
        // -----------------------------------------------------

        Contract contract =
                getContractById(id);

        // -----------------------------------------------------
        // 2. TÌM CUSTOMER
        // -----------------------------------------------------

        Customer customer =
                customerRepository
                        .findByName(
                                request.getCustomerName()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy khách hàng: "
                                                + request.getCustomerName()
                                )
                        );

        // -----------------------------------------------------
        // 3. TÌM APARTMENT
        // -----------------------------------------------------

        Apartment apartment =
                apartmentRepository
                        .findByName(
                                request.getApartmentName()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy căn hộ: "
                                                + request.getApartmentName()
                                )
                        );

        // -----------------------------------------------------
        // 4. KIỂM TRA TIỀN THUÊ
        // -----------------------------------------------------

        if (request.getMonthlyRent() == null) {

            throw new RuntimeException(
                    "Tiền thuê hàng tháng không được để trống"
            );
        }

        if (request.getMonthlyRent() <= 0) {

            throw new RuntimeException(
                    "Tiền thuê hàng tháng phải lớn hơn 0"
            );
        }

        // -----------------------------------------------------
        // 5. CẬP NHẬT CONTRACT
        // -----------------------------------------------------

        contract.setApartmentId(
                apartment.getId()
        );

        contract.setCustomerName(
                customer.getName()
        );

        contract.setApartmentName(
                apartment.getName()
        );

        contract.setStartDate(
                request.getStartDate()
        );

        contract.setEndDate(
                request.getEndDate()
        );

        contract.setMonthlyRent(
                request.getMonthlyRent()
        );

        contract.setStatus(
                request.getStatus()
        );

        // -----------------------------------------------------
        // Không thay đổi userId
        // -----------------------------------------------------

        return contractRepository.save(contract);
    }

    // =========================================================
    // DELETE CONTRACT
    // =========================================================

    public void deleteContract(Long id) {

        // -----------------------------------------------------
        // 1. KIỂM TRA CONTRACT
        // -----------------------------------------------------

        Contract contract =
                getContractById(id);

        // -----------------------------------------------------
        // 2. KIỂM TRA CONTRACT ĐÃ CÓ INVOICE CHƯA
        // -----------------------------------------------------

        boolean hasInvoice =
                invoiceRepository.existsByContractId(id);

        // -----------------------------------------------------
        // 3. NẾU ĐÃ CÓ INVOICE
        // KHÔNG CHO XÓA
        // -----------------------------------------------------

        if (hasInvoice) {

            throw new IllegalStateException(
                    "Không thể xóa hợp đồng vì hợp đồng đã có hóa đơn."
            );
        }

        // -----------------------------------------------------
        // 4. CHƯA CÓ INVOICE → CHO XÓA
        // -----------------------------------------------------

        contractRepository.delete(contract);
    }
}