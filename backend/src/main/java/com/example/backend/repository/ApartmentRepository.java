package com.example.backend.repository;

import com.example.backend.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApartmentRepository
        extends JpaRepository<Apartment, Long> {

    Optional<Apartment> findByName(String name);

    List<Apartment> findByStatus(String status);

    List<Apartment> findByFloor_Id(Long floorId);

    List<Apartment> findByStatusAndFloor_Id(
            String status,
            Long floorId
    );

    // ==============================
    // LẤY CĂN HỘ THEO OWNER
    // ==============================

    List<Apartment> findByOwner_Id(Long ownerId);
}