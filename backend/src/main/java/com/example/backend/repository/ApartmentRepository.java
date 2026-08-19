package com.example.backend.repository;

import com.example.backend.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    // Tìm căn hộ theo tên
    Optional<Apartment> findByName(String name);

    // Filter theo status
    List<Apartment> findByStatus(String status);

    // Filter theo floor
    List<Apartment> findByFloor_Id(Long floorId);

    // Filter cả status và floor
    List<Apartment> findByStatusAndFloor_Id(
            String status,
            Long floorId
    );
}