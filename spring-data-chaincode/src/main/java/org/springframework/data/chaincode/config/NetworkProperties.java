package org.springframework.data.chaincode.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author yingp
 * @date 2018/9/5
 */
@ConfigurationProperties(NetworkProperties.PREFIX)
public class NetworkProperties {
    public static final String PREFIX = "chaincode.network";

    private Map<String, String> ordererLocations;

    private Map<String, String> peerLocations;

    private Map<String, String> eventHubLocations;

    private String privateKeyLocation;

    private String mspId;

    private String keyStoreLocation;

    public Map<String, String> getOrdererLocations() {
        return ordererLocations;
    }

    public void setOrdererLocations(Map<String, String> ordererLocations) {
        this.ordererLocations = ordererLocations;
    }

    public Map<String, String> getPeerLocations() {
        return peerLocations;
    }

    public void setPeerLocations(Map<String, String> peerLocations) {
        this.peerLocations = peerLocations;
    }

    public Map<String, String> getEventHubLocations() {
        return eventHubLocations;
    }

    public void setEventHubLocations(Map<String, String> eventHubLocations) {
        this.eventHubLocations = eventHubLocations;
    }

    public String getPrivateKeyLocation() {
        return privateKeyLocation;
    }

    public void setPrivateKeyLocation(String privateKeyLocation) {
        this.privateKeyLocation = privateKeyLocation;
    }

    public String getMspId() {
        return mspId;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }

    public String getKeyStoreLocation() {
        return keyStoreLocation;
    }

    public void setKeyStoreLocation(String keyStoreLocation) {
        this.keyStoreLocation = keyStoreLocation;
    }
}
