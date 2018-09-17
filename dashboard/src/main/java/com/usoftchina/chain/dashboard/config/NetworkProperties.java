package com.usoftchina.chain.dashboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yingp
 * @date 2018/9/17
 */
@ConfigurationProperties(NetworkProperties.PREFIX)
public class NetworkProperties {
    public static final String PREFIX = "network";

    private List<String> channels;

    private Map<String, HostConfig> orderers = new HashMap<>();

    private Map<String, HostConfig> peers = new HashMap<>();

    private Map<String, String> events = new HashMap<>();

    private Cli cli;

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public Map<String, HostConfig> getOrderers() {
        return orderers;
    }

    public void setOrderers(Map<String, HostConfig> orderers) {
        this.orderers = orderers;
    }

    public Map<String, HostConfig> getPeers() {
        return peers;
    }

    public void setPeers(Map<String, HostConfig> peers) {
        this.peers = peers;
    }

    public Map<String, String> getEvents() {
        return events;
    }

    public void setEvents(Map<String, String> events) {
        this.events = events;
    }

    public Cli getCli() {
        return cli;
    }

    public void setCli(Cli cli) {
        this.cli = cli;
    }
}
