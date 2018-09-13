/*
 *
 *  Copyright 2017 IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.usoftchina.chain.test.cluster;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.chaincode.config.AbstractChaincodeConfiguration;
import org.springframework.data.chaincode.repository.config.EnableChaincodeRepositories;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author yingp
 */
@Configuration
@ComponentScan
@EnableChaincodeRepositories(basePackages = {"com.usoftchina.chain.repository"})
public class HuaslNetworkConfig extends AbstractChaincodeConfiguration {

    @Override
    @Bean(name = "peerLocations")
    public Map<String, String> peerLocations() {
        final Map<String, String> res = new HashMap<>();
        res.put("peer0.huasl.example.com", "grpcs://192.168.0.177:7051");
        return res;
    }

    @Override
    @Bean(name = "eventHubLocations")
    public Map<String, String> eventHubLocations() {
        final Map<String, String> res = new HashMap<>();
        res.put("peer0.huasl.example.com", "grpcs://192.168.0.177:7053");
        return res;
    }

    @Override
    @Bean(name = "ordererLocations")
    public Map<String, String> ordererLocations() {
        final Map<String, String> res = new HashMap<>();
        res.put("orderer.example.com", "grpcs://192.168.0.176:7050");
        return res;
    }

    @Bean(name = "ordererProperties")
    public Map<String, Properties> ordererProperties() throws IOException {
        final Map<String, Properties> propertiesMap = new HashMap<>();
        Properties orderer0Properties = new Properties();
        String ordererPemFileLocation = "classpath:cluster-network/crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem";
        File ordererPemFile = ResourceUtils.getFile(ordererPemFileLocation);

        orderer0Properties.setProperty("pemFile", ordererPemFile.getCanonicalPath());
        orderer0Properties.setProperty("hostnameOverride", "orderer.example.com");
        orderer0Properties.setProperty("sslProvider", "openSSL");
        orderer0Properties.setProperty("negotiationType", "TLS");
        orderer0Properties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);
        orderer0Properties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[] {false});

        propertiesMap.put("orderer.example.com", orderer0Properties);
        return propertiesMap;

    }

    @Bean(name = "peerProperties")
    public Map<String, Properties> peerProperties() throws IOException {
        Properties peer0Properties = new Properties();
        String peer0PemFileLocation = "classpath:cluster-network/crypto-config/peerOrganizations/huasl.example.com/peers/peer0.huasl.example.com/tls/ca.crt";
        File peer0PemFile = ResourceUtils.getFile(peer0PemFileLocation);
        peer0Properties.setProperty("pemFile", peer0PemFile.getCanonicalPath());
        peer0Properties.setProperty("hostnameOverride", "peer0.huasl.example.com");
        peer0Properties.setProperty("sslProvider", "openSSL");
        peer0Properties.setProperty("negotiationType", "TLS");
        peer0Properties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
        peer0Properties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});
        peer0Properties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[] {true});

        final Map<String, Properties> propertiesMap = new HashMap<>();
        propertiesMap.put("peer0.huasl.example.com", peer0Properties);
        return propertiesMap;
    }

    @Bean(name = "privateKeyLocation")
    public String privateKeyLocation() {
        return "cluster-network/crypto-config/peerOrganizations/huasl.example.com/users/Admin@huasl.example.com/msp"
                + "/keystore/1e9d3781f2cebba6d4c26b600a1537622452e5dad10125074857ad210eaabac3_sk";
    }

    @Bean(name = "userSigningCert")
    public String userSigningCert() {
        final String certificateFile = "classpath:cluster-network/crypto-config/peerOrganizations/huasl.example.com/users"
                + "/Admin@huasl.example.com/msp/signcerts/Admin@huasl.example.com-cert.pem";
        try (final InputStream in = new FileInputStream(ResourceUtils.getFile(certificateFile))) {
            return IOUtils.toString(in, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean(name = "mspId")
    public String mspId() {
        return "huaslMSP";
    }

    @Bean(name = "caCert")
    public String caCert() {
        final String certificateFile = "classpath:cluster-network/crypto-config/peerOrganizations/huasl.example.com/ca/ca.huasl.example.com-cert.pem";
        try (final InputStream in = new FileInputStream(ResourceUtils.getFile(certificateFile))) {
            return IOUtils.toString(in, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
