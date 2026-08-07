package com.example.backend.controller;

import com.example.backend.entity.Block;
import com.example.backend.entity.Floor;
import com.example.backend.repository.BlockRepository;
import com.example.backend.repository.FloorRepository;
import com.example.backend.dto.FloorRequest;
import com.example.backend.dto.FloorResponse;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/floors")
@CrossOrigin(origins = "http://localhost:5173")
public class FloorController {

    private final FloorRepository floorRepository;
    private final BlockRepository blockRepository;

    public FloorController(
            FloorRepository floorRepository,
            BlockRepository blockRepository
    ) {
        this.floorRepository = floorRepository;
        this.blockRepository = blockRepository;
    }

    // CREATE FLOOR
    @PostMapping
    public Floor create(@RequestBody Floor floor) {

        Long blockId = floor.getBlock().getId();

        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new RuntimeException("Block not found"));

        floor.setBlock(block);

        return floorRepository.save(floor);
    }

    // GET ALL
    @GetMapping
    public List<Floor> getAll() {
        return floorRepository.findAll();
    }

    // XÓA FLOOR
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {

        if (!floorRepository.existsById(id)) {
            throw new RuntimeException("Floor not found");
        }

        floorRepository.deleteById(id);

        return "Deleted floor with id = " + id;
    }

    // ✅ PUT phải nằm TRONG class
    @PutMapping("/{id}")
    public FloorResponse update(@PathVariable Long id,
                                @RequestBody FloorRequest request) {

        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floor not found"));

        Block block = blockRepository.findById(request.getBlockId())
                .orElseThrow(() -> new RuntimeException("Block not found"));

        floor.setName(request.getName());
        floor.setBlock(block);

        floorRepository.save(floor);

        return new FloorResponse(
                floor.getId(),
                floor.getName(),
                block.getName()
        );
    }
}
