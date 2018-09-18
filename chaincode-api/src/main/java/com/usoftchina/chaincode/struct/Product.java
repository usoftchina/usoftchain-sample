package com.usoftchina.chaincode.struct;

/**
 * 物料资料
 *
 * @author yingp
 * @date 2018/9/14
 */
public class Product {
    private String docType;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 编号、原厂型号
     */
    private String num;
    /**
     * 规格
     */
    private String spec;
    /**
     * 组别、类目
     */
    private String group;
    /**
     * 描述
     */
    private String desc;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 单位
     */
    private String unit;

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
