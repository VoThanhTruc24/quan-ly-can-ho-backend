package com.example.backend.service;

import com.example.backend.dto.CustomerRentalResponse;
import com.example.backend.entity.Apartment;
import com.example.backend.entity.Contract;
import com.example.backend.entity.Customer;
import com.example.backend.repository.ApartmentRepository;
import com.example.backend.repository.ContractRepository;
import com.example.backend.repository.CustomerRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ContractRepository contractRepository;
    private final ApartmentRepository apartmentRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            ContractRepository contractRepository,
            ApartmentRepository apartmentRepository
    ) {
        this.customerRepository = customerRepository;
        this.contractRepository = contractRepository;
        this.apartmentRepository = apartmentRepository;
    }

    // =========================================================
    // GET ALL
    // =========================================================

    public List<Customer> getAllCustomers() {

        return customerRepository.findAll();
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    public Customer getCustomerById(Long id) {

        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy khách hàng có ID: " + id
                        )
                );
    }

    // =========================================================
    // CREATE
    // =========================================================

    public Customer createCustomer(Customer customer) {

        return customerRepository.save(customer);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    public Customer updateCustomer(
            Long id,
            Customer customerData
    ) {

        Customer customer = getCustomerById(id);

        customer.setName(
                customerData.getName()
        );

        customer.setEmail(
                customerData.getEmail()
        );

        customer.setPhone(
                customerData.getPhone()
        );

        customer.setAddress(
                customerData.getAddress()
        );

        return customerRepository.save(customer);
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void deleteCustomer(Long id) {

        Customer customer = getCustomerById(id);

        customerRepository.delete(customer);
    }

    // =========================================================
    // GET CUSTOMERS + RENTAL INFO
    // =========================================================

    public List<CustomerRentalResponse> getCustomersWithRentalInfo() {

        // Lấy toàn bộ Customer
        List<Customer> customers =
                customerRepository.findAll();

        List<CustomerRentalResponse> result =
                new ArrayList<>();

        for (Customer customer : customers) {

            // -------------------------------------------------
            // Thông tin cơ bản của Customer
            // -------------------------------------------------

            CustomerRentalResponse response =
                    new CustomerRentalResponse();

            response.setId(
                    customer.getId()
            );

            response.setName(
                    customer.getName()
            );

            response.setEmail(
                    customer.getEmail()
            );

            response.setPhone(
                    customer.getPhone()
            );

            response.setAddress(
                    customer.getAddress()
            );

            // -------------------------------------------------
            // Tìm Contract theo customer_id
            // -------------------------------------------------

            List<Contract> contracts =
                    contractRepository.findByCustomerId(
                            customer.getId()
                    );

            // -------------------------------------------------
            // Customer chưa có hợp đồng
            // -------------------------------------------------

            if (contracts == null || contracts.isEmpty()) {

                result.add(response);

                continue;
            }

            // -------------------------------------------------
            // Lấy hợp đồng đầu tiên
            // -------------------------------------------------

            Contract contract = contracts.get(0);

            // -------------------------------------------------
            // THÔNG TIN HỢP ĐỒNG
            // -------------------------------------------------

            response.setContractId(
                    contract.getId()
            );

            response.setContractStatus(
                    contract.getStatus()
            );

            // -------------------------------------------------
            // THÔNG TIN CĂN HỘ
            // -------------------------------------------------

            if (contract.getApartmentId() != null) {

                Apartment apartment =
                        apartmentRepository
                                .findById(
                                        contract.getApartmentId()
                                )
                                .orElse(null);

                if (apartment != null) {

                    response.setApartmentId(
                            apartment.getId()
                    );

                    response.setApartmentName(
                            apartment.getName()
                    );

                    // -------------------------------------------------
                    // THÔNG TIN OWNER
                    // -------------------------------------------------

                    if (apartment.getOwner() != null) {

                        response.setOwnerId(
                                apartment
                                        .getOwner()
                                        .getId()
                                        .longValue()
                        );

                        response.setOwnerName(
                                apartment
                                        .getOwner()
                                        .getFullName()
                        );
                    }
                }
            }

            // -------------------------------------------------
            // ADD RESPONSE
            // -------------------------------------------------

            result.add(response);
        }

        return result;
    }
}