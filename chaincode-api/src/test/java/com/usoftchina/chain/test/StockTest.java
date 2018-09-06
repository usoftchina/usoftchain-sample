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
            String result = stockRepository.createAccount("91440300319521190W", "深圳市华商龙商务互联科技有限公司");
            System.out.println(result);
        } catch (InvokeException e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    public void testQueryAccount() {
        Assert.assertNotNull(stockRepository);
        try {
            String result = stockRepository.queryAccount("91440300319521190W");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + result);
        } catch (InvokeException e) {
            System.err.println(e.getMessage());
        }
    }

}
