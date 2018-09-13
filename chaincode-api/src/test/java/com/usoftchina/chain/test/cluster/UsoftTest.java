package com.usoftchina.chain.test.cluster;

import com.alibaba.fastjson.JSON;
import org.hyperledger.fabric.sdk.Channel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.fabric.query.BlockHexInfo;
import org.springframework.fabric.query.BlockchainHexInfo;
import org.springframework.fabric.query.ChannelQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author yingp
 * @date 2018/9/12
 */
@ContextConfiguration(classes = {UsoftNetworkConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class UsoftTest {
    @Autowired
    private ChaincodeClient client;

    @Test
    public void test() throws Exception {
        Assert.assertNotNull(client);
        Channel channel = client.getChannel("mychannel");

        BlockchainHexInfo blockchainHexInfo = ChannelQuery.queryBlockchainInfo(channel);
        System.out.println(JSON.toJSON(blockchainHexInfo));
        // {"previousBlockHash":"9336e360c283b68bd5d8d63a4ef7539db4bd6833f09a4aef09bc36d4a95ebedd","currentBlockHash":"5444288e7ffc393d55bc0647de395cf7dbe47aa973a3f104891d2ee1cd3e75a1","height":13}
        System.out.println(JSON.toJSON(ChannelQuery.queryPeersInfo(channel)));
        // [{"blockchain":{"previousBlockHash":"9336e360c283b68bd5d8d63a4ef7539db4bd6833f09a4aef09bc36d4a95ebedd","currentBlockHash":"5444288e7ffc393d55bc0647de395cf7dbe47aa973a3f104891d2ee1cd3e75a1","height":13},"name":"peer0.usoft.example.com","url":"grpcs://192.168.0.180:7051"}]
        String blockHash = blockchainHexInfo.getCurrentBlockHash();
        while(true) {
            BlockHexInfo blockHexInfo = ChannelQuery.queryBlockByHash(channel, blockHash);
            System.out.println(blockHash + " >> " + JSON.toJSON(blockHexInfo));
            if (blockHexInfo.getBlockNumber() == 0) {
                break;
            }
            blockHash = blockHexInfo.getPreviousHash();
        }
        // ...
        // d683269aa58a42d289db5db86d31f9f941bf1058bc07c9b69c3f5f7495fb9ebe >> {"previousHash":"2ef693e1ab91029909e52d12db92343c999ecbcfb3f1f0cae9dcbfe71fac9d1b","dataHash":"e91b15398865fb457a8f8a7c3296960faac70fb3b4a0e0cbd26ce7e0c5878e22","blockNumber":4,"transactionCount":0,"envelopes":[{"valid":true,"validationCode":0,"type":"ENVELOPE","nonce":"b627c38bc04c40ae06c7981c52af61964bf64e67ab1b1138","transactionID":"","timestamp":1536844063000}]}
        // 2ef693e1ab91029909e52d12db92343c999ecbcfb3f1f0cae9dcbfe71fac9d1b >> {"previousHash":"5e5e3f761cafcafc6b04b5505ba4871741f30dcabc9fdd4cc012cab7cdf4a2d1","dataHash":"87b710e41d35411849368d2bcd6a41206d273e7c4f07767405a4de9416cf67dd","blockNumber":3,"transactionCount":0,"envelopes":[{"valid":true,"validationCode":0,"type":"ENVELOPE","nonce":"c31cc23241fe551a01aebe761992a9ad9d34a04e63976c09","transactionID":"","timestamp":1536844058000}]}
        // 5e5e3f761cafcafc6b04b5505ba4871741f30dcabc9fdd4cc012cab7cdf4a2d1 >> {"previousHash":"ebfafa09f97c9bcf613b949de8d42c55c4021d90c9c712495c9814052b0a8813","dataHash":"ac8529072635362ea5e771ef241a08ef6a369e7a682f1b2d147fccec6abbf78d","blockNumber":2,"transactionCount":0,"envelopes":[{"valid":true,"validationCode":0,"type":"ENVELOPE","nonce":"b23f780d70ef55cd8dec1b811648e3e5914c9a1499e2c27e","transactionID":"","timestamp":1536844053000}]}
        // ebfafa09f97c9bcf613b949de8d42c55c4021d90c9c712495c9814052b0a8813 >> {"previousHash":"fa8006a616143b762dac0665490d9f72607d026e3ec84094ac830d926cd512a5","dataHash":"566f69c64d0b7712ed75fa6fe5b9af60279697b9d4a3eab56c5750b95c9df9d3","blockNumber":1,"transactionCount":0,"envelopes":[{"valid":true,"validationCode":0,"type":"ENVELOPE","nonce":"583f48974de2f299acd7dba0ee40687962d98af87f4cb565","transactionID":"","timestamp":1536844047000}]}
        // fa8006a616143b762dac0665490d9f72607d026e3ec84094ac830d926cd512a5 >> {"previousHash":"","dataHash":"b875482bf154c6d7d921fe783a49b6f6623b697a7b1c3a628542fd8775cd8f26","blockNumber":0,"transactionCount":0,"envelopes":[{"valid":true,"validationCode":0,"type":"ENVELOPE","nonce":"4517f1c97dca26454d298490f988ba7a31fc83463f373b36","transactionID":"","timestamp":1536844038000}]}
    }
}
