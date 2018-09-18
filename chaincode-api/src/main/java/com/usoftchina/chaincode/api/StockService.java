package com.usoftchina.chaincode.api;

import com.alibaba.fastjson.JSON;
import com.usoftchina.chaincode.bean.BatchHistory;
import com.usoftchina.chaincode.bean.History;
import com.usoftchina.chaincode.repository.StockRepository;
import com.usoftchina.chaincode.struct.Batch;
import com.usoftchina.chaincode.struct.Product;
import com.usoftchina.chaincode.struct.Warehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yingp
 * @date 2018/9/14
 */
@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    /**
     * 创建仓库
     *
     * @param warehouse
     */
    public void createWarehouse(Warehouse warehouse) {
        stockRepository.createWarehouse(warehouse.getName(), warehouse.getDesc(), warehouse.getAddress());
    }

    /**
     * 查询仓库
     *
     * @param name
     * @return
     */
    public Warehouse queryWarehouse(String name) {
        String json = stockRepository.queryWarehouse(name);
        return JSON.parseObject(json, Warehouse.class);
    }

    /**
     * 创建仓位
     *
     * @param warehouseName
     * @param locationName
     * @param locationDesc
     */
    public void createLocation(String warehouseName, String locationName, String locationDesc) {
        stockRepository.createLocation(warehouseName, locationName, locationDesc);
    }

    /**
     * 创建物料
     *
     * @param product
     */
    public void createProduct(Product product) {
        stockRepository.createProduct(product.getNum(), product.getSpec(), product.getGroup(), product.getDesc(), product.getBrand(), product.getUnit());
    }

    /**
     * 库存初始化
     *
     * @param productNum
     * @param warehouseName
     * @param locationName
     * @param quantity
     * @return
     */
    public Batch initStock(String productNum, String warehouseName, String locationName, String quantity) {
        String json = stockRepository.initStock(productNum, warehouseName, locationName, quantity);
        return JSON.parseObject(json, Batch.class);
    }

    /**
     * 完工入库
     *
     * @param productNum
     * @param warehouseName
     * @param locationName
     * @param quantity
     * @return
     */
    public Batch completeStock(String productNum, String warehouseName, String locationName, String quantity) {
        String json = stockRepository.completeStock(productNum, warehouseName, locationName, quantity);
        return JSON.parseObject(json, Batch.class);
    }

    /**
     * 库存转移：出货、采购验收
     *
     * @param toAccount
     * @param toWarehouseName
     * @param toLocationName
     * @param fromStockNum
     * @param quantity
     * @return
     */
    public Batch transferStock(String toAccount, String toWarehouseName, String toLocationName, String fromStockNum, String quantity) {
        String json = stockRepository.transferStock(toAccount, toWarehouseName, toLocationName, fromStockNum, quantity);
        return JSON.parseObject(json, Batch.class);
    }

    /**
     * 批次查询
     *
     * @param batchNum
     * @return
     */
    public Batch queryBatch(String batchNum) {
        String json = stockRepository.queryBatch(batchNum);
        if (null != json) {
            return JSON.parseObject(json, Batch.class);
        }
        return null;
    }

    /**
     * 批次历史操作查询
     *
     * @param batchNum
     * @return
     */
    public List<BatchHistory> queryBatchHistory(String batchNum) {
        String json = stockRepository.queryBatchHistory(batchNum);
        if (null != json) {
            List<History> histories = JSON.parseArray(json, History.class);
            return histories.stream().map(BatchHistory::new).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 查询所属批次
     *
     * @param owner
     * @return
     */
    public List<String> queryBatchesByOwner(String owner) {
        String json = stockRepository.queryBatchesByOwner(owner);
        if (null != json) {
            return JSON.parseArray(json, String.class);
        }
        return null;
    }

    /**
     * 查询我的批次
     *
     * @return
     */
    public List<String> queryMyBatches() {
        String json = stockRepository.queryMyBatches();
        if (null != json) {
            return JSON.parseArray(json, String.class);
        }
        return null;
    }

    /**
     * 查询全部批次
     *
     * @return
     */
    public List<Batch> queryAllBatches() {
        String json = stockRepository.queryAllBatches();
        if (null != json) {
            return JSON.parseArray(json, Batch.class);
        }
        return null;
    }

    /**
     * 查询全部仓库
     *
     * @return
     */
    public List<Warehouse> queryAllWarehouses() {
        String json = stockRepository.queryAllWarehouses();
        if (null != json) {
            return JSON.parseArray(json, Warehouse.class);
        }
        return null;
    }

    /**
     * 查询全部物料
     *
     * @return
     */
    public List<Product> queryAllProducts() {
        String json = stockRepository.queryAllProducts();
        if (null != json) {
            return JSON.parseArray(json, Product.class);
        }
        return null;
    }

    /**
     * 物料查询
     *
     * @param num
     * @return
     */
    public Product queryProduct(String num) {
        String json = stockRepository.queryProduct(num);
        if (null != json) {
            return JSON.parseObject(json, Product.class);
        }
        return null;
    }

}
