package com.usoftchina.chaincode.bean;

import com.alibaba.fastjson.JSON;
import org.springframework.util.Base64Utils;

import java.util.Date;

/**
 * @author yingp
 * @date 2018/9/14
 */
public class History {
    private String tx_id;
    private String value;
    private Timestamp timestamp;

    public String getTx_id() {
        return tx_id;
    }

    public void setTx_id(String tx_id) {
        this.tx_id = tx_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public <T> T parseObject(Class<T> targetClass) {
        String json = new String(Base64Utils.decodeFromString(getValue()));
        return JSON.parseObject(json, targetClass);
    }

    public Date getDate() {
        return new Date(timestamp.getSeconds() * 1000 + timestamp.getNanos() / 1000000);
    }

    public static class Timestamp {
        private long seconds;
        private long nanos;

        public long getSeconds() {
            return seconds;
        }

        public void setSeconds(long seconds) {
            this.seconds = seconds;
        }

        public long getNanos() {
            return nanos;
        }

        public void setNanos(long nanos) {
            this.nanos = nanos;
        }
    }
}
