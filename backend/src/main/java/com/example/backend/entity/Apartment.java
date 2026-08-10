package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "apartment")
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double area;
    private String status;

    @ManyToOne
    @JoinColumn(name = "floor_id")
    private Floor floor;

    // ===== GETTER =====
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getArea() {
        return area;
    }

    public String getStatus() {
        return status;
    }

    public Floor getFloor() {
        return floor;
    }

    // ===== SETTER =====
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }
}