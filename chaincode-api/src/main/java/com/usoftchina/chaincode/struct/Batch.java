package com.usoftchina.chaincode.struct;

import java.io.Serializable;
import java.util.Date;

/**
 * 库存批次
 *
 * @author yingp
 * @date 2018/9/10
 */
public class Batch implements Serializable {
    private String num;
    private String owner;
    private String productNum;
    private double quantity;
    private String warehouseName;
    private String locationName;
    /**
     * 来源
     */
    private String preNum;
    /**
     * 入库日期
     */
    private Date indate;
    /**
     * 类型: 初始化init,交易trade,制造make
     */
    private String batchType;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String productNum) {
        this.productNum = productNum;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getPreNum() {
        return preNum;
    }

    public void setPreNum(String preNum) {
        this.preNum = preNum;
    }

    public Date getIndate() {
        return indate;
    }

    public void setIndate(Date indate) {
        this.indate = indate;
    }

    public String getBatchType() {
        return batchType;
    }

    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }
}
