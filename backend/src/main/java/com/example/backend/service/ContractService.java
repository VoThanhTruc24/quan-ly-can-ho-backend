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
    // GET CONTRACTS BY CUSTOMER
    // =========================================================

    public List<Contract> getContractsByCustomerId(
            Long customerId
    ) {

        return contractRepository.findByCustomerId(
                customerId
        );
    }

    // =========================================================
    // GET CONTRACTS BY APARTMENT
    // =========================================================

    public List<Contract> getContractsByApartmentId(
            Long apartmentId
    ) {

        return contractRepository.findByApartmentId(
                apartmentId
        );
    }

    // =========================================================
    // CREATE CONTRACT
    //
    // Được gọi từ trang Khách hàng.
    //
    // Luồng:
    // Customer
    //     ↓
    // Contract
    //     ↓
    // Apartment
    //     ↓
    // RENTED
    //     ↓
    // Invoice UNPAID
    //
    // Điều kiện:
    // - Apartment phải có Owner
    // - Apartment phải còn trống
    // - Apartment không có contract ACTIVE
    // =========================================================

    @Transactional
    public Contract createContract(
            ContractRequest request
    ) {

        // -----------------------------------------------------
        // 1. VALIDATE REQUEST
        // -----------------------------------------------------

        if (request == null) {
            throw new RuntimeException(
                    "Dữ liệu hợp đồng không được để trống"
            );
        }

        if (
                request.getCustomerName() == null
                        || request.getCustomerName().trim().isEmpty()
        ) {
            throw new RuntimeException(
                    "Tên khách hàng không được để trống"
            );
        }

        if (
                request.getApartmentName() == null
                        || request.getApartmentName().trim().isEmpty()
        ) {
            throw new RuntimeException(
                    "Tên căn hộ không được để trống"
            );
        }

        if (request.getStartDate() == null) {
            throw new RuntimeException(
                    "Ngày bắt đầu không được để trống"
            );
        }

        if (request.getEndDate() == null) {
            throw new RuntimeException(
                    "Ngày kết thúc không được để trống"
            );
        }

        if (
                !request.getEndDate()
                        .isAfter(request.getStartDate())
        ) {
            throw new RuntimeException(
                    "Ngày kết thúc phải sau ngày bắt đầu"
            );
        }

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
        // 2. TÌM CUSTOMER
        // -----------------------------------------------------

        Customer customer =
                customerRepository
                        .findByName(
                                request.getCustomerName().trim()
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
                                request.getApartmentName().trim()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy căn hộ: "
                                                + request.getApartmentName()
                                )
                        );

        // -----------------------------------------------------
        // 4. KIỂM TRA OWNER
        //
        // Căn hộ bắt buộc phải có Owner trước khi cho thuê.
        // -----------------------------------------------------

        if (apartment.getOwner() == null) {
            throw new RuntimeException(
                    "Căn hộ "
                            + apartment.getName()
                            + " chưa được gán Owner nên không thể cho thuê."
            );
        }

        // -----------------------------------------------------
        // 5. KIỂM TRA TRẠNG THÁI CĂN HỘ
        //
        // Chỉ căn AVAILABLE mới được tạo hợp đồng mới.
        // -----------------------------------------------------

        String apartmentStatus =
                apartment.getStatus();

        if (
                apartmentStatus == null
                        || !"AVAILABLE".equalsIgnoreCase(
                        apartmentStatus.trim()
                )
        ) {
            throw new RuntimeException(
                    "Căn hộ "
                            + apartment.getName()
                            + " hiện không còn trống."
            );
        }

        // -----------------------------------------------------
        // 6. KIỂM TRA CĂN HỘ ĐÃ CÓ NGƯỜI THUÊ CHƯA
        // -----------------------------------------------------

        List<Contract> apartmentContracts =
                contractRepository.findByApartmentId(
                        apartment.getId()
                );

        boolean apartmentAlreadyRented =
                apartmentContracts.stream()
                        .anyMatch(contract ->
                                "ACTIVE".equalsIgnoreCase(
                                        contract.getStatus()
                                )
                        );

        if (apartmentAlreadyRented) {
            throw new RuntimeException(
                    "Căn hộ "
                            + apartment.getName()
                            + " hiện đang có hợp đồng thuê."
            );
        }

        // -----------------------------------------------------
        // 7. KIỂM TRA NGƯỜI ĐĂNG NHẬP
        //
        // Vẫn giữ kiểm tra authentication để API không bị gọi
        // bởi người chưa đăng nhập.
        // -----------------------------------------------------

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication == null
                        || !authentication.isAuthenticated()
                        || authentication.getName() == null
                        || authentication.getName()
                        .equals("anonymousUser")
        ) {

            throw new RuntimeException(
                    "Không xác định được người dùng đang đăng nhập"
            );
        }

        // -----------------------------------------------------
        // 8. TÌM USER ĐANG ĐĂNG NHẬP
        //
        // Vẫn giữ lại để không phá cấu trúc hiện tại.
        // Tuy nhiên KHÔNG dùng user này làm Owner của contract.
        // Owner thực sự lấy từ apartment.getOwner().
        // -----------------------------------------------------

        String username =
                authentication.getName();

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy user: "
                                                + username
                                )
                        );

        // Tránh cảnh báo IDE về biến user không được sử dụng
        if (user == null) {
            throw new RuntimeException(
                    "Không xác định được tài khoản đang đăng nhập"
            );
        }

        // -----------------------------------------------------
        // 9. TẠO CONTRACT
        // -----------------------------------------------------

        Contract contract =
                new Contract();

        // -----------------------------------------------------
        // OWNER CỦA APARTMENT
        //
        // Đây là thay đổi quan trọng.
        //
        // Không dùng Admin đang đăng nhập.
        // Contract.userId = apartment.owner.id
        // -----------------------------------------------------

        contract.setUserId(
                apartment.getOwner()
                        .getId()
                        .longValue()
        );

        // -----------------------------------------------------
        // CUSTOMER
        // -----------------------------------------------------

        contract.setCustomerId(
                customer.getId()
        );

        contract.setCustomerName(
                customer.getName()
        );

        // -----------------------------------------------------
        // APARTMENT
        // -----------------------------------------------------

        contract.setApartmentId(
                apartment.getId()
        );

        contract.setApartmentName(
                apartment.getName()
        );

        // -----------------------------------------------------
        // DATE
        // -----------------------------------------------------

        contract.setStartDate(
                request.getStartDate()
        );

        contract.setEndDate(
                request.getEndDate()
        );

        // -----------------------------------------------------
        // RENT
        // -----------------------------------------------------

        contract.setMonthlyRent(
                request.getMonthlyRent()
        );

        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        String status =
                request.getStatus();

        if (
                status == null
                        || status.trim().isEmpty()
        ) {
            status = "ACTIVE";
        }

        contract.setStatus(
                normalizeContractStatus(status)
        );

        // -----------------------------------------------------
        // 10. SAVE CONTRACT
        // -----------------------------------------------------

        Contract savedContract =
                contractRepository.save(
                        contract
                );

        // -----------------------------------------------------
        // 11. ĐỔI TRẠNG THÁI CĂN HỘ
        // -----------------------------------------------------

        if (
                "ACTIVE".equalsIgnoreCase(
                        savedContract.getStatus()
                )
        ) {

            apartment.setStatus(
                    "RENTED"
            );

            apartmentRepository.save(
                    apartment
            );
        }

        // -----------------------------------------------------
        // 12. TỰ ĐỘNG TẠO INVOICE
        // -----------------------------------------------------

        LocalDate today =
                LocalDate.now();

        Invoice invoice =
                new Invoice();

        // -----------------------------------------------------
        // Contract ID
        // -----------------------------------------------------

        invoice.setContractId(
                savedContract.getId()
        );

        // -----------------------------------------------------
        // Current month
        // -----------------------------------------------------

        invoice.setMonth(
                today.getMonthValue()
        );

        // -----------------------------------------------------
        // Current year
        // -----------------------------------------------------

        invoice.setYear(
                today.getYear()
        );

        // -----------------------------------------------------
        // Amount
        // -----------------------------------------------------

        invoice.setAmount(
                BigDecimal.valueOf(
                        savedContract
                                .getMonthlyRent()
                )
        );

        // -----------------------------------------------------
        // Due date
        //
        // Ngày cuối tháng hiện tại
        // -----------------------------------------------------

        LocalDate endOfMonth =
                today.withDayOfMonth(
                        today.lengthOfMonth()
                );

        invoice.setDueDate(
                endOfMonth
        );

        // =====================================================
        // 13. HÓA ĐƠN MỚI TẠO = UNPAID
        //
        // Không được tính vào doanh thu ngay.
        // Chỉ khi Owner thanh toán thì mới PAID.
        // =====================================================

        invoice.setStatus(
                "UNPAID"
        );

        invoice.setPaidDate(
                null
        );

        invoice.setPaymentMethod(
                null
        );

        // -----------------------------------------------------
        // SAVE INVOICE
        // -----------------------------------------------------

        invoiceRepository.save(
                invoice
        );

        return savedContract;
    }

    // =========================================================
    // UPDATE CONTRACT
    //
    // Hỗ trợ:
    // - sửa Customer
    // - sửa Apartment
    // - sửa ngày
    // - sửa tiền thuê
    // - sửa trạng thái
    //
    // Đồng thời xử lý trạng thái Apartment.
    // =========================================================

    @Transactional
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
        // 2. VALIDATE
        // -----------------------------------------------------

        if (request == null) {
            throw new RuntimeException(
                    "Dữ liệu hợp đồng không được để trống"
            );
        }

        if (
                request.getCustomerName() == null
                        || request.getCustomerName().trim().isEmpty()
        ) {
            throw new RuntimeException(
                    "Tên khách hàng không được để trống"
            );
        }

        if (
                request.getApartmentName() == null
                        || request.getApartmentName().trim().isEmpty()
        ) {
            throw new RuntimeException(
                    "Tên căn hộ không được để trống"
            );
        }

        if (request.getStartDate() == null) {
            throw new RuntimeException(
                    "Ngày bắt đầu không được để trống"
            );
        }

        if (request.getEndDate() == null) {
            throw new RuntimeException(
                    "Ngày kết thúc không được để trống"
            );
        }

        if (
                !request.getEndDate()
                        .isAfter(request.getStartDate())
        ) {
            throw new RuntimeException(
                    "Ngày kết thúc phải sau ngày bắt đầu"
            );
        }

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
        // 3. TÌM CUSTOMER
        // -----------------------------------------------------

        Customer customer =
                customerRepository
                        .findByName(
                                request.getCustomerName().trim()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy khách hàng: "
                                                + request.getCustomerName()
                                )
                        );

        // -----------------------------------------------------
        // 4. TÌM APARTMENT MỚI
        // -----------------------------------------------------

        Apartment newApartment =
                apartmentRepository
                        .findByName(
                                request.getApartmentName().trim()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy căn hộ: "
                                                + request.getApartmentName()
                                )
                        );

        // -----------------------------------------------------
        // 5. CĂN HỘ BẮT BUỘC PHẢI CÓ OWNER
        // -----------------------------------------------------

        if (newApartment.getOwner() == null) {
            throw new RuntimeException(
                    "Căn hộ "
                            + newApartment.getName()
                            + " chưa được gán Owner nên không thể dùng cho hợp đồng."
            );
        }

        // -----------------------------------------------------
        // 6. APARTMENT CŨ
        // -----------------------------------------------------

        Apartment oldApartment =
                null;

        if (contract.getApartmentId() != null) {

            oldApartment =
                    apartmentRepository
                            .findById(
                                    contract.getApartmentId()
                            )
                            .orElse(null);
        }

        // -----------------------------------------------------
        // 7. NORMALIZE STATUS
        // -----------------------------------------------------

        String newStatus =
                normalizeContractStatus(
                        request.getStatus()
                );

        // -----------------------------------------------------
        // 8. KIỂM TRA APARTMENT MỚI
        // -----------------------------------------------------

        boolean apartmentChanged =
                oldApartment == null
                        || !oldApartment
                        .getId()
                        .equals(
                                newApartment.getId()
                        );

        // -----------------------------------------------------
        // Chỉ kiểm tra AVAILABLE khi đổi sang căn khác.
        //
        // Nếu đang chỉnh sửa thông tin của chính hợp đồng
        // trên cùng căn đang RENTED thì không chặn.
        // -----------------------------------------------------

        if (
                apartmentChanged
                        && "ACTIVE".equalsIgnoreCase(
                        newStatus
                )
        ) {

            String apartmentStatus =
                    newApartment.getStatus();

            if (
                    apartmentStatus == null
                            || !"AVAILABLE".equalsIgnoreCase(
                            apartmentStatus.trim()
                    )
            ) {
                throw new RuntimeException(
                        "Căn hộ "
                                + newApartment.getName()
                                + " hiện không còn trống."
                );
            }

            // -------------------------------------------------
            // CHECK ACTIVE CONTRACT
            // -------------------------------------------------

            List<Contract> apartmentContracts =
                    contractRepository
                            .findByApartmentId(
                                    newApartment.getId()
                            );

            boolean alreadyUsed =
                    apartmentContracts.stream()
                            .anyMatch(
                                    existingContract ->

                                            !existingContract
                                                    .getId()
                                                    .equals(id)

                                                    && "ACTIVE"
                                                    .equalsIgnoreCase(
                                                            existingContract
                                                                    .getStatus()
                                                    )
                            );

            if (alreadyUsed) {

                throw new RuntimeException(
                        "Căn hộ "
                                + newApartment.getName()
                                + " đang có người thuê."
                );
            }
        }

        // -----------------------------------------------------
        // 9. UPDATE CUSTOMER
        // -----------------------------------------------------

        contract.setCustomerId(
                customer.getId()
        );

        contract.setCustomerName(
                customer.getName()
        );

        // -----------------------------------------------------
        // 10. UPDATE APARTMENT
        // -----------------------------------------------------

        contract.setApartmentId(
                newApartment.getId()
        );

        contract.setApartmentName(
                newApartment.getName()
        );

        // -----------------------------------------------------
        // 11. OWNER CỦA APARTMENT
        //
        // Đồng bộ lại userId theo Owner thật của căn hộ.
        // -----------------------------------------------------

        contract.setUserId(
                newApartment.getOwner()
                        .getId()
                        .longValue()
        );

        // -----------------------------------------------------
        // 12. UPDATE DATE
        // -----------------------------------------------------

        contract.setStartDate(
                request.getStartDate()
        );

        contract.setEndDate(
                request.getEndDate()
        );

        // -----------------------------------------------------
        // 13. UPDATE RENT
        // -----------------------------------------------------

        contract.setMonthlyRent(
                request.getMonthlyRent()
        );

        // -----------------------------------------------------
        // 14. UPDATE STATUS
        // -----------------------------------------------------

        contract.setStatus(
                newStatus
        );

        // -----------------------------------------------------
        // 15. SAVE CONTRACT
        // -----------------------------------------------------

        Contract savedContract =
                contractRepository.save(
                        contract
                );

        // -----------------------------------------------------
        // 16. XỬ LÝ APARTMENT CŨ
        // -----------------------------------------------------

        if (
                oldApartment != null
                        && (
                        apartmentChanged
                                || !"ACTIVE"
                                .equalsIgnoreCase(
                                        newStatus
                                )
                )
        ) {

            updateApartmentStatusIfFree(
                    oldApartment
            );
        }

        // -----------------------------------------------------
        // 17. XỬ LÝ APARTMENT MỚI
        // -----------------------------------------------------

        if (
                "ACTIVE".equalsIgnoreCase(
                        newStatus
                )
        ) {

            newApartment.setStatus(
                    "RENTED"
            );

            apartmentRepository.save(
                    newApartment
            );

        } else {

            updateApartmentStatusIfFree(
                    newApartment
            );
        }

        return savedContract;
    }

    // =========================================================
    // DELETE CONTRACT
    // =========================================================

    @Transactional
    public void deleteContract(Long id) {

        // -----------------------------------------------------
        // 1. LẤY CONTRACT
        // -----------------------------------------------------

        Contract contract =
                getContractById(id);

        // -----------------------------------------------------
        // 2. KHÔNG CHO XÓA CONTRACT ĐÃ CÓ INVOICE
        //
        // Giữ nguyên để không phá dữ liệu hóa đơn.
        // -----------------------------------------------------

        boolean hasInvoice =
                invoiceRepository
                        .existsByContractId(id);

        if (hasInvoice) {
            throw new IllegalStateException(
                    "Không thể xóa hợp đồng vì hợp đồng đã có hóa đơn."
            );
        }

        // -----------------------------------------------------
        // 3. LƯU APARTMENT ID
        // -----------------------------------------------------

        Long apartmentId =
                contract.getApartmentId();

        // -----------------------------------------------------
        // 4. DELETE CONTRACT
        // -----------------------------------------------------

        contractRepository.delete(
                contract
        );

        // -----------------------------------------------------
        // 5. APARTMENT TRỞ LẠI TRỐNG
        // -----------------------------------------------------

        if (apartmentId != null) {

            Apartment apartment =
                    apartmentRepository
                            .findById(
                                    apartmentId
                            )
                            .orElse(null);

            if (apartment != null) {

                updateApartmentStatusIfFree(
                        apartment
                );
            }
        }
    }

    // =========================================================
    // NORMALIZE CONTRACT STATUS
    // =========================================================

    private String normalizeContractStatus(
            String status
    ) {

        if (
                status == null
                        || status.trim().isEmpty()
        ) {
            return "ACTIVE";
        }

        String normalized =
                status
                        .trim()
                        .toUpperCase();

        switch (normalized) {

            case "ACTIVE":
            case "EXPIRED":
            case "TERMINATED":
            case "CANCELLED":
                return normalized;

            case "ĐANG THUÊ":
            case "ĐANG HIỆU LỰC":
                return "ACTIVE";

            case "ĐÃ HẾT HẠN":
                return "EXPIRED";

            case "ĐÃ KẾT THÚC":
            case "ĐÃ CHẤM DỨT":
                return "TERMINATED";

            case "ĐÃ HỦY":
                return "CANCELLED";

            default:
                return normalized;
        }
    }

    // =========================================================
    // UPDATE APARTMENT STATUS IF FREE
    // =========================================================

    private void updateApartmentStatusIfFree(
            Apartment apartment
    ) {

        List<Contract> contracts =
                contractRepository
                        .findByApartmentId(
                                apartment.getId()
                        );

        boolean hasActiveContract =
                contracts.stream()
                        .anyMatch(contract ->
                                "ACTIVE".equalsIgnoreCase(
                                        contract.getStatus()
                                )
                        );

        if (!hasActiveContract) {

            apartment.setStatus(
                    "AVAILABLE"
            );

            apartmentRepository.save(
                    apartment
            );
        }
    }
}