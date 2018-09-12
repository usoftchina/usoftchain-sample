package org.springframework.fabric.query;

import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Peer;

/**
 * @author yingp
 * @date 2018/9/11
 */
public class PeerHexInfo {
    private String name;
    private String url;
    private BlockchainHexInfo blockchain;

    public PeerHexInfo(Peer peer, BlockchainInfo info) {
        this.name = peer.getName();
        this.url = peer.getUrl();
        this.blockchain = new BlockchainHexInfo(info);
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public BlockchainHexInfo getBlockchain() {
        return blockchain;
    }
}
