package io.owslab.mailreceiver.form;

/**
 * Created by khanhlvb on 7/17/18.
 */
public class ReceiveRuleBundleForm {
    private String receiveMailType;
    private String receiveMailRule;
    private String saveToTrashBox;

    public String getReceiveMailType() {
        return receiveMailType;
    }

    public void setReceiveMailType(String receiveMailType) {
        this.receiveMailType = receiveMailType;
    }

    public String getReceiveMailRule() {
        return receiveMailRule;
    }

    public void setReceiveMailRule(String receiveMailRule) {
        this.receiveMailRule = receiveMailRule;
    }

    public String getSaveToTrashBox() {
        return saveToTrashBox;
    }

    public void setSaveToTrashBox(String saveToTrashBox) {
        this.saveToTrashBox = saveToTrashBox;
    }
}
