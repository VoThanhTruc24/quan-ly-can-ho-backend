package com.example.backend.security;

import com.example.backend.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ==============================
        // DEBUG
        // ==============================

        System.out.println("====================================");
        System.out.println("JWT FILTER");
        System.out.println("METHOD: " + request.getMethod());
        System.out.println("URL: " + request.getRequestURI());

        final String authHeader =
                request.getHeader("Authorization");

        System.out.println(
                "AUTH HEADER: " + authHeader
        );

        // ==============================
        // KHÔNG CÓ TOKEN
        // ==============================

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            System.out.println(
                    "NO JWT -> CONTINUE"
            );

            filterChain.doFilter(request, response);
            return;
        }

        // ==============================
        // LẤY JWT
        // ==============================

        final String jwt =
                authHeader.substring(7);

        String username;

        // ==============================
        // GIẢI MÃ JWT
        // ==============================

        try {

            username =
                    jwtService.extractUsername(jwt);

            System.out.println(
                    "JWT USERNAME: " + username
            );

        } catch (Exception e) {

            System.out.println(
                    "JWT INVALID: " + e.getMessage()
            );

            filterChain.doFilter(request, response);
            return;
        }

        // ==============================
        // XÁC THỰC USER
        // ==============================

        if (username != null
                && SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(username);

            System.out.println(
                    "USER AUTHORITIES: "
                            + userDetails.getAuthorities()
            );

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authToken);

            System.out.println(
                    "AUTHENTICATION SET SUCCESS"
            );
        }

        // ==============================
        // TIẾP TỤC REQUEST
        // ==============================

        filterChain.doFilter(request, response);
    }
}