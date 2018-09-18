package org.springframework.fabric.query;

import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.BlockInfo;

import java.io.Serializable;

/**
 * @author yingp
 * @date 2018/9/11
 */
public class EndorserHexInfo implements Serializable {

    private String id;
    private String mspId;
    private String signature;

    public EndorserHexInfo(BlockInfo.EndorserInfo info) {
        this.id = info.getId();
        this.mspId = info.getMspid();
        this.signature = Hex.toHexString(info.getSignature());
    }

    public String getId() {
        return id;
    }

    public String getMspId() {
        return mspId;
    }

    public String getSignature() {
        return signature;
    }
}
