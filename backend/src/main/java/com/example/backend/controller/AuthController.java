package com.example.backend.controller;
import com.example.backend.security.JwtService;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        Optional<User> optionalUser =
                userRepository.findByUsername(request.getUsername());

        // Không tìm thấy username
        if (optionalUser.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            false,
                            "Sai tên đăng nhập hoặc mật khẩu",
                            null,
                            null,
                            null,
                            null
                    ));
        }

        User user = optionalUser.get();

        // Sai password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            false,
                            "Sai tên đăng nhập hoặc mật khẩu",
                            null,
                            null,
                            null,
                            null
                    ));
        }

        // Tạo UserDetails để JwtService tạo token
        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build();

        // Tạo JWT
        String token = jwtService.generateToken(user.getUsername());

        // Login thành công
        return ResponseEntity.ok(
                new LoginResponse(
                        true,
                        "Đăng nhập thành công",
                        token,
                        user.getUsername(),
                        user.getFullName(),
                        user.getRole()
                )
        );
    }
}