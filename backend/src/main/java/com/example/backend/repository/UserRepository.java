package com.example.backend.repository;

import com.example.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Integer> {

    // Tìm user theo username
    Optional<User> findByUsername(String username);

    // Kiểm tra username đã tồn tại chưa
    boolean existsByUsername(String username);

    // Lấy danh sách user theo role
    List<User> findByRole(String role);
}