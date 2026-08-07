package com.example.backend.config;

import com.example.backend.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // REST API nên tắt CSRF
                .csrf(csrf -> csrf.disable())

                // Cho phép CORS
                .cors(cors -> {})

                // JWT không sử dụng HTTP Session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Phân quyền
                .authorizeHttpRequests(auth -> auth

                        // Login không cần token
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/blocks/**").permitAll()
                        .requestMatchers("/api/floors/**").permitAll()

                        // API admin
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Các API khác phải đăng nhập
                        .anyRequest().authenticated()
                )

                // JWT Filter chạy trước filter username/password
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}