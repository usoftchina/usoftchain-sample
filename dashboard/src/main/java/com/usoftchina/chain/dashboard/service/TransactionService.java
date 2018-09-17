package com.usoftchina.chain.dashboard.service;

import com.usoftchina.chain.dashboard.dao.TransactionRepository;
import com.usoftchina.chain.dashboard.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yingp
 * @date 2018/9/14
 */
@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> findByBlockHash(String blockHash) {
        return transactionRepository.findByBlockHash(blockHash);
    }
}
