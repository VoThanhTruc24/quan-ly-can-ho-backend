package com.example.backend.config;

import com.example.backend.security.JwtAuthenticationFilter;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // ==============================
    // PASSWORD ENCODER
    // ==============================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // ==============================
    // CORS CONFIGURATION
    // ==============================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Frontend React / Vite
        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        // Các HTTP method được phép
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        // Cho phép các header
        configuration.setAllowedHeaders(
                List.of("*")
        );

        // Cho phép gửi Authorization / Cookie nếu cần
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    // ==============================
    // SECURITY
    // ==============================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // REST API không dùng CSRF
                .csrf(csrf -> csrf.disable())

                // Bật CORS
                .cors(cors -> {})

                // JWT không sử dụng Session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // ==============================
                // PHÂN QUYỀN
                // ==============================

                .authorizeHttpRequests(auth -> auth

                        // Cho phép OPTIONS - CORS preflight
                        .requestMatchers(
                                org.springframework.http.HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // Cho phép Spring xử lý error
                        .requestMatchers("/error").permitAll()

                        // Login không cần JWT
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // Blocks
                        .requestMatchers(
                                "/api/blocks/**"
                        ).permitAll()

                        // Floors
                        .requestMatchers(
                                "/api/floors/**"
                        ).permitAll()

                        // Apartments
                        .requestMatchers(
                                "/api/apartments/**"
                        ).permitAll()

                        // ==============================
                        // CUSTOMER
                        // ==============================

                        .requestMatchers(
                                "/api/customers/**"
                        ).permitAll()

                        // ==============================
                        // API ADMIN
                        // ==============================

                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // Các API còn lại cần đăng nhập
                        .anyRequest().authenticated()
                )

                // ==============================
                // JWT FILTER
                // ==============================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}