package com.example.backend.controller;

import com.example.backend.dto.DashboardResponse;
import com.example.backend.service.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    // GET /api/dashboard
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {

        return ResponseEntity.ok(
                dashboardService.getDashboard()
        );
    }

    // GET /api/dashboard/stats?year=2026
    @GetMapping("/stats")
    public ResponseEntity<DashboardResponse> getDashboardStats(
            @RequestParam(required = false) Integer year
    ) {

        return ResponseEntity.ok(
                dashboardService.getDashboard(year)
        );
    }
}