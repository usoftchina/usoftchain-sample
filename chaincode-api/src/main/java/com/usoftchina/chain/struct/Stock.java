package com.usoftchina.chain.struct;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yingp
 * @date 2018/9/10
 */
public class Stock implements Serializable{
    private String num;
    private String accountNum;
    private String productNum;
    private double quantity;
    private String warehouseName;
    private String locationName;
    private String preNum;
    private Date indate;
    private String stockType;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
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

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }
}
