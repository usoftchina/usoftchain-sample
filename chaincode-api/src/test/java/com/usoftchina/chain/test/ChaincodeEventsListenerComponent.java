package com.usoftchina.chain.test;

import com.usoftchina.chain.repository.StockRepository;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEvent;
import org.springframework.data.chaincode.events.BlockEventListener;
import org.springframework.data.chaincode.events.ChaincodeEventListener;
import org.springframework.stereotype.Component;

/**
 * @author yingp
 * @date 2018/9/10
 */
@Component
public class ChaincodeEventsListenerComponent {
    public int blockEvents;
    public int ccEvents;

    @BlockEventListener(channel = "mychannel")
    public void hanldeBlockEvent(BlockEvent event) {
        System.out.println(">>>> Got block event " + event.getPeer().getName());
        blockEvents++;
    }

    @ChaincodeEventListener(chaincode = StockRepository.class)
    public void handleChaincodeEventInEventsRepo(ChaincodeEvent event) {
        System.out.println(">>>> Got chaincode event " + event.getChaincodeId());
        ccEvents++;
    }
}
