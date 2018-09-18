package org.springframework.fabric.query;

import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.BlockchainInfo;

import java.io.Serializable;

/**
 * @author yingp
 * @date 2018/9/11
 */
public class BlockchainHexInfo implements Serializable{
    private long height;
    private String currentBlockHash;
    private String previousBlockHash;

    public BlockchainHexInfo(BlockchainInfo info) {
        this.height = info.getHeight();
        this.currentBlockHash = Hex.toHexString(info.getCurrentBlockHash());
        this.previousBlockHash = Hex.toHexString(info.getPreviousBlockHash());
    }

    public long getHeight() {
        return height;
    }

    public String getCurrentBlockHash() {
        return currentBlockHash;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }
}
