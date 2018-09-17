package com.usoftchina.chain.dashboard.entity;

import java.io.Serializable;

/**
 * @author yingp
 * @date 2018/9/14
 */
public class TransactionAction implements Serializable {
    private long id;
    private String transactionID;
    private int responseStatus;
    private String responseMessage;
    private int endorsementsCount;
    private int status;
    private String payload;
    private String args;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public int getEndorsementsCount() {
        return endorsementsCount;
    }

    public void setEndorsementsCount(int endorsementsCount) {
        this.endorsementsCount = endorsementsCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}
