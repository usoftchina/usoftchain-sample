package com.usoftchina.chain.dashboard.config;

/**
 * @author yingp
 * @date 2018/9/14
 */
public class HostConfig {
    private String url;
    private String pemFile;
    private String hostnameOverride;
    private String sslProvider;
    private String negotiationType;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPemFile() {
        return pemFile;
    }

    public void setPemFile(String pemFile) {
        this.pemFile = pemFile;
    }

    public String getHostnameOverride() {
        return hostnameOverride;
    }

    public void setHostnameOverride(String hostnameOverride) {
        this.hostnameOverride = hostnameOverride;
    }

    public String getSslProvider() {
        return sslProvider;
    }

    public void setSslProvider(String sslProvider) {
        this.sslProvider = sslProvider;
    }

    public String getNegotiationType() {
        return negotiationType;
    }

    public void setNegotiationType(String negotiationType) {
        this.negotiationType = negotiationType;
    }
}
