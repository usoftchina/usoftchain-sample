package org.springframework.fabric.query;

import org.hyperledger.fabric.sdk.BlockInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yingp
 * @date 2018/9/11
 */
public class TransactionEnvelopeInfo {

    private byte validationCode;
    private boolean valid;
    private List<TransactionActionInfo> transactionActions;

    public TransactionEnvelopeInfo(BlockInfo.TransactionEnvelopeInfo info) {
        this.validationCode = info.getValidationCode();
        this.valid = info.isValid();

        int count = info.getTransactionActionInfoCount();
        this.transactionActions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            this.transactionActions.add(new TransactionActionInfo(info.getTransactionActionInfo(i)));
        }
    }

    public byte getValidationCode() {
        return validationCode;
    }

    public boolean isValid() {
        return valid;
    }

    public List<TransactionActionInfo> getTransactionActions() {
        return transactionActions;
    }
}
