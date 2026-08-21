package com.example.backend.repository;

import com.example.backend.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository
        extends JpaRepository<Contract, Long> {

    // =====================================================
    // LẤY TẤT CẢ HỢP ĐỒNG
    // Dùng cho ADMIN
    // =====================================================

    List<Contract> findAll();

    // =====================================================
    // LẤY HỢP ĐỒNG THEO USER ID
    // Dùng cho OWNER
    // =====================================================

    List<Contract> findByUserId(Long userId);
}