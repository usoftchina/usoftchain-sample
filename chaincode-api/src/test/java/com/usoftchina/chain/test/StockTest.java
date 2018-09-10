package com.usoftchina.chain.test;

import com.alibaba.fastjson.JSON;
import com.usoftchina.chain.repository.StockRepository;
import com.usoftchina.chain.struct.Stock;
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
    public void test() throws Exception{
        Assert.assertNotNull(stockRepository);

        stockRepository.createAccount("91440300778798789B", "天派电子(深圳)有限公司");
        stockRepository.createAccount("91440300319521190W", "深圳市华商龙商务互联科技有限公司");
        stockRepository.createAccount("91441900760628116P", "东莞市新宁仓储有限公司");

        stockRepository.createWarehouse("91441900760628116P","新宁","新宁仓","广东省东莞市长安镇乌沙第六工业区海滨路23号");
        stockRepository.createLocation("新宁","1-1", "1-1");
        stockRepository.createWarehouse("91440300778798789B","天派材料良品仓","天派材料良品仓","深圳市宝安区福永镇新和村新兴工业园6区A1栋");
        stockRepository.createLocation("天派材料良品仓","1-1", "1-1");

        stockRepository.createProduct("EMVA500ADA470MF80G","EMVA500ADA470MF80G","电容","铝电解电容","贵弥功NCC","PCS");

        String stockJson = stockRepository.initStock("91440300319521190W","EMVA500ADA470MF80G","新宁","1-1","1000.00");
        System.out.println("=========" + stockJson + "=========");
        Stock stock = JSON.parseObject(stockJson, Stock.class);
        System.out.println("~~~~~~~~~~" +stock.getIndate().toString());

        stockJson = stockRepository.transferStock("91440300778798789B","天派材料良品仓","1-1",stock.getNum(),"800.00");
        System.out.println("=========" + stockJson + "=========");
        Stock stock2 = JSON.parseObject(stockJson, Stock.class);

        stockJson = stockRepository.queryStock(stock2.getPreNum());
        System.out.println("=========" + stockJson + "=========");

        stockJson = stockRepository.queryStock(stock2.getNum());
        System.out.println("=========" + stockJson + "=========");
    }

}
