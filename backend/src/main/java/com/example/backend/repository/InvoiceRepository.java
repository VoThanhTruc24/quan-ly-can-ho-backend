package com.example.backend.repository;

import com.example.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository
        extends JpaRepository<Invoice, Long> {

    List<Invoice> findByYear(Integer year);
}