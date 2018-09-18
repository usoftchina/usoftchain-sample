package org.springframework.fabric.query;

import org.hyperledger.fabric.sdk.Orderer;

import java.io.Serializable;

/**
 * @author yingp
 * @date 2018/9/18
 */
public class OrdererInfo implements Serializable {

    private String name;
    private String url;

    public OrdererInfo(Orderer orderer) {
        this.name = orderer.getName();
        this.url = orderer.getUrl();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
