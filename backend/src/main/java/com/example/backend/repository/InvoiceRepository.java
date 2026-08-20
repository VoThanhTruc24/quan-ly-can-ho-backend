package com.example.backend.repository;

import com.example.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository
        extends JpaRepository<Invoice, Long> {

    List<Invoice> findByYear(Integer year);

    boolean existsByContractId(Long contractId);
}