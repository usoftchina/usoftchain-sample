package com.usoftchina.chain.test;

import com.usoftchina.chain.config.NetworkConfig;
import com.usoftchina.chain.repository.StockRepository;
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
@ContextConfiguration(classes = {NetworkConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class StockTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    public void test() throws Exception {
        Assert.assertNotNull(stockRepository);

        String result = stockRepository.createAccount("91441900760628116P", "东莞市新宁仓储有限公司");
        System.out.println(result);
    }

}
