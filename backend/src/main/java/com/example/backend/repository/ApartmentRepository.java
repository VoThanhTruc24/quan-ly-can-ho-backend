package com.example.backend.repository;

import com.example.backend.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApartmentRepository
        extends JpaRepository<Apartment, Long> {

    // =========================
    // TÌM THEO TÊN
    // =========================

    Optional<Apartment> findByName(String name);

    // =========================
    // FILTER STATUS
    // =========================

    List<Apartment> findByStatus(String status);

    // =========================
    // FILTER FLOOR
    // =========================

    List<Apartment> findByFloor_Id(Long floorId);

    // =========================
    // FILTER STATUS + FLOOR
    // =========================

    List<Apartment> findByStatusAndFloor_Id(
            String status,
            Long floorId
    );

    // =========================
    // OWNER
    // =========================

    List<Apartment> findByOwner_Id(Long ownerId);
}