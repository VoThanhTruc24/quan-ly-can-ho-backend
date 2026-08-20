package com.example.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice")
public class Invoice {

    // =========================
    // ID
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // CONTRACT ID
    // =========================

    @Column(name = "contract_id")
    private Long contractId;

    // =========================
    // MONTH
    // =========================

    @Column(name = "month")
    private Integer month;

    // =========================
    // YEAR
    // =========================

    @Column(name = "year")
    private Integer year;

    // =========================
    // AMOUNT
    // =========================

    @Column(name = "amount")
    private BigDecimal amount;

    // =========================
    // DUE DATE
    // =========================

    @Column(name = "due_date")
    private LocalDate dueDate;

    // =========================
    // STATUS
    // =========================

    @Column(name = "status")
    private String status;

    // =========================
    // CONSTRUCTOR
    // =========================

    public Invoice() {
    }

    // =========================
    // GETTER / SETTER
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}