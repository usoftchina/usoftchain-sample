package org.springframework.fabric.query;

import org.hyperledger.fabric.sdk.BlockInfo;
import org.springframework.fabric.query.util.AsciiUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yingp
 * @date 2018/9/11
 */
public class TransactionActionInfo {

    private int responseStatus;
    private String responseMessage;
    private int endorsementsCount;
    private int status;
    private String payload;
    private String[] args;
    private List<EndorserHexInfo> endorsers;

    public TransactionActionInfo(BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo info) {
        this.responseStatus = info.getResponseStatus();
        this.responseMessage = info.getResponseMessage();
        this.endorsementsCount = info.getEndorsementsCount();
        this.status = info.getProposalResponseStatus();
        boolean isDeploy = "deploy".equals(new String(info.getChaincodeInputArgs(0)));
        if (isDeploy) {
            // 初始化的payload不处理
            this.payload = "";
        } else {
            this.payload = new String(info.getProposalResponsePayload(), Charset.defaultCharset());
        }

        int count = isDeploy ? 2 : info.getChaincodeInputArgsCount();
        this.args = new String[count];
        for (int i = 0; i < count; i++) {
            this.args[i] = new String(info.getChaincodeInputArgs(i), Charset.defaultCharset());
        }

        count = info.getEndorsementsCount();
        this.endorsers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            this.endorsers.add(new EndorserHexInfo(info.getEndorsementInfo(i)));
        }
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public int getEndorsementsCount() {
        return endorsementsCount;
    }

    public int getStatus() {
        return status;
    }

    public String getPayload() {
        return payload;
    }

    public String[] getArgs() {
        return args;
    }

    public List<EndorserHexInfo> getEndorsers() {
        return endorsers;
    }
}
