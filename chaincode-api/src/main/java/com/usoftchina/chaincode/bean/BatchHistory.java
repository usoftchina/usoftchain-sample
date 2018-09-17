package com.usoftchina.chaincode.bean;

import com.usoftchina.chaincode.struct.Batch;

import java.util.Date;

/**
 * @author yingp
 * @date 2018/9/14
 */
public class BatchHistory {
    private String tx_id;
    private Batch value;
    private Date timestamp;

    public BatchHistory(History history) {
        this.tx_id = history.getTx_id();
        this.value = history.parseObject(Batch.class);
        this.timestamp = history.getDate();
    }

    public String getTx_id() {
        return tx_id;
    }

    public void setTx_id(String tx_id) {
        this.tx_id = tx_id;
    }

    public Batch getValue() {
        return value;
    }

    public void setValue(Batch value) {
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
