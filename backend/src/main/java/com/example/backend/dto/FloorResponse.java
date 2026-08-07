package com.example.backend.dto;

public class FloorResponse {

    private Long id;
    private String name;
    private String blockName;

    public FloorResponse(Long id, String name, String blockName) {
        this.id = id;
        this.name = name;
        this.blockName = blockName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBlockName() {
        return blockName;
    }
}