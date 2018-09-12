package com.usoftchina.chain.test.cluster;

import com.usoftchina.chain.repository.StockRepository;
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
@ContextConfiguration(classes = {SkypineNetworkConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SkypineTest {
    @Autowired
    private StockRepository stockRepository;

    @Test
    public void test() throws Exception {
        Assert.assertNotNull(stockRepository);
        stockRepository.createWarehouse("天派材料良品仓", "天派材料良品仓", "深圳市宝安区福永镇新和村新兴工业园6区A1栋");
        stockRepository.createLocation("天派材料良品仓", "1-1", "1-1");
    }
}