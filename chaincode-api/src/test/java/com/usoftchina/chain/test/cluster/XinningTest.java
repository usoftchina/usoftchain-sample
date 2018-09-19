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
@ContextConfiguration(classes = {XinningNetworkConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class XinningTest {
    @Autowired
    private StockRepository stockRepository;

    @Test
    public void testInvoke() throws Exception {
        Assert.assertNotNull(stockRepository);
        stockRepository.createWarehouse("新宁", "新宁仓", "广东省东莞市长安镇乌沙第六工业区海滨路23号");
        stockRepository.createLocation("新宁", "1-1", "1-1");
    }

    @Test
    public void testQuery() throws Exception {
        Assert.assertNotNull(stockRepository);
        String json = stockRepository.queryWarehouse("新宁");
        System.out.println(">>>>>>>>>>" + json);
        // {"address":"广东省东莞市长安镇乌沙第六工业区海滨路23号","desc":"新宁仓","locations":{"1-1":"1-1","default":"默认仓位"},"name":"新宁","owner":"xinning"}
    }

    @Test
    public void testQueryAll() throws Exception {
        Assert.assertNotNull(stockRepository);
        String json = stockRepository.queryAllWarehouses();
        System.out.println(">>>>>>>>>>" + json);
        // [{"namespace":"stockcontract","key":"warehouse:新宁","value":"eyJhZGRyZXNzIjoi5bm/5Lic55yB5Lic6I6e5biC6ZW/5a6J6ZWH5LmM5rKZ56ys5YWt5bel5Lia5Yy65rW35ruo6LevMjPlj7ciLCJkZXNjIjoi5paw5a6B5LuTIiwiZG9jVHlwZSI6IndhcmVob3VzZSIsImxvY2F0aW9ucyI6eyIxLTEiOiIxLTEiLCJkZWZhdWx0Ijoi6buY6K6k5LuT5L2NIn0sIm5hbWUiOiLmlrDlroEiLCJvd25lciI6Inhpbm5pbmcifQ=="}]
    }
}
