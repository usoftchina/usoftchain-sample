package org.springframework.fabric.query;

import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.BlockInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yingp
 * @date 2018/9/11
 */
public class BlockHexInfo {

    private long blockNumber;
    private String dataHash;
    private String previousHash;
    private int transactionCount;
    private List<EnvelopeHexInfo> envelopes;

    public BlockHexInfo(BlockInfo info) {
        this.blockNumber = info.getBlockNumber();
        this.dataHash = Hex.toHexString(info.getDataHash());
        this.previousHash = Hex.toHexString(info.getPreviousHash());
        this.transactionCount = info.getTransactionCount();
        this.envelopes = new ArrayList<>();
        info.getEnvelopeInfos().forEach(envelopeInfo -> {
            this.envelopes.add(new EnvelopeHexInfo(envelopeInfo));
        });
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public String getDataHash() {
        return dataHash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public List<EnvelopeHexInfo> getEnvelopes() {
        return envelopes;
    }
}
