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
    // DASHBOARD MẶC ĐỊNH
    // GET /api/dashboard
    // =====================================================

    public DashboardResponse getDashboard() {

        int currentYear =
                LocalDate.now().getYear();

        return getDashboard(currentYear);
    }


    // =====================================================
    // DASHBOARD THEO NĂM
    //
    // GET /api/dashboard/stats?year=2026
    // GET /api/dashboard/stats?year=2025
    // =====================================================

    public DashboardResponse getDashboard(
            Integer year
    ) {

        // =================================================
        // Nếu year null thì lấy năm hiện tại
        // =================================================

        int selectedYear =
                year != null
                        ? year
                        : LocalDate.now().getYear();


        // =================================================
        // 1. TỔNG SỐ KHÁCH HÀNG
        //
        // Giữ nguyên tổng toàn hệ thống
        // =================================================

        long totalCustomers =
                customerRepository.count();


        // =================================================
        // 2. TỔNG SỐ CĂN HỘ
        //
        // Giữ nguyên tổng toàn hệ thống
        // =================================================

        long totalApartments =
                apartmentRepository.count();


        // =================================================
        // 3. TỔNG SỐ HỢP ĐỒNG
        //
        // Giữ nguyên tổng toàn hệ thống
        // =================================================

        long totalContracts =
                contractRepository.count();


        // =================================================
        // 4. LẤY INVOICE THEO NĂM
        // =================================================

        List<Invoice> selectedYearInvoices =
                invoiceRepository.findByYear(
                        selectedYear
                );


        // =================================================
        // 5. TỔNG DOANH THU CỦA NĂM ĐANG CHỌN
        // =================================================

        BigDecimal totalRevenue =
                BigDecimal.ZERO;

        for (Invoice invoice :
                selectedYearInvoices) {

            if (invoice.getAmount() != null) {

                totalRevenue =
                        totalRevenue.add(
                                invoice.getAmount()
                        );
            }
        }


        // =================================================
        // 6. DOANH THU THEO 12 THÁNG
        // =================================================

        List<DashboardResponse.MonthlyRevenue>
                monthlyRevenue =
                new ArrayList<>();


        // -----------------------------------------------
        // Tạo đủ 12 tháng
        // -----------------------------------------------

        for (int month = 1;
             month <= 12;
             month++) {

            BigDecimal revenue =
                    BigDecimal.ZERO;


            // -------------------------------------------
            // Duyệt invoice của năm được chọn
            // -------------------------------------------

            for (Invoice invoice :
                    selectedYearInvoices) {

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


            // -------------------------------------------
            // Thêm tháng vào response
            // -------------------------------------------

            monthlyRevenue.add(
                    new DashboardResponse.MonthlyRevenue(
                            month,
                            revenue
                    )
            );
        }


        // =================================================
        // 7. TRẢ KẾT QUẢ
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