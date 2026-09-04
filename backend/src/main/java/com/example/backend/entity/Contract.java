package com.example.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // USER ID
    // =========================
    // ID của User/Owner đang quản lý/tạo hợp đồng
    // =========================

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // =========================
    // CUSTOMER ID
    // =========================
    // ID khách hàng thuê căn hộ
    // =========================

    @Column(name = "customer_id")
    private Long customerId;

    // =========================
    // APARTMENT ID
    // =========================

    @Column(name = "apartment_id", nullable = false)
    private Long apartmentId;

    // =========================
    // CUSTOMER NAME
    // =========================
    // Giữ lại để tương thích frontend hiện tại
    // =========================

    @Column(name = "customer_name")
    private String customerName;

    // =========================
    // APARTMENT NAME
    // =========================

    @Column(name = "apartment_name")
    private String apartmentName;

    // =========================
    // DATE
    // =========================

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // =========================
    // RENT
    // =========================

    @Column(name = "monthly_rent")
    private Double monthlyRent;

    // =========================
    // STATUS
    // =========================

    @Column(name = "status")
    private String status;

    // =========================
    // CONSTRUCTOR
    // =========================

    public Contract() {
    }

    public Contract(
            Long userId,
            Long customerId,
            Long apartmentId,
            String customerName,
            String apartmentName,
            LocalDate startDate,
            LocalDate endDate,
            Double monthlyRent,
            String status
    ) {
        this.userId = userId;
        this.customerId = customerId;
        this.apartmentId = apartmentId;
        this.customerName = customerName;
        this.apartmentName = apartmentName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlyRent = monthlyRent;
        this.status = status;
    }

    // =========================
    // GETTER
    // =========================

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getApartmentName() {
        return apartmentName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Double getMonthlyRent() {
        return monthlyRent;
    }

    public String getStatus() {
        return status;
    }

    // =========================
    // SETTER
    // =========================

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setMonthlyRent(Double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}