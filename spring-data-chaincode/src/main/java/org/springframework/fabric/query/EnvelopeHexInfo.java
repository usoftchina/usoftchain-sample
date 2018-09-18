package org.springframework.fabric.query;

import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.BlockInfo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yingp
 * @date 2018/9/11
 */
public class EnvelopeHexInfo implements Serializable {
    private String transactionID;
    private String nonce;
    private byte validationCode;
    private Date timestamp;
    private BlockInfo.EnvelopeType type;
    private boolean valid;
    private TransactionEnvelopeInfo transactionEnvelope;


    public EnvelopeHexInfo(BlockInfo.EnvelopeInfo info) {
        this.transactionID = info.getTransactionID();
        this.validationCode = info.getValidationCode();
        this.nonce = Hex.toHexString(info.getNonce());
        this.timestamp = info.getTimestamp();
        this.type = info.getType();
        this.valid = info.isValid();

        if (info.getType() == BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE) {
            BlockInfo.TransactionEnvelopeInfo txInfo = (BlockInfo.TransactionEnvelopeInfo) info;
            this.transactionEnvelope = new TransactionEnvelopeInfo(txInfo);
        }
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getNonce() {
        return nonce;
    }

    public byte getValidationCode() {
        return validationCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public BlockInfo.EnvelopeType getType() {
        return type;
    }

    public boolean isValid() {
        return valid;
    }

    public TransactionEnvelopeInfo getTransactionEnvelope() {
        return transactionEnvelope;
    }
}
