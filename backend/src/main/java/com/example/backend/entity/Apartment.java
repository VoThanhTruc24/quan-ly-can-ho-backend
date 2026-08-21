package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "apartment")
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // THÔNG TIN CĂN HỘ
    // =========================

    private String name;

    private Double area;

    private String status;

    // =========================
    // FLOOR
    // =========================

    @ManyToOne
    @JoinColumn(name = "floor_id")
    private Floor floor;

    // =========================
    // OWNER
    // =========================

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    // =========================
    // GETTER
    // =========================

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

    public User getOwner() {
        return owner;
    }

    // =========================
    // SETTER
    // =========================

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

    public void setOwner(User owner) {
        this.owner = owner;
    }
}