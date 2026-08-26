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
        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }


    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // =====================================================
    // CORS
    // =====================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );


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


        configuration.setAllowedHeaders(
                List.of("*")
        );


        configuration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }


    // =====================================================
    // SECURITY
    // =====================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {


        http

                // =================================================
                // CSRF
                // =================================================

                .csrf(
                        csrf -> csrf.disable()
                )


                // =================================================
                // CORS
                // =================================================

                .cors(
                        cors -> {}
                )


                // =================================================
                // SESSION
                // =================================================

                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )


                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(
                        auth -> auth


                                // =================================
                                // OPTIONS
                                // =================================

                                .requestMatchers(
                                        org.springframework.http.HttpMethod.OPTIONS,
                                        "/**"
                                ).permitAll()


                                // =================================
                                // ERROR
                                // =================================

                                .requestMatchers(
                                        "/error"
                                ).permitAll()


                                // =================================
                                // AUTH
                                // =================================

                                .requestMatchers(
                                        "/api/auth/**"
                                ).permitAll()


                                // =================================
                                // PUBLIC API
                                // =================================

                                .requestMatchers(
                                        "/api/blocks/**"
                                ).permitAll()


                                .requestMatchers(
                                        "/api/floors/**"
                                ).permitAll()


                                .requestMatchers(
                                        "/api/apartments/**"
                                ).permitAll()


                                .requestMatchers(
                                        "/api/customers/**"
                                ).permitAll()


                                .requestMatchers(
                                        "/api/contracts/**"
                                ).permitAll()


                                // =================================
                                // ADMIN
                                //
                                // Chấp nhận:
                                // ADMIN
                                // ROLE_ADMIN
                                // =================================

                                .requestMatchers(
                                        "/api/admin/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "ROLE_ADMIN"
                                )


                                // =================================
                                // OWNER
                                //
                                // Chấp nhận:
                                // OWNER
                                // ROLE_OWNER
                                // =================================

                                .requestMatchers(
                                        "/api/owner/**"
                                )
                                .hasAnyAuthority(
                                        "OWNER",
                                        "ROLE_OWNER"
                                )


                                // =================================
                                // DASHBOARD
                                // =================================

                                .requestMatchers(
                                        "/api/dashboard/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "ROLE_ADMIN"
                                )


                                // =================================
                                // CÒN LẠI
                                // =================================

                                .anyRequest()
                                .authenticated()
                )


                // =================================================
                // JWT FILTER
                // =================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}