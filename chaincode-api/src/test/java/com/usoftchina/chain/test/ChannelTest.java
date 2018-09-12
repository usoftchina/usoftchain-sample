package com.usoftchina.chain.test;

import com.alibaba.fastjson.JSON;
import org.hyperledger.fabric.sdk.Channel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.fabric.query.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author yingp
 * @date 2018/9/11
 */
@ContextConfiguration(classes = {StandaloneNetworkConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelTest {

    @Autowired
    private ChaincodeClient client;

    @Test
    public void test() throws Exception {
        Channel channel = client.getChannel("mychannel");

        BlockchainHexInfo blockchainHexInfo = ChannelQuery.queryBlockchainInfo(channel);
        System.out.println(JSON.toJSON(blockchainHexInfo));
        // {"previousBlockHash":"af38bed4605992a34fe4f89440c7e11fa2e2d403ffe63f46441e222b950b528c","currentBlockHash":"32ec1d927f2d6e529d8838bebb8c5dfb15720d45f6296767681b124e5185c6b3","height":12}
        System.out.println(JSON.toJSON(ChannelQuery.queryPeersInfo(channel)));
        // [{"blockchain":{"previousBlockHash":"af38bed4605992a34fe4f89440c7e11fa2e2d403ffe63f46441e222b950b528c","currentBlockHash":"32ec1d927f2d6e529d8838bebb8c5dfb15720d45f6296767681b124e5185c6b3","height":12},"name":"peer0","url":"grpc://192.168.0.174:7051"}]

        String blockHash = blockchainHexInfo.getCurrentBlockHash();
        while(true) {
            BlockHexInfo blockHexInfo = ChannelQuery.queryBlockByHash(channel, blockHash);
            System.out.println(blockHash + " >> " + JSON.toJSON(blockHexInfo));
            if (blockHexInfo.getBlockNumber() == 0) {
                break;
            }
            blockHash = blockHexInfo.getPreviousHash();
        }
        // 32ec1d927f2d6e529d8838bebb8c5dfb15720d45f6296767681b124e5185c6b3 >> {"previousHash":"af38bed4605992a34fe4f89440c7e11fa2e2d403ffe63f46441e222b950b528c","dataHash":"b09f30eb16be3fa34bba8d4e5a58373afc0733c808c1b2a51c7a6273cef6d849","blockNumber":11,"transactionCount":1,"envelopes":[{"valid":true,"validationCode":0,"type":"TRANSACTION_ENVELOPE","nonce":"4da3051b636e4fdd6abefb81fa9e1246beeae7a78704fcde","transactionID":"b025a90e2913485760e69823387d6194c89dc89bf6a62ef58deb16080e768417","timestamp":1536573838218}]}
        // 23e3aba8aae21fd6f0da7fd860c1090c5f229ccad8ee3e788d3c605c115b6c9d >> {"previousHash":"","dataHash":"5111afaf12b3fdff6890f5aad6f5f2bbad457d23e78641aa8bab0c294be22508","blockNumber":0,"transactionCount":0,"envelopes":[{"valid":true,"validationCode":0,"type":"ENVELOPE","nonce":"fff46d8cc9252d57b4086749c23afcbb141f42bbd7bd0606","transactionID":"","timestamp":1536574157000}]}
    }
}
