package com.usoftchina.chain.repository;

import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeInvoke;
import org.springframework.data.chaincode.repository.ChaincodeQuery;
import org.springframework.data.chaincode.repository.ChaincodeRepository;

/**
 * @author yingp
 * @date 2018/9/5
 */
@Chaincode(channel = "mychannel", name = "stockcontract", version = "1.0")
public interface StockRepository extends ChaincodeRepository {
    /**
     * 创建账户
     * {"Args":["createAccount",num,name]}
     * {"Args":["createAccount","91441900760628116P","东莞市新宁仓储有限公司"]}
     *
     * @param num
     * @param name
     * @return
     */
    @ChaincodeInvoke
    String createAccount(String num, String name);

    /**
     * 创建仓库
     * {"Args":["createWarehouse",owner,name,desc,address]}
     * {"Args":["createWarehouse","91441900760628116P","新宁","新宁仓","广东省东莞市长安镇乌沙第六工业区海滨路23号"]}
     *
     * @param owner
     * @param name
     * @param desc
     * @param address
     * @return
     */
    @ChaincodeInvoke
    String createWarehouse(String owner, String name, String desc, String address);

    /**
     * 创建仓位
     * {"Args":["createLocation",warehouseName,locationName,(locationDesc)]}
     * {"Args":["createLocation","新宁","1-1","1-1"]}
     *
     * @param warehouseName
     * @param locationName
     * @param locationDesc
     * @return
     */
    @ChaincodeInvoke
    String createLocation(String warehouseName, String locationName, String locationDesc);

    /**
     * 创建物料
     * {"Args":["createProduct",num,spec,group,desc,brand,unit]}
     * {"Args":["createProduct","EMVA500ADA470MF80G","EMVA500ADA470MF80G","电容","铝电解电容","贵弥功NCC","PCS"]}
     *
     * @param num
     * @param spec
     * @param group
     * @param desc
     * @param brand
     * @param unit
     * @return
     */
    @ChaincodeInvoke
    String createProduct(String num, String spec, String group, String desc, String brand, String unit);

    /**
     * 库存初始化
     * {"Args":["initStock","914403006658721616","NCC24","新宁","1-1","1000.00"]}
     *
     * @param owner
     * @param productNum
     * @param warehouseName
     * @param locationName
     * @param quantity
     * @return
     */
    @ChaincodeInvoke
    String initStock(String owner, String productNum, String warehouseName, String locationName, String quantity);

    /**
     * 完工入库
     * {"Args":["initStock","914403006658721616","NCC24","新宁","1-1","1000.00"]}
     *
     * @param owner
     * @param productNum
     * @param warehouseName
     * @param locationName
     * @param quantity
     * @return
     */
    @ChaincodeInvoke
    String completeStock(String owner, String productNum, String warehouseName, String locationName, String quantity);

    /**
     * 库存转移：出货、采购验收
     * {"Args":["transferStock",toAccountNum,toWarehouseName,toLocationName,fromStockNum,quantity]}
     * {"Args":["transferStock","91440300MA5DC1WL1W","格力","1-1","201809039382631161","1000.00"]}
     *
     * @param toAccountNum
     * @param toWarehouseName
     * @param toLocationName
     * @param fromStockNum
     * @param quantity
     * @return
     */
    @ChaincodeInvoke
    String transferStock(String toAccountNum, String toWarehouseName, String toLocationName, String fromStockNum, String quantity);

    /**
     * 库存查询
     * {"Args":["queryStock",stockNum]}
     * {"Args":["queryStock","201809039382631161"]}
     *
     * @param stockNum
     * @return
     */
    @ChaincodeQuery
    String queryStock(String stockNum);
}
