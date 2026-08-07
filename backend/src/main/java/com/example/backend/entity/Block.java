package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "block") // 👈 QUAN TRỌNG
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Block() {}

    public Block(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}