package com.usoftchina.chain.api;

import com.usoftchina.chain.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yingp
 * @date 2018/9/5
 */
@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockRepository stockRepository;

    @GetMapping
    public void queryStock(String stockNum) {
        System.out.println(stockRepository.queryStock(stockNum));
    }

}
