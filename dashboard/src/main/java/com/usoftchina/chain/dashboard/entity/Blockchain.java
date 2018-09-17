package com.usoftchina.chain.dashboard.entity;

import java.io.Serializable;

/**
 * @author yingp
 * @date 2018/9/14
 */
public class Blockchain implements Serializable {
    private String channel;
    private long height;
    private String currentBlockHash;
    private String previousBlockHash;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getCurrentBlockHash() {
        return currentBlockHash;
    }

    public void setCurrentBlockHash(String currentBlockHash) {
        this.currentBlockHash = currentBlockHash;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setPreviousBlockHash(String previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }
}
