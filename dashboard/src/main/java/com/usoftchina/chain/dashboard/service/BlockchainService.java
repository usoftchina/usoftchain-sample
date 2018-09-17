package com.usoftchina.chain.dashboard.service;

import com.usoftchina.chain.dashboard.entity.Blockchain;
import com.usoftchina.chain.dashboard.dao.BlockchainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yingp
 * @date 2018/9/14
 */
@Service
public class BlockchainService {
    @Autowired
    private BlockchainRepository blockchainRepository;

    public List<Blockchain> findAll() {
        return blockchainRepository.findAll();
    }
}
