package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "floor")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "block_id")
    private Block block;

    // ===== GETTER SETTER =====

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

    public Block getBlock() {   // 👈 FIX 1
        return block;
    }

    public void setBlock(Block block) { // 👈 FIX 2
        this.block = block;
    }
}