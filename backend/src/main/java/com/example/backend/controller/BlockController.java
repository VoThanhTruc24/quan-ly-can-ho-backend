package com.example.backend.controller;

import com.example.backend.entity.Block;
import com.example.backend.service.BlockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@CrossOrigin(origins = "http://localhost:5173")
public class BlockController {

    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    // CREATE
    @PostMapping
    public Block create(@RequestBody Block block) {
        return blockService.create(block);
    }

    // GET ALL
    @GetMapping
    public List<Block> getAll() {
        return blockService.getAll();
    }

    // UPDATE
    @PutMapping("/{id}")
    public Block update(@PathVariable Long id, @RequestBody Block block) {
        return blockService.update(id, block);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        blockService.delete(id);
    }
}