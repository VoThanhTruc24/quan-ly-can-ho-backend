package com.example.backend.repository;

import com.example.backend.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository
        extends JpaRepository<Contract, Long> {

    // =========================
    // LẤY HỢP ĐỒNG THEO OWNER
    // =========================

    List<Contract> findByUserId(Long userId);

    // =========================
    // LẤY HỢP ĐỒNG THEO CUSTOMER
    // =========================

    List<Contract> findByCustomerId(Long customerId);

    // =========================
    // LẤY HỢP ĐỒNG THEO APARTMENT
    // =========================

    List<Contract> findByApartmentId(Long apartmentId);
}