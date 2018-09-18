package com.usoftchina.chain.dashboard.controller;

import com.usoftchina.chain.dashboard.service.BlockService;
import com.usoftchina.chain.dashboard.service.BlockchainService;
import com.usoftchina.chain.dashboard.service.TransactionService;
import com.usoftchina.chain.dashboard.web.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author yingp
 * @date 2018/9/14
 */
@RestController
@RequestMapping
public class BlockchainController {

    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/blockchain")
    public ResponseEntity getBlockchains() {
        return Result.ok(blockchainService.findAll());
    }

    @GetMapping("/block")
    public ResponseEntity getBlocksPage(@RequestParam String channel, @PageableDefault(size = 15, page = 0) Pageable pageable) {
        return Result.ok(blockService.pagingByChannel(channel, pageable));
    }

    @GetMapping("/transaction/page")
    public ResponseEntity getTransactionsPage(@RequestParam String channel, @PageableDefault(size = 15, page = 0) Pageable pageable) {
        return Result.ok(transactionService.pagingByChannel(channel, pageable));
    }

    @GetMapping("/transaction/list")
    public ResponseEntity getTransactionsByBlock(@RequestParam String blockHash) {
        return Result.ok(transactionService.findByBlockHash(blockHash));
    }

    @GetMapping("/transaction")
    public ResponseEntity getTransaction(@RequestParam String transactionID) {
        return Result.ok(transactionService.findOne(transactionID));
    }
}
