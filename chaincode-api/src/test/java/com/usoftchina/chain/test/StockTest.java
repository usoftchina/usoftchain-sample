package com.usoftchina.chain.test;

import com.usoftchina.chain.config.NetworkConfig;
import com.usoftchina.chain.repository.StockRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.chaincode.sdk.client.InvokeException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author yingp
 * @date 2018/9/5
 */
@ContextConfiguration(classes = {NetworkConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class StockTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    public void testCreateAccount() throws Exception {
        Assert.assertNotNull(stockRepository);
        try {
            String result = stockRepository.createAccount("91440300778798789B", "天派电子(深圳)有限公司");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + result);
        } catch (InvokeException e) {
            System.err.println(e.getMessage());
        }

    }

    @Test
    public void testQueryAccount() {
        Assert.assertNotNull(stockRepository);
        try {
            String result = stockRepository.queryAccount("91440300778798789B");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + result);
        } catch (InvokeException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testCreateWarehouse() throws Exception {
        Assert.assertNotNull(stockRepository);
        try {
            String result = stockRepository.createWarehouse("91440300778798789B", "新宁", "新宁仓", "广东省东莞市长安镇乌沙第六工业区海滨路23号");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + result);
        } catch (InvokeException e) {
            System.err.println(e.getMessage());
        }

    }

    @Test
    public void testCreateLocation() throws Exception {
        Assert.assertNotNull(stockRepository);
        try {
            String result = stockRepository.createLocation("新宁", "1-1", "1-1");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + result);
        } catch (InvokeException e) {
            System.err.println(e.getMessage());
        }

    }

}
