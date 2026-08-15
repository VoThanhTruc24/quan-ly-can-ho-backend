package com.example.backend.service;

import com.example.backend.entity.Customer;
import com.example.backend.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // GET ALL
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // GET BY ID
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy khách hàng có ID: " + id)
                );
    }

    // CREATE
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // UPDATE
    public Customer updateCustomer(Long id, Customer customerData) {

        Customer customer = getCustomerById(id);

        customer.setName(customerData.getName());
        customer.setEmail(customerData.getEmail());
        customer.setPhone(customerData.getPhone());
        customer.setAddress(customerData.getAddress());

        return customerRepository.save(customer);
    }

    // DELETE
    public void deleteCustomer(Long id) {

        Customer customer = getCustomerById(id);

        customerRepository.delete(customer);
    }
}