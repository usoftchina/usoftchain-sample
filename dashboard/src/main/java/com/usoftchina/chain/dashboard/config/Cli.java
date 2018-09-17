package com.usoftchina.chain.dashboard.config;

/**
 * @author yingp
 * @date 2018/9/14
 */
public class Cli {
    private String privateKey;
    private String userSigningCert;
    private String mspId;
    private String caCert;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getUserSigningCert() {
        return userSigningCert;
    }

    public void setUserSigningCert(String userSigningCert) {
        this.userSigningCert = userSigningCert;
    }

    public String getMspId() {
        return mspId;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }

    public String getCaCert() {
        return caCert;
    }

    public void setCaCert(String caCert) {
        this.caCert = caCert;
    }
}
