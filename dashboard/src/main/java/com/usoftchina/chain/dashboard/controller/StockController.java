package com.usoftchina.chain.dashboard.controller;

import com.usoftchina.chain.dashboard.web.Result;
import com.usoftchina.chaincode.api.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yingp
 * @date 2018/9/18
 */
@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/warehouse")
    public ResponseEntity getWarehouseList() {
        return Result.ok(stockService.queryAllWarehouses());
    }

    @GetMapping("/product")
    public ResponseEntity getProductList() {
        return Result.ok(stockService.queryAllProducts());
    }

    @GetMapping("/batch")
    public ResponseEntity getBatchList() {
        return Result.ok(stockService.queryAllBatches());
    }

    @GetMapping("/batch/history")
    public ResponseEntity getBatchHistoryList(@RequestParam String batchNum) {
        return Result.ok(stockService.queryBatchHistory(batchNum));
    }

}
