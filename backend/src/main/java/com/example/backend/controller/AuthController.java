package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // =====================================================
    // LOGIN
    // POST /api/auth/login
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        Optional<User> optionalUser =
                userRepository.findByUsername(
                        request.getUsername()
                );

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

        // Kiểm tra password
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

        // =================================================
        // TẠO JWT
        // =================================================

        String token =
                jwtService.generateToken(
                        user.getUsername()
                );

        // =================================================
        // LOGIN THÀNH CÔNG
        // =================================================

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


    // =====================================================
    // REGISTER OWNER
    // POST /api/auth/register-owner
    // =====================================================

    @PostMapping("/register-owner")
    public ResponseEntity<?> registerOwner(
            @RequestBody RegisterOwnerRequest request
    ) {

        // ---------------------------------------------
        // 1. Kiểm tra username
        // ---------------------------------------------

        if (request.getUsername() == null
                || request.getUsername().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Username không được để trống");
        }


        // ---------------------------------------------
        // 2. Kiểm tra password
        // ---------------------------------------------

        if (request.getPassword() == null
                || request.getPassword().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Password không được để trống");
        }


        // ---------------------------------------------
        // 3. Kiểm tra username đã tồn tại chưa
        // ---------------------------------------------

        if (userRepository.existsByUsername(
                request.getUsername()
        )) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username đã tồn tại");
        }


        // ---------------------------------------------
        // 4. Tạo User
        // ---------------------------------------------

        User user = new User();

        user.setUsername(
                request.getUsername()
        );

        // QUAN TRỌNG:
        // Không lưu password dạng text
        // Phải mã hóa bằng BCrypt

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setFullName(
                request.getFullName()
        );

        // Tài khoản này là OWNER

        user.setRole("OWNER");


        // ---------------------------------------------
        // 5. Lưu database
        // ---------------------------------------------

        User savedUser =
                userRepository.save(user);


        // ---------------------------------------------
        // 6. Trả kết quả
        // ---------------------------------------------

        return ResponseEntity.ok(
                new RegisterOwnerResponse(
                        true,
                        "Đăng ký tài khoản OWNER thành công",
                        savedUser.getUsername(),
                        savedUser.getFullName(),
                        savedUser.getRole()
                )
        );
    }


    // =====================================================
    // REQUEST REGISTER OWNER
    // =====================================================

    public static class RegisterOwnerRequest {

        private String username;

        private String password;

        private String fullName;


        public RegisterOwnerRequest() {
        }


        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }


        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }


        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }


    // =====================================================
    // RESPONSE REGISTER OWNER
    // =====================================================

    public static class RegisterOwnerResponse {

        private boolean success;

        private String message;

        private String username;

        private String fullName;

        private String role;


        public RegisterOwnerResponse(
                boolean success,
                String message,
                String username,
                String fullName,
                String role
        ) {
            this.success = success;
            this.message = message;
            this.username = username;
            this.fullName = fullName;
            this.role = role;
        }


        public boolean isSuccess() {
            return success;
        }


        public String getMessage() {
            return message;
        }


        public String getUsername() {
            return username;
        }


        public String getFullName() {
            return fullName;
        }


        public String getRole() {
            return role;
        }
    }
}