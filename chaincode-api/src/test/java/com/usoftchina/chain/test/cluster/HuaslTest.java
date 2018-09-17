package com.usoftchina.chain.test.cluster;

import com.usoftchina.chaincode.repository.StockRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author yingp
 * @date 2018/9/12
 */
@ContextConfiguration(classes = {HuaslNetworkConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class HuaslTest {
    @Autowired
    private StockRepository stockRepository;

    @Test
    public void testInvoke() throws Exception {
        Assert.assertNotNull(stockRepository);
        stockRepository.createProduct("EMVA500ADA470MF80G","EMVA500ADA470MF80G","电容","铝电解电容","贵弥功NCC","PCS");
        String json = stockRepository.initStock("EMVA500ADA470MF80G","新宁","1-1","1000.00");
        System.out.println(">>>>>>>>>>" + json);
        // {"num":"201809148907782353","owner":"huasl","productNum":"EMVA500ADA470MF80G","quantity":1000,"warehouseName":"新宁","locationName":"1-1","preNum":"","indate":1536890778864,"batchType":"init"}
    }

    @Test
    public void testQuery() throws Exception {
        String json = stockRepository.queryMyBatches();
        System.out.println(">>>>>>>>>>" + json);
        // ["201809148907782353"]
    }
}
