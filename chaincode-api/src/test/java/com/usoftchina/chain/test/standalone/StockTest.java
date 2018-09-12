package com.usoftchina.chain.test.standalone;

import com.alibaba.fastjson.JSON;
import com.usoftchina.chain.repository.StockRepository;
import com.usoftchina.chain.struct.Batch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author yingp
 * @date 2018/9/5
 */
@ContextConfiguration(classes = {StandaloneNetworkConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class StockTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    public void test() throws Exception {
        Assert.assertNotNull(stockRepository);

//        stockRepository.createAccount("91440300778798789B", "天派电子(深圳)有限公司");
//        stockRepository.createAccount("91440300319521190W", "深圳市华商龙商务互联科技有限公司");
//        stockRepository.createAccount("91441900760628116P", "东莞市新宁仓储有限公司");

        stockRepository.createWarehouse("新宁", "新宁仓", "广东省东莞市长安镇乌沙第六工业区海滨路23号");
        stockRepository.createLocation("新宁", "1-1", "1-1");
        stockRepository.createWarehouse("天派材料良品仓", "天派材料良品仓", "深圳市宝安区福永镇新和村新兴工业园6区A1栋");
        stockRepository.createLocation("天派材料良品仓", "1-1", "1-1");

        stockRepository.createProduct("EMVA500ADA470MF80G", "EMVA500ADA470MF80G", "电容", "铝电解电容", "贵弥功NCC", "PCS");

        String batchJson = stockRepository.initStock("EMVA500ADA470MF80G", "新宁", "1-1", "1000.00");
        System.out.println("=========" + batchJson + "=========");
        // {"num":"201809127174001276","owner":"org1","productNum":"EMVA500ADA470MF80G","quantity":1000,"warehouseName":"新宁","locationName":"1-1","preNum":"","indate":1536717400040,"batchType":"init"}
        Batch batch = JSON.parseObject(batchJson, Batch.class);

        batchJson = stockRepository.transferStock("org1", "天派材料良品仓", "1-1", batch.getNum(), "800.00");
        System.out.println("=========" + batchJson + "=========");
        // {"num":"201809127174021918","owner":"org1","productNum":"EMVA500ADA470MF80G","quantity":800,"warehouseName":"天派材料良品仓","locationName":"1-1","preNum":"201809127174001276","indate":1536717402413,"batchType":"trade"}
        Batch batch2 = JSON.parseObject(batchJson, Batch.class);

        batchJson = stockRepository.queryBatchHistory(batch2.getPreNum());
        System.out.println("=========" + batchJson + "=========");
        // [{"tx_id":"585e2de922716f90333cc2cc4c9d8f188a74f43596e9aa8253e3c267926f71f7","value":"eyJudW0iOiIyMDE4MDkxMjcxNzQwMDEyNzYiLCJvd25lciI6Im9yZzEiLCJwcm9kdWN0TnVtIjoiRU1WQTUwMEFEQTQ3ME1GODBHIiwicXVhbnRpdHkiOjEwMDAsIndhcmVob3VzZU5hbWUiOiLmlrDlroEiLCJsb2NhdGlvbk5hbWUiOiIxLTEiLCJwcmVOdW0iOiIiLCJpbmRhdGUiOjE1MzY3MTc0MDAwNDAsImJhdGNoVHlwZSI6ImluaXQifQ==","timestamp":{"seconds":1536717024,"nanos":905000000}},{"tx_id":"69514a7b591aa4fbd651f44cc46b4c7ab4622b03b5996cb05d235061e9eafda7","value":"eyJudW0iOiIyMDE4MDkxMjcxNzQwMDEyNzYiLCJvd25lciI6Im9yZzEiLCJwcm9kdWN0TnVtIjoiRU1WQTUwMEFEQTQ3ME1GODBHIiwicXVhbnRpdHkiOjIwMCwid2FyZWhvdXNlTmFtZSI6IuaWsOWugSIsImxvY2F0aW9uTmFtZSI6IjEtMSIsInByZU51bSI6IiIsImluZGF0ZSI6MTUzNjcxNzQwMDA0MCwiYmF0Y2hUeXBlIjoiaW5pdCJ9","timestamp":{"seconds":1536717027,"nanos":566000000}}]

        batchJson = stockRepository.queryBatch(batch2.getNum());
        System.out.println("=========" + batchJson + "=========");
        // {"batchType":"trade","indate":1536717402413,"locationName":"1-1","num":"201809127174021918","owner":"org1","preNum":"201809127174001276","productNum":"EMVA500ADA470MF80G","quantity":800,"warehouseName":"天派材料良品仓"}

        batchJson = stockRepository.queryMyBatches();
        System.out.println("=========" + batchJson + "=========");
        // ["201809127174001276","201809127174021918"]
    }

}
