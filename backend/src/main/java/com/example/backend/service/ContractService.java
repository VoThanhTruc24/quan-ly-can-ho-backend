package com.example.backend.service;

import com.example.backend.dto.ContractRequest;
import com.example.backend.entity.Apartment;
import com.example.backend.entity.Contract;
import com.example.backend.entity.Customer;
import com.example.backend.entity.User;

import com.example.backend.repository.ApartmentRepository;
import com.example.backend.repository.ContractRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final CustomerRepository customerRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;

    public ContractService(
            ContractRepository contractRepository,
            CustomerRepository customerRepository,
            ApartmentRepository apartmentRepository,
            UserRepository userRepository
    ) {
        this.contractRepository = contractRepository;
        this.customerRepository = customerRepository;
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // GET ALL
    // =========================

    public List<Contract> getAllContracts() {

        return contractRepository.findAll();
    }

    // =========================
    // GET BY ID
    // =========================

    public Contract getContractById(Long id) {

        return contractRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy hợp đồng có ID: " + id
                        )
                );
    }

    // =========================
    // CREATE
    // =========================

    public Contract createContract(
            ContractRequest request
    ) {

        // --------------------------------
        // 1. Tìm customer theo tên
        // --------------------------------

        Customer customer = customerRepository
                .findByName(request.getCustomerName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy khách hàng: "
                                        + request.getCustomerName()
                        )
                );

        // --------------------------------
        // 2. Tìm apartment theo tên
        // --------------------------------

        Apartment apartment = apartmentRepository
                .findByName(request.getApartmentName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy căn hộ: "
                                        + request.getApartmentName()
                        )
                );

        // --------------------------------
        // 3. Lấy user đang đăng nhập
        // --------------------------------

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

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy user: " + username
                        )
                );

        // --------------------------------
        // 4. Tạo contract
        // --------------------------------

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

        // --------------------------------
        // 5. Lưu database
        // --------------------------------

        return contractRepository.save(contract);
    }

    // =========================
    // UPDATE
    // =========================

    public Contract updateContract(
            Long id,
            ContractRequest request
    ) {

        // --------------------------------
        // 1. Lấy contract hiện tại
        // --------------------------------

        Contract contract = getContractById(id);

        // --------------------------------
        // 2. Tìm customer
        // --------------------------------

        Customer customer = customerRepository
                .findByName(request.getCustomerName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy khách hàng: "
                                        + request.getCustomerName()
                        )
                );

        // --------------------------------
        // 3. Tìm apartment
        // --------------------------------

        Apartment apartment = apartmentRepository
                .findByName(request.getApartmentName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy căn hộ: "
                                        + request.getApartmentName()
                        )
                );

        // --------------------------------
        // 4. Cập nhật
        // --------------------------------

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

        // Không thay đổi userId
        // vì đây là user tạo hợp đồng

        return contractRepository.save(contract);
    }

    // =========================
    // DELETE
    // =========================

    public void deleteContract(Long id) {

        Contract contract = getContractById(id);

        contractRepository.delete(contract);
    }
}