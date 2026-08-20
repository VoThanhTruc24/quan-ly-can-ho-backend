package com.example.backend.controller;

import com.example.backend.dto.ContractRequest;
import com.example.backend.entity.Contract;
import com.example.backend.service.ContractService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin(origins = "http://localhost:5173")
public class ContractController {

    private final ContractService contractService;

    public ContractController(
            ContractService contractService
    ) {
        this.contractService = contractService;
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Contract>> getAllContracts() {

        return ResponseEntity.ok(
                contractService.getAllContracts()
        );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContractById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                contractService.getContractById(id)
        );
    }

    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    public ResponseEntity<Contract> createContract(
            @RequestBody ContractRequest request
    ) {

        return ResponseEntity.ok(
                contractService.createContract(request)
        );
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<Contract> updateContract(
            @PathVariable Long id,
            @RequestBody ContractRequest request
    ) {

        return ResponseEntity.ok(
                contractService.updateContract(id, request)
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContract(
            @PathVariable Long id
    ) {

        try {

            contractService.deleteContract(id);

            return ResponseEntity
                    .noContent()
                    .build();

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }
}