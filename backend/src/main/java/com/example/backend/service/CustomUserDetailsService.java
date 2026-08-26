package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Không tìm thấy user: " + username
                        )
                );

        String role = user.getRole();

        if (role == null || role.trim().isEmpty()) {
            throw new UsernameNotFoundException(
                    "User chưa có role: " + username
            );
        }

        role = role.trim().toUpperCase();

        System.out.println(
                "===================================="
        );
        System.out.println(
                "LOGIN USER: " + user.getUsername()
        );
        System.out.println(
                "DATABASE ROLE: " + role
        );
        System.out.println(
                "SPRING AUTHORITY: ROLE_" + role
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority(
                                "ROLE_" + role
                        )
                )
        );
    }
}