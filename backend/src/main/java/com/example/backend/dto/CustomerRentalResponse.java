package com.example.backend.dto;

public class CustomerRentalResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;

    private Long contractId;
    private String contractStatus;

    private Long apartmentId;
    private String apartmentName;

    private Long ownerId;
    private String ownerName;

    public CustomerRentalResponse() {
    }

    public CustomerRentalResponse(
            Long id,
            String name,
            String email,
            String phone,
            String address,
            Long contractId,
            String contractStatus,
            Long apartmentId,
            String apartmentName,
            Long ownerId,
            String ownerName
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.contractId = contractId;
        this.contractStatus = contractStatus;
        this.apartmentId = apartmentId;
        this.apartmentName = apartmentName;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getApartmentName() {
        return apartmentName;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}