
package com.example.backend.security;

import com.example.backend.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;

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

        final String authHeader = request.getHeader("Authorization");

        // Không có Authorization header: tiếp tục để Spring Security
        // quyết định request có được phép truy cập hay không.
        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Chỉ chấp nhận Bearer token.
        if (!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy token và loại bỏ khoảng trắng ở hai đầu.
        final String jwt = authHeader.substring(7).trim();

        // Token rỗng hoặc chứa khoảng trắng bên trong là không hợp lệ.
        if (jwt.isEmpty() || jwt.chars().anyMatch(Character::isWhitespace)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Lấy username từ JWT.
            final String username = jwtService.extractUsername(jwt);

            // Chỉ xác thực khi SecurityContext chưa có Authentication.
            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                // Kiểm tra token còn hợp lệ với tài khoản hiện tại.
                if (jwtService.isTokenValid(jwt, userDetails)) {

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

                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }

        } catch (JwtException | IllegalArgumentException |
                 UsernameNotFoundException e) {

            // JWT không hợp lệ hoặc tài khoản không tồn tại.
            // Không ghi token hay thông tin nhạy cảm vào log.
            SecurityContextHolder.clearContext();
        }

        // Tiếp tục filter chain.
        // Endpoint cần đăng nhập sẽ được Spring Security chặn
        // nếu không có Authentication hợp lệ.
        filterChain.doFilter(request, response);
    }
}
