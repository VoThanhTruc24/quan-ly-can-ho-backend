package com.example.backend.dto;

public class OwnerRequest {

    private String username;
    private String password;
    private String fullName;

    public OwnerRequest() {
    }

    public OwnerRequest(
            String username,
            String password,
            String fullName
    ) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    // =========================
    // GETTER
    // =========================

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    // =========================
    // SETTER
    // =========================

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}