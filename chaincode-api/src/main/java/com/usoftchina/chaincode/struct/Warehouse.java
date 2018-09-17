package com.usoftchina.chaincode.struct;

import java.util.Map;

/**
 * 仓库
 *
 * @author yingp
 * @date 2018/9/14
 */
public class Warehouse {
    private String name;
    private String desc;
    private String address;
    private String owner;
    /**
     * 仓位
     */
    private Map<String, String> locations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Map<String, String> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, String> locations) {
        this.locations = locations;
    }
}
