package com.example.backend.repository;

import com.example.backend.entity.Invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository
        extends JpaRepository<Invoice, Long> {

    // ==========================================
    // LẤY HÓA ĐƠN THEO NĂM
    // ==========================================

    List<Invoice> findByYear(Integer year);


    // ==========================================
    // KIỂM TRA CONTRACT ĐÃ CÓ HÓA ĐƠN
    // ==========================================

    boolean existsByContractId(Long contractId);


    // ==========================================
    // LẤY HÓA ĐƠN THEO CONTRACT
    // ==========================================

    List<Invoice> findByContractId(Long contractId);
}