package com.usoftchina.chain.dashboard.service;

import com.usoftchina.chain.dashboard.dao.TransactionRepository;
import com.usoftchina.chain.dashboard.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Transaction findOne(String transactionID) {
        return transactionRepository.findOne(transactionID);
    }

    public List<Transaction> findByBlockHash(String blockHash) {
        return transactionRepository.findByBlockHash(blockHash);
    }

    public Page<Transaction> pagingByChannel(String channel, Pageable pageable) {
        return transactionRepository.pagingByChannel(channel, pageable);
    }
}
