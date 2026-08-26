package com.example.backend.controller;

import com.example.backend.entity.Apartment;
import com.example.backend.entity.Contract;
import com.example.backend.entity.Invoice;
import com.example.backend.entity.User;

import com.example.backend.repository.ApartmentRepository;
import com.example.backend.repository.ContractRepository;
import com.example.backend.repository.InvoiceRepository;
import com.example.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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


    // ==========================================
    // LẤY OWNER ĐANG ĐĂNG NHẬP
    // ==========================================

    private User getCurrentOwner() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().equals("anonymousUser")) {

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

        if (!"OWNER".equals(owner.getRole())) {

            throw new RuntimeException(
                    "Tài khoản không phải Owner"
            );
        }

        return owner;
    }


    // ==========================================
    // GET /api/owner/me
    // ==========================================

    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo() {

        User owner =
                getCurrentOwner();

        return ResponseEntity.ok(owner);
    }


    // ==========================================
    // GET /api/owner/me/apartments
    // ==========================================

    @GetMapping("/me/apartments")
    public ResponseEntity<List<Apartment>> getMyApartments() {

        User owner =
                getCurrentOwner();

        List<Apartment> apartments =
                apartmentRepository
                        .findByOwner_Id(
                                owner.getId().longValue()
                        );

        return ResponseEntity.ok(apartments);
    }


    // ==========================================
    // GET /api/owner/me/contracts
    // ==========================================

    @GetMapping("/me/contracts")
    public ResponseEntity<List<Contract>> getMyContracts() {

        User owner =
                getCurrentOwner();

        List<Contract> contracts =
                contractRepository
                        .findByUserId(
                                owner.getId().longValue()
                        );

        return ResponseEntity.ok(contracts);
    }


    // ==========================================
    // GET /api/owner/me/invoices
    // ==========================================

    @GetMapping("/me/invoices")
    public ResponseEntity<List<Invoice>> getMyInvoices() {

        User owner =
                getCurrentOwner();


        // ------------------------------------------
        // Lấy contract của Owner
        // ------------------------------------------

        List<Contract> contracts =
                contractRepository.findByUserId(
                        owner.getId().longValue()
                );


        // ------------------------------------------
        // Gom invoice
        // ------------------------------------------

        List<Invoice> invoices =
                new ArrayList<>();


        for (Contract contract : contracts) {

            if (contract.getId() == null) {
                continue;
            }

            List<Invoice> contractInvoices =
                    invoiceRepository.findByContractId(
                            contract.getId()
                    );

            invoices.addAll(
                    contractInvoices
            );
        }


        return ResponseEntity.ok(invoices);
    }
}