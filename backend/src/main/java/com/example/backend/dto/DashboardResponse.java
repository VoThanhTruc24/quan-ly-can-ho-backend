package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private long totalCustomers;
    private long totalApartments;
    private long totalContracts;
    private BigDecimal totalRevenue;

    private List<MonthlyRevenue> monthlyRevenue;

    public DashboardResponse() {
    }

    public DashboardResponse(
            long totalCustomers,
            long totalApartments,
            long totalContracts,
            BigDecimal totalRevenue,
            List<MonthlyRevenue> monthlyRevenue
    ) {
        this.totalCustomers = totalCustomers;
        this.totalApartments = totalApartments;
        this.totalContracts = totalContracts;
        this.totalRevenue = totalRevenue;
        this.monthlyRevenue = monthlyRevenue;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalApartments() {
        return totalApartments;
    }

    public void setTotalApartments(long totalApartments) {
        this.totalApartments = totalApartments;
    }

    public long getTotalContracts() {
        return totalContracts;
    }

    public void setTotalContracts(long totalContracts) {
        this.totalContracts = totalContracts;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public List<MonthlyRevenue> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(List<MonthlyRevenue> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    // ==========================================
    // MONTHLY REVENUE
    // ==========================================

    public static class MonthlyRevenue {

        private int month;
        private BigDecimal revenue;

        public MonthlyRevenue() {
        }

        public MonthlyRevenue(
                int month,
                BigDecimal revenue
        ) {
            this.month = month;
            this.revenue = revenue;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        public void setRevenue(BigDecimal revenue) {
            this.revenue = revenue;
        }
    }
}