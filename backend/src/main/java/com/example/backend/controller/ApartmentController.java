package com.example.backend.controller;

import com.example.backend.dto.ApartmentRequest;
import com.example.backend.entity.Apartment;
import com.example.backend.entity.Floor;
import com.example.backend.repository.ApartmentRepository;
import com.example.backend.repository.FloorRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apartments")
@CrossOrigin(origins = "http://localhost:5173")
public class ApartmentController {

    private final ApartmentRepository apartmentRepository;
    private final FloorRepository floorRepository;

    public ApartmentController(ApartmentRepository apartmentRepository,
                               FloorRepository floorRepository) {
        this.apartmentRepository = apartmentRepository;
        this.floorRepository = floorRepository;
    }

    // CREATE
    @PostMapping
    public Apartment create(@RequestBody ApartmentRequest request) {

        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new RuntimeException("Floor not found"));

        Apartment apartment = new Apartment();
        apartment.setName(request.getName());
        apartment.setArea(request.getArea());
        apartment.setStatus(request.getStatus());
        apartment.setFloor(floor);

        return apartmentRepository.save(apartment);
    }

    // GET ALL + FILTER
    @GetMapping
    public List<Apartment> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long floorId
    ) {

        if (status != null && floorId != null) {
            return apartmentRepository.findByStatusAndFloor_Id(status, floorId);
        }

        if (status != null) {
            return apartmentRepository.findByStatus(status);
        }

        if (floorId != null) {
            return apartmentRepository.findByFloor_Id(floorId);
        }

        return apartmentRepository.findAll();
    }

    // UPDATE
    @PutMapping("/{id}")
    public Apartment update(@PathVariable Long id,
                            @RequestBody ApartmentRequest request) {

        Apartment apartment = apartmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new RuntimeException("Floor not found"));

        apartment.setName(request.getName());
        apartment.setArea(request.getArea());
        apartment.setStatus(request.getStatus());
        apartment.setFloor(floor);

        return apartmentRepository.save(apartment);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        apartmentRepository.deleteById(id);
        return "Deleted " + id;
    }
}