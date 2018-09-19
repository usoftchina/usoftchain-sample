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

package com.usoftchina.chain.dashboard.config;

import com.usoftchina.chain.dashboard.util.IteratorUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.chaincode.config.AbstractChaincodeConfiguration;
import org.springframework.data.chaincode.repository.config.EnableChaincodeRepositories;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author yingp
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(NetworkProperties.class)
@EnableChaincodeRepositories(basePackages = {"com.usoftchina.chaincode.repository"})
public class NetworkConfig extends AbstractChaincodeConfiguration {

    @Autowired
    private NetworkProperties networkProperties;

    @Override
    @Bean(name = "peerLocations")
    public Map<String, String> peerLocations() {
        final Map<String, String> res = new HashMap<>(1);
        if (!CollectionUtils.isEmpty(networkProperties.getPeers())) {
            networkProperties.getPeers().forEach((name, config) -> res.put(name, config.getUrl()));
        }
        return res;
    }

    @Override
    @Bean(name = "eventHubLocations")
    public Map<String, String> eventHubLocations() {
        return networkProperties.getEvents();
    }

    @Override
    @Bean(name = "ordererLocations")
    public Map<String, String> ordererLocations() {
        final Map<String, String> res = new HashMap<>(1);
        if (!CollectionUtils.isEmpty(networkProperties.getOrderers())) {
            networkProperties.getOrderers().forEach((name, config) -> res.put(name, config.getUrl()));
        }
        return res;
    }

    /**
     * fabric sdk必须传文件绝对路径
     *
     * @param srcFileName
     * @return
     * @throws IOException
     */
    private File copyToTempDirectory(String srcFileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(srcFileName);
        Path destFile = Paths.get(FileUtils.getTempDirectoryPath(), DigestUtils.md5DigestAsHex(srcFileName.getBytes()));
        Files.copy(resource.getInputStream(), destFile);
        return destFile.toFile();
    }

    @Bean(name = "ordererProperties")
    public Map<String, Properties> ordererProperties() throws IOException {
        final Map<String, Properties> propertiesMap = new HashMap<>(6);

        if (!CollectionUtils.isEmpty(networkProperties.getOrderers())) {
            networkProperties.getOrderers().forEach(IteratorUtils.throwingConsumer((name, config) -> {
                Properties ordererProperties = new Properties();
                ordererProperties.setProperty("pemFile", copyToTempDirectory(config.getPemFile()).getCanonicalPath());
                ordererProperties.setProperty("hostnameOverride", config.getHostnameOverride());
                ordererProperties.setProperty("sslProvider", config.getSslProvider());
                ordererProperties.setProperty("negotiationType", config.getNegotiationType());
                ordererProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);
                ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[]{false});

                propertiesMap.put(name, ordererProperties);
            }));
        }
        return propertiesMap;

    }

    @Bean(name = "peerProperties")
    public Map<String, Properties> peerProperties() throws IOException {
        final Map<String, Properties> propertiesMap = new HashMap<>(7);

        if (!CollectionUtils.isEmpty(networkProperties.getPeers())) {
            networkProperties.getPeers().forEach(IteratorUtils.throwingConsumer((name, config) -> {
                Properties peerProperties = new Properties();
                peerProperties.setProperty("pemFile", copyToTempDirectory(config.getPemFile()).getCanonicalPath());
                peerProperties.setProperty("hostnameOverride", config.getHostnameOverride());
                peerProperties.setProperty("sslProvider", config.getSslProvider());
                peerProperties.setProperty("negotiationType", config.getNegotiationType());
                peerProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
                peerProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});
                peerProperties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[]{true});

                propertiesMap.put(name, peerProperties);
            }));
        }
        return propertiesMap;
    }

    @Bean(name = "privateKeyLocation")
    public String privateKeyLocation() throws IOException {
        return copyToTempDirectory(networkProperties.getCli().getPrivateKey()).getCanonicalPath();
    }

    @Bean(name = "userSigningCert")
    public String userSigningCert() {
        try (final InputStream in = new ClassPathResource(networkProperties.getCli().getUserSigningCert()).getInputStream()) {
            return IOUtils.toString(in, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean(name = "mspId")
    public String mspId() {
        return networkProperties.getCli().getMspId();
    }

    @Bean(name = "caCert")
    public String caCert() {
        try (final InputStream in = new ClassPathResource(networkProperties.getCli().getCaCert()).getInputStream()) {
            return IOUtils.toString(in, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
