package org.springframework.fabric.query;

import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yingp
 * @date 2018/9/11
 */
public class ChannelQuery {
    /**
     * Get blockchain hex info
     *
     * @param channel
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public static BlockchainHexInfo queryBlockchainInfo(Channel channel) throws ProposalException, InvalidArgumentException {
        if (channel.getPeers().isEmpty()) {
            return null;
        }
        return new BlockchainHexInfo(channel.queryBlockchainInfo());
    }

    /**
     * Get all orderers info
     *
     * @param channel
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public static List<OrdererInfo> queryOrderersInfo(Channel channel) throws ProposalException, InvalidArgumentException {
        List<OrdererInfo> list = new ArrayList<>();
        for (Orderer orderer : channel.getOrderers()) {
            list.add(new OrdererInfo(orderer));
        }
        return list;
    }

    /**
     * Get all peers hex info
     *
     * @param channel
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public static List<PeerHexInfo> queryPeersInfo(Channel channel) throws ProposalException, InvalidArgumentException {
        List<PeerHexInfo> list = new ArrayList<>();
        for (Peer peer : channel.getPeers()) {
            list.add(new PeerHexInfo(peer, channel.queryBlockchainInfo(peer)));
        }
        return list;
    }

    /**
     * Get block info
     *
     * @param channel
     * @param txid
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public static BlockHexInfo queryBlockByTransactionID(Channel channel, String txid) throws ProposalException, InvalidArgumentException {
        return new BlockHexInfo(channel.queryBlockByTransactionID(txid));
    }

    /**
     * Get block info
     *
     * @param channel
     * @param blockHash
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public static BlockHexInfo queryBlockByHash(Channel channel, String blockHash) throws ProposalException, InvalidArgumentException {
        return new BlockHexInfo(channel.queryBlockByHash(Hex.decode(blockHash)));
    }
}
