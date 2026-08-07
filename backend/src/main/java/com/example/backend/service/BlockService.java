package com.example.backend.service;

import com.example.backend.entity.Block;
import com.example.backend.repository.BlockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockService {

    private final BlockRepository blockRepository;

    public BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    public Block create(Block block) {
        return blockRepository.save(block);
    }

    public List<Block> getAll() {
        return blockRepository.findAll();
    }

    public Block update(Long id, Block newBlock) {
        Block block = blockRepository.findById(id).orElseThrow();
        block.setName(newBlock.getName());
        return blockRepository.save(block);
    }

    public void delete(Long id) {
        blockRepository.deleteById(id);
    }
}