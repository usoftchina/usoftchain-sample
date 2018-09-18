package com.usoftchina.chain.dashboard.controller;

import com.usoftchina.chain.dashboard.web.Result;
import org.hyperledger.fabric.sdk.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.fabric.query.ChannelQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yingp
 * @date 2018/9/18
 */
@RestController
@RequestMapping
public class NetworkController {

    @Autowired
    private ChaincodeClient client;

    @GetMapping("/channel")
    public ResponseEntity getChannelInfo(String name) throws Exception{
        Channel channel = client.getChannel(name);
        ModelMap map = new ModelMap("name", channel.getName());
        map.put("orderers", ChannelQuery.queryOrderersInfo(channel));
        map.put("peers", ChannelQuery.queryPeersInfo(channel));
        return Result.ok(map);
    }
}
