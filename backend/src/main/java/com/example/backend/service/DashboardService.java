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


    // =====================================================
    // DASHBOARD - NĂM HIỆN TẠI
    // =====================================================

    public DashboardResponse getDashboard() {

        int currentYear =
                LocalDate.now().getYear();

        return getDashboard(currentYear);
    }


    // =====================================================
    // DASHBOARD - THEO NĂM
    // =====================================================

    public DashboardResponse getDashboard(
            Integer selectedYear
    ) {

        // =================================================
        // 1. KIỂM TRA NĂM
        // =================================================

        int year =
                selectedYear != null
                        ? selectedYear
                        : LocalDate.now().getYear();


        // =================================================
        // 2. TỔNG KHÁCH HÀNG
        // =================================================

        long totalCustomers =
                customerRepository.count();


        // =================================================
        // 3. TỔNG CĂN HỘ
        // =================================================

        long totalApartments =
                apartmentRepository.count();


        // =================================================
        // 4. TỔNG HỢP ĐỒNG
        // =================================================

        long totalContracts =
                contractRepository.count();


        // =================================================
        // 5. LẤY HÓA ĐƠN THEO NĂM
        // =================================================

        List<Invoice> invoices =
                invoiceRepository.findByYear(year);


        // =================================================
        // 6. DOANH THU
        //
        // CHỈ TÍNH HÓA ĐƠN ĐÃ THANH TOÁN
        // =================================================

        BigDecimal totalRevenue =
                BigDecimal.ZERO;


        for (Invoice invoice : invoices) {

            if (
                    invoice.getAmount() != null
                            &&
                            "PAID".equalsIgnoreCase(
                                    invoice.getStatus()
                            )
            ) {

                totalRevenue =
                        totalRevenue.add(
                                invoice.getAmount()
                        );
            }
        }


        // =================================================
        // 7. DOANH THU THEO THÁNG
        //
        // ĐỦ 12 THÁNG
        // CHỈ TÍNH PAID
        // =================================================

        List<DashboardResponse.MonthlyRevenue>
                monthlyRevenue =
                new ArrayList<>();


        for (
                int month = 1;
                month <= 12;
                month++
        ) {

            BigDecimal monthlyTotal =
                    BigDecimal.ZERO;


            for (Invoice invoice : invoices) {

                if (
                        invoice.getMonth() != null
                                &&
                                invoice.getMonth() == month
                                &&
                                invoice.getAmount() != null
                                &&
                                "PAID".equalsIgnoreCase(
                                        invoice.getStatus()
                                )
                ) {

                    monthlyTotal =
                            monthlyTotal.add(
                                    invoice.getAmount()
                            );
                }
            }


            monthlyRevenue.add(
                    new DashboardResponse.MonthlyRevenue(
                            month,
                            monthlyTotal
                    )
            );
        }


        // =================================================
        // DEBUG
        // =================================================

        System.out.println(
                "======================================"
        );

        System.out.println(
                "DASHBOARD YEAR: " + year
        );

        System.out.println(
                "TOTAL CUSTOMERS: "
                        + totalCustomers
        );

        System.out.println(
                "TOTAL APARTMENTS: "
                        + totalApartments
        );

        System.out.println(
                "TOTAL CONTRACTS: "
                        + totalContracts
        );

        System.out.println(
                "PAID REVENUE: "
                        + totalRevenue
        );

        System.out.println(
                "======================================"
        );


        // =================================================
        // 8. RETURN
        // =================================================

        return new DashboardResponse(
                totalCustomers,
                totalApartments,
                totalContracts,
                totalRevenue,
                monthlyRevenue
        );
    }
}