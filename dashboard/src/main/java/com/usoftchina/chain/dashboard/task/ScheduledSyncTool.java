package com.usoftchina.chain.dashboard.task;

import com.alibaba.fastjson.JSON;
import com.usoftchina.chain.dashboard.config.NetworkProperties;
import com.usoftchina.chain.dashboard.dao.BlockRepository;
import com.usoftchina.chain.dashboard.entity.Block;
import com.usoftchina.chain.dashboard.entity.Blockchain;
import com.usoftchina.chain.dashboard.entity.Transaction;
import com.usoftchina.chain.dashboard.entity.TransactionAction;
import com.usoftchina.chain.dashboard.dao.BlockchainRepository;
import com.usoftchina.chain.dashboard.dao.TransactionRepository;
import com.usoftchina.chain.dashboard.util.IteratorUtils;
import org.hyperledger.fabric.sdk.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.fabric.query.BlockHexInfo;
import org.springframework.fabric.query.BlockchainHexInfo;
import org.springframework.fabric.query.ChannelQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 定时读取区块数据后写入关系数据库
 *
 * @author yingp
 * @date 2018/9/14
 */
@Component
public class ScheduledSyncTool {
    @Autowired
    private BlockchainRepository blockchainRepository;
    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ChaincodeClient client;

    @Autowired
    private NetworkProperties networkProperties;

    @Scheduled(fixedDelay = 20000)
    @Transactional(rollbackFor = Exception.class)
    public void syncBlockInfo() throws Exception {
        Assert.notNull(client, "chaincode client not initialized");
        if (!CollectionUtils.isEmpty(networkProperties.getChannels())) {
            try {
                networkProperties.getChannels().forEach(
                        IteratorUtils.throwingConsumer(ch -> checkBlockchain(client.getChannel(ch))));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private void checkBlockchain(Channel channel) throws Exception {
        BlockchainHexInfo blockchainHexInfo = ChannelQuery.queryBlockchainInfo(channel);
        if (null != blockchainHexInfo) {
            Blockchain blockchain = blockchainRepository.findOne(channel.getName());
            boolean changed;
            if (null == blockchain) {
                blockchain = new Blockchain();
                blockchain.setChannel(channel.getName());
                changed = true;
            } else {
                changed = !blockchain.getCurrentBlockHash().equals(blockchainHexInfo.getCurrentBlockHash());
            }
            if (changed) {
                blockchain.setHeight(blockchainHexInfo.getHeight());
                blockchain.setCurrentBlockHash(blockchainHexInfo.getCurrentBlockHash());
                blockchain.setPreviousBlockHash(blockchainHexInfo.getPreviousBlockHash());
                blockchainRepository.saveOrUpdate(blockchain);

                checkBlock(channel, blockchainHexInfo.getCurrentBlockHash());
            }
        }
    }

    private void checkBlock(Channel channel, String blockHash) throws Exception {
        BlockHexInfo blockHexInfo = ChannelQuery.queryBlockByHash(channel, blockHash);
        Block block = blockRepository.findOne(blockHash);
        if (null == block) {
            block = new Block();
            block.setBlockHash(blockHash);
            block.setBlockNumber(blockHexInfo.getBlockNumber());
            block.setDataHash(blockHexInfo.getDataHash());
            block.setTransactionCount(blockHexInfo.getTransactionCount());
            block.setChannel(channel.getName());
            if (blockHexInfo.getBlockNumber() > 0) {
                block.setPreviousHash(blockHexInfo.getPreviousHash());
                checkBlock(channel, blockHexInfo.getPreviousHash());
            }

            blockRepository.save(block);
            checkTransaction(blockHash, blockHexInfo);
        }
    }

    private void checkTransaction(String blockHash, BlockHexInfo blockHexInfo) {
        if (!CollectionUtils.isEmpty(blockHexInfo.getEnvelopes())) {
            blockHexInfo.getEnvelopes().stream()
                    .filter(envelope -> null != envelope.getTransactionEnvelope())
                    .forEach(envelope -> {
                        Transaction transaction = transactionRepository.findOne(envelope.getTransactionID());
                        if (null == transaction) {
                            transaction = new Transaction();
                            transaction.setTransactionID(envelope.getTransactionID());
                            transaction.setNonce(envelope.getNonce());
                            transaction.setTimestamp(envelope.getTimestamp());
                            transaction.setValid(envelope.isValid());
                            transaction.setValidationCode(envelope.getValidationCode());
                            transaction.setBlockHash(blockHash);
                            if (!CollectionUtils.isEmpty(envelope.getTransactionEnvelope().getTransactionActions())) {
                                List<TransactionAction> actions = new ArrayList<>();
                                envelope.getTransactionEnvelope().getTransactionActions().forEach(ac -> {
                                    TransactionAction action = new TransactionAction();
                                    action.setArgs(JSON.toJSONString(ac.getArgs()));
                                    action.setEndorsementsCount(ac.getEndorsementsCount());
                                    action.setPayload(ac.getPayload());
                                    action.setResponseMessage(ac.getResponseMessage());
                                    action.setResponseStatus(ac.getResponseStatus());
                                    action.setStatus(ac.getStatus());
                                    action.setTransactionID(envelope.getTransactionID());
                                    actions.add(action);
                                });
                                transaction.setTransactionActions(actions);
                            }
                            transactionRepository.save(transaction);
                        }
                    });
        }
    }
}
