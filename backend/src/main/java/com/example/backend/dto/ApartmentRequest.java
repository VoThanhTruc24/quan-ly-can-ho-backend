package com.example.backend.dto;

public class ApartmentRequest {

    private String name;
    private Double area;
    private String status;
    private Long floorId;

    public String getName() { return name; }
    public Double getArea() { return area; }
    public String getStatus() { return status; }
    public Long getFloorId() { return floorId; }
}