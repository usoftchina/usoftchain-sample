package com.usoftchina.chain.dashboard.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author yingp
 * @date 2018/9/14
 */
public class Transaction implements Serializable{
    private String transactionID;
    private String nonce;
    private boolean valid;
    private byte validationCode;
    private Date timestamp;
    public List<TransactionAction> transactionActions;
    private String blockHash;
    private String channel;

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public byte getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(byte validationCode) {
        this.validationCode = validationCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<TransactionAction> getTransactionActions() {
        return transactionActions;
    }

    public void setTransactionActions(List<TransactionAction> transactionActions) {
        this.transactionActions = transactionActions;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
