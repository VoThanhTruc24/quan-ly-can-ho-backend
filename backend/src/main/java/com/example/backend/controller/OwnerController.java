package com.example.backend.controller;

import com.example.backend.dto.OwnerRequest;
import com.example.backend.entity.Apartment;
import com.example.backend.entity.User;
import com.example.backend.repository.ApartmentRepository;
import com.example.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/owners")
@CrossOrigin(origins = "http://localhost:5173")
public class OwnerController {

    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final PasswordEncoder passwordEncoder;

    public OwnerController(
            UserRepository userRepository,
            ApartmentRepository apartmentRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.apartmentRepository = apartmentRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // ==========================================
    // GET ALL OWNER
    // ==========================================

    @GetMapping
    public ResponseEntity<List<User>> getAllOwners() {

        List<User> owners =
                userRepository.findByRole("OWNER");

        return ResponseEntity.ok(owners);
    }


    // ==========================================
    // CREATE OWNER
    // ==========================================

    @PostMapping
    public ResponseEntity<?> createOwner(
            @RequestBody OwnerRequest request
    ) {

        // -----------------------------
        // Kiểm tra username
        // -----------------------------

        if (request.getUsername() == null
                || request.getUsername().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Username không được để trống");
        }


        // -----------------------------
        // Kiểm tra password
        // -----------------------------

        if (request.getPassword() == null
                || request.getPassword().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Mật khẩu không được để trống");
        }


        // -----------------------------
        // Kiểm tra username tồn tại
        // -----------------------------

        if (userRepository.existsByUsername(
                request.getUsername()
        )) {

            return ResponseEntity
                    .badRequest()
                    .body("Username đã tồn tại");
        }


        // -----------------------------
        // Tạo User
        // -----------------------------

        User user = new User();

        user.setUsername(
                request.getUsername().trim()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setFullName(
                request.getFullName()
        );

        user.setRole("OWNER");


        // -----------------------------
        // Lưu database
        // -----------------------------

        User savedUser =
                userRepository.save(user);


        return ResponseEntity.ok(savedUser);
    }


    // ==========================================
    // GÁN OWNER CHO CĂN HỘ
    // ==========================================

    @PutMapping("/{ownerId}/apartments/{apartmentId}")
    public ResponseEntity<?> assignApartment(
            @PathVariable Long ownerId,
            @PathVariable Long apartmentId
    ) {

        // -----------------------------
        // 1. Tìm Owner
        // -----------------------------

        User owner = userRepository
                .findById(ownerId.intValue())
                .orElse(null);

        if (owner == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Không tìm thấy Owner có ID: " + ownerId);
        }


        // -----------------------------
        // 2. Kiểm tra role
        // -----------------------------

        if (!"OWNER".equals(owner.getRole())) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "User có ID "
                                    + ownerId
                                    + " không phải OWNER"
                    );
        }


        // -----------------------------
        // 3. Tìm căn hộ
        // -----------------------------

        Apartment apartment =
                apartmentRepository
                        .findById(apartmentId)
                        .orElse(null);

        if (apartment == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Không tìm thấy căn hộ có ID: "
                                    + apartmentId
                    );
        }


        // -----------------------------
        // 4. Gán Owner
        // -----------------------------

        apartment.setOwner(owner);


        // -----------------------------
        // 5. Lưu
        // -----------------------------

        Apartment savedApartment =
                apartmentRepository.save(apartment);


        return ResponseEntity.ok(savedApartment);
    }


    // ==========================================
    // XEM CĂN HỘ CỦA OWNER
    // ==========================================

    @GetMapping("/{ownerId}/apartments")
    public ResponseEntity<?> getOwnerApartments(
            @PathVariable Long ownerId
    ) {

        // Kiểm tra Owner tồn tại

        User owner = userRepository
                .findById(ownerId.intValue())
                .orElse(null);

        if (owner == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Không tìm thấy Owner có ID: "
                                    + ownerId
                    );
        }


        // Lấy căn hộ của Owner

        List<Apartment> apartments =
                apartmentRepository
                        .findByOwner_Id(ownerId);

        return ResponseEntity.ok(apartments);
    }


    // ==========================================
    // BỎ GÁN OWNER KHỎI CĂN HỘ
    // ==========================================

    @DeleteMapping("/{ownerId}/apartments/{apartmentId}")
    public ResponseEntity<?> unassignApartment(
            @PathVariable Long ownerId,
            @PathVariable Long apartmentId
    ) {

        Apartment apartment =
                apartmentRepository
                        .findById(apartmentId)
                        .orElse(null);

        if (apartment == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Không tìm thấy căn hộ có ID: "
                                    + apartmentId
                    );
        }


        // Nếu căn hộ đang thuộc Owner này

        if (apartment.getOwner() != null
                && apartment.getOwner()
                .getId()
                .longValue() == ownerId) {

            apartment.setOwner(null);

            apartmentRepository.save(apartment);

            return ResponseEntity.ok(
                    "Đã bỏ gán Owner khỏi căn hộ"
            );
        }


        return ResponseEntity
                .badRequest()
                .body(
                        "Căn hộ không thuộc Owner này"
                );
    }
}