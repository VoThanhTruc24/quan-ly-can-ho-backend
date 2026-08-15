package com.example.backend.controller;

import com.example.backend.entity.Customer;
import com.example.backend.service.CustomerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // =========================
    // GET ALL
    // =========================

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {

        return ResponseEntity.ok(
                customerService.getAllCustomers()
        );
    }

    // =========================
    // GET BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                customerService.getCustomerById(id)
        );
    }

    // =========================
    // CREATE
    // =========================

    @PostMapping
    public ResponseEntity<Customer> createCustomer(
            @RequestBody Customer customer
    ) {

        return ResponseEntity.ok(
                customerService.createCustomer(customer)
        );
    }

    // =========================
    // UPDATE
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long id,
            @RequestBody Customer customer
    ) {

        return ResponseEntity.ok(
                customerService.updateCustomer(id, customer)
        );
    }

    // =========================
    // DELETE
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable Long id
    ) {

        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }
}