package com.example.backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public Map<String, Object> test(Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        response.put("message", "JWT hợp lệ");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities());

        return response;
    }

    @GetMapping("/admin/test")
    public Map<String, Object> adminTest(Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Bạn có quyền ADMIN");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities());

        return response;
    }
}