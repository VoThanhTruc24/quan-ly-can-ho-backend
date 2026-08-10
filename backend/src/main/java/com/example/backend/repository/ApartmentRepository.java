package com.example.backend.repository;

import com.example.backend.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    // filter theo status
    List<Apartment> findByStatus(String status);

    // filter theo floor
    List<Apartment> findByFloor_Id(Long floorId);

    // filter cả 2
    List<Apartment> findByStatusAndFloor_Id(String status, Long floorId);
}