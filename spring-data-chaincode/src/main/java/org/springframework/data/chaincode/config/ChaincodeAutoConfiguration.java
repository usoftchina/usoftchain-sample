package org.springframework.data.chaincode.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.chaincode.events.FabricEventsConfig;
import org.springframework.data.chaincode.repository.config.EnableChaincodeRepositories;
import org.springframework.data.chaincode.sdk.client.FabricClientConfig;

import java.util.Map;

/**
 * @author yingp
 * @date 2018/9/5
 */
@Configuration
@ComponentScan("org.springframework.data.chaincode")
@Import({FabricClientConfig.class, FabricEventsConfig.class})
@EnableConfigurationProperties({NetworkProperties.class})
@EnableChaincodeRepositories
public class ChaincodeAutoConfiguration {

    @Autowired
    private NetworkProperties networkProperties;

    /**
     * @return location of orderers accessible to client
     */
    @Bean(name = "ordererLocations")
    public Map<String, String> ordererLocations() {
        return networkProperties.getOrdererLocations();
    }

    /**
     * @return location of peers accessible to client
     */
    @Bean(name = "peerLocations")
    public Map<String, String> peerLocations() {
        return networkProperties.getPeerLocations();
    }

    /**
     * @return of event hubs, usually runs as part of peers, accessible to client
     */
    @Bean(name = "eventHubLocations")
    public Map<String, String> eventHubLocations() {
        return networkProperties.getEventHubLocations();
    }

    @Bean(name = "privateKeyLocation")
    public String privateKeyLocation() {
        return networkProperties.getPrivateKeyLocation();
    }

    @Bean(name = "mspId")
    public String mspId() {
        return networkProperties.getMspId();
    }

    @Bean(name = "keyStoreLocation")
    public String keyStoreLocation() {
        return networkProperties.getKeyStoreLocation();
    }
}
