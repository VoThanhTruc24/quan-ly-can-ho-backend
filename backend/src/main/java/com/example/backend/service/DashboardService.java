package com.example.backend.service;

import com.example.backend.dto.DashboardResponse;
import com.example.backend.entity.Invoice;
import com.example.backend.repository.ApartmentRepository;
import com.example.backend.repository.ContractRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.InvoiceRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final ApartmentRepository apartmentRepository;
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

    public DashboardService(
            CustomerRepository customerRepository,
            ApartmentRepository apartmentRepository,
            ContractRepository contractRepository,
            InvoiceRepository invoiceRepository
    ) {
        this.customerRepository = customerRepository;
        this.apartmentRepository = apartmentRepository;
        this.contractRepository = contractRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public DashboardResponse getDashboard() {

        // ==========================================
        // 1. TỔNG SỐ KHÁCH HÀNG
        // ==========================================

        long totalCustomers =
                customerRepository.count();


        // ==========================================
        // 2. TỔNG SỐ CĂN HỘ
        // ==========================================

        long totalApartments =
                apartmentRepository.count();


        // ==========================================
        // 3. TỔNG SỐ HỢP ĐỒNG
        // ==========================================

        long totalContracts =
                contractRepository.count();


        // ==========================================
        // 4. DOANH THU
        // ==========================================

        List<Invoice> invoices =
                invoiceRepository.findAll();

        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Invoice invoice : invoices) {

            if (invoice.getAmount() != null) {

                totalRevenue =
                        totalRevenue.add(
                                invoice.getAmount()
                        );
            }
        }


        // ==========================================
        // 5. DOANH THU THEO THÁNG - NĂM HIỆN TẠI
        // ==========================================

        int currentYear =
                LocalDate.now().getYear();

        List<Invoice> currentYearInvoices =
                invoiceRepository.findByYear(currentYear);

        List<DashboardResponse.MonthlyRevenue>
                monthlyRevenue = new ArrayList<>();


        // Tạo đủ 12 tháng
        for (int month = 1; month <= 12; month++) {

            BigDecimal revenue =
                    BigDecimal.ZERO;

            for (Invoice invoice : currentYearInvoices) {

                if (
                        invoice.getMonth() != null
                                && invoice.getMonth() == month
                                && invoice.getAmount() != null
                ) {

                    revenue =
                            revenue.add(
                                    invoice.getAmount()
                            );
                }
            }

            monthlyRevenue.add(
                    new DashboardResponse.MonthlyRevenue(
                            month,
                            revenue
                    )
            );
        }


        // ==========================================
        // RETURN
        // ==========================================

        return new DashboardResponse(
                totalCustomers,
                totalApartments,
                totalContracts,
                totalRevenue,
                monthlyRevenue
        );
    }
}