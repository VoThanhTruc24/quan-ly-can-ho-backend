package com.example.backend.repository;

import com.example.backend.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository
        extends JpaRepository<Contract, Long> {

    // Lấy tất cả hợp đồng của một Owner
    List<Contract> findByUserId(Long userId);
}