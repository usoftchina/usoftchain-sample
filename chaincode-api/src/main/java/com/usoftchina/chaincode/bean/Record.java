package com.usoftchina.chaincode.bean;

import com.alibaba.fastjson.JSON;
import org.springframework.util.Base64Utils;

/**
 * @author yingp
 * @date 2018/9/19
 */
public class Record {
    private String namespace;
    private String key;
    private String value;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public <T> T parseObject(Class<T> targetClass) {
        String json = new String(Base64Utils.decodeFromString(getValue()));
        return JSON.parseObject(json, targetClass);
    }
}
