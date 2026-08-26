package com.example.backend.controller;

import com.example.backend.entity.Apartment;
import com.example.backend.entity.User;
import com.example.backend.repository.ApartmentRepository;
import com.example.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/owners")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminOwnerController {

    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminOwnerController(
            UserRepository userRepository,
            ApartmentRepository apartmentRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.apartmentRepository = apartmentRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // =========================================================
    // 1. LẤY DANH SÁCH OWNER
    //
    // GET /api/admin/owners
    // =========================================================

    @GetMapping
    public ResponseEntity<List<User>> getAllOwners() {

        List<User> owners =
                userRepository.findByRole("OWNER");

        return ResponseEntity.ok(owners);
    }


    // =========================================================
    // 2. TẠO OWNER
    //
    // POST /api/admin/owners
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createOwner(
            @RequestBody CreateOwnerRequest request
    ) {

        // -----------------------------------------------------
        // Kiểm tra username
        // -----------------------------------------------------

        if (request.getUsername() == null
                || request.getUsername().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Username không được để trống"
                            )
                    );
        }


        // -----------------------------------------------------
        // Kiểm tra password
        // -----------------------------------------------------

        if (request.getPassword() == null
                || request.getPassword().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Mật khẩu không được để trống"
                            )
                    );
        }


        // -----------------------------------------------------
        // Kiểm tra họ tên
        // -----------------------------------------------------

        if (request.getFullName() == null
                || request.getFullName().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Họ và tên không được để trống"
                            )
                    );
        }


        String username =
                request.getUsername().trim();


        // -----------------------------------------------------
        // Kiểm tra username trùng
        // -----------------------------------------------------

        if (userRepository.existsByUsername(username)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "message",
                                    "Username đã tồn tại"
                            )
                    );
        }


        // -----------------------------------------------------
        // Tạo Owner
        // -----------------------------------------------------

        User owner = new User();

        owner.setUsername(username);

        owner.setFullName(
                request.getFullName().trim()
        );

        owner.setRole("OWNER");


        // -----------------------------------------------------
        // MÃ HÓA PASSWORD
        // -----------------------------------------------------

        owner.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );


        // -----------------------------------------------------
        // Lưu database
        // -----------------------------------------------------

        User savedOwner =
                userRepository.save(owner);


        // -----------------------------------------------------
        // Không trả password ra frontend
        // -----------------------------------------------------

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        Map.of(
                                "id", savedOwner.getId(),
                                "username", savedOwner.getUsername(),
                                "fullName", savedOwner.getFullName(),
                                "role", savedOwner.getRole(),
                                "message", "Tạo Owner thành công"
                        )
                );
    }


    // =========================================================
    // 3. LẤY CĂN HỘ CỦA OWNER
    //
    // GET
    // /api/admin/owners/{ownerId}/apartments
    // =========================================================

    @GetMapping("/{ownerId}/apartments")
    public ResponseEntity<?> getOwnerApartments(
            @PathVariable Integer ownerId
    ) {

        User owner =
                userRepository.findById(ownerId)
                        .orElse(null);

        if (owner == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    "Không tìm thấy Owner"
                            )
                    );
        }


        if (!"OWNER".equalsIgnoreCase(
                owner.getRole()
        )) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "User này không phải Owner"
                            )
                    );
        }


        List<Apartment> apartments =
                apartmentRepository.findByOwner_Id(
                        ownerId.longValue()
                );

        return ResponseEntity.ok(apartments);
    }


    // =========================================================
    // 4. GÁN CĂN HỘ CHO OWNER
    //
    // PUT
    // /api/admin/owners/{ownerId}/apartments/{apartmentId}
    // =========================================================

    @PutMapping("/{ownerId}/apartments/{apartmentId}")
    public ResponseEntity<?> assignApartmentToOwner(
            @PathVariable Integer ownerId,
            @PathVariable Long apartmentId
    ) {

        // -----------------------------------------------------
        // Tìm Owner
        // -----------------------------------------------------

        User owner =
                userRepository.findById(ownerId)
                        .orElse(null);

        if (owner == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    "Không tìm thấy Owner"
                            )
                    );
        }


        // -----------------------------------------------------
        // Kiểm tra role
        // -----------------------------------------------------

        if (!"OWNER".equalsIgnoreCase(
                owner.getRole()
        )) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "User này không phải Owner"
                            )
                    );
        }


        // -----------------------------------------------------
        // Tìm căn hộ
        // -----------------------------------------------------

        Apartment apartment =
                apartmentRepository.findById(apartmentId)
                        .orElse(null);

        if (apartment == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    "Không tìm thấy căn hộ"
                            )
                    );
        }


        // -----------------------------------------------------
        // Nếu căn hộ đã có Owner khác
        // -----------------------------------------------------

        if (apartment.getOwner() != null
                && !apartment.getOwner()
                .getId()
                .equals(ownerId)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "message",
                                    "Căn hộ này đã được gán cho Owner khác"
                            )
                    );
        }


        // -----------------------------------------------------
        // GÁN OWNER
        // -----------------------------------------------------

        apartment.setOwner(owner);


        // -----------------------------------------------------
        // Lưu
        // -----------------------------------------------------

        Apartment savedApartment =
                apartmentRepository.save(apartment);


        return ResponseEntity.ok(savedApartment);
    }


    // =========================================================
    // 5. BỎ GÁN CĂN HỘ
    //
    // PUT
    // /api/admin/owners/{ownerId}/apartments/{apartmentId}/unassign
    // =========================================================

    @PutMapping("/{ownerId}/apartments/{apartmentId}/unassign")
    public ResponseEntity<?> unassignApartment(
            @PathVariable Integer ownerId,
            @PathVariable Long apartmentId
    ) {

        User owner =
                userRepository.findById(ownerId)
                        .orElse(null);

        if (owner == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    "Không tìm thấy Owner"
                            )
                    );
        }


        Apartment apartment =
                apartmentRepository.findById(apartmentId)
                        .orElse(null);

        if (apartment == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    "Không tìm thấy căn hộ"
                            )
                    );
        }


        if (apartment.getOwner() == null
                || !apartment.getOwner()
                .getId()
                .equals(ownerId)) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Căn hộ này không thuộc Owner này"
                            )
                    );
        }


        apartment.setOwner(null);

        Apartment savedApartment =
                apartmentRepository.save(apartment);

        return ResponseEntity.ok(savedApartment);
    }


    // =========================================================
    // 6. ĐỔI / RESET PASSWORD OWNER
    //
    // PUT
    // /api/admin/owners/{ownerId}/password
    // =========================================================

    @PutMapping("/{ownerId}/password")
    public ResponseEntity<?> resetOwnerPassword(
            @PathVariable Integer ownerId,
            @RequestBody ResetPasswordRequest request
    ) {

        // -----------------------------------------------------
        // Kiểm tra password mới
        // -----------------------------------------------------

        if (request.getPassword() == null
                || request.getPassword().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Mật khẩu mới không được để trống"
                            )
                    );
        }


        // -----------------------------------------------------
        // Tìm Owner
        // -----------------------------------------------------

        User owner =
                userRepository.findById(ownerId)
                        .orElse(null);

        if (owner == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    "Không tìm thấy Owner"
                            )
                    );
        }


        // -----------------------------------------------------
        // Kiểm tra role
        // -----------------------------------------------------

        if (!"OWNER".equalsIgnoreCase(
                owner.getRole()
        )) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Tài khoản này không phải Owner"
                            )
                    );
        }


        // -----------------------------------------------------
        // Mã hóa password mới
        // -----------------------------------------------------

        owner.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );


        // -----------------------------------------------------
        // Lưu
        // -----------------------------------------------------

        userRepository.save(owner);


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Đổi mật khẩu Owner thành công",
                        "username",
                        owner.getUsername()
                )
        );
    }


    // =========================================================
    // REQUEST TẠO OWNER
    // =========================================================

    public static class CreateOwnerRequest {

        private String username;
        private String password;
        private String fullName;

        public CreateOwnerRequest() {
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


    // =========================================================
    // REQUEST RESET PASSWORD
    // =========================================================

    public static class ResetPasswordRequest {

        private String password;

        public ResetPasswordRequest() {
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}