package io.owslab.mailreceiver.form;

import java.util.List;

/**
 * Created by khanhlvb on 8/6/18.
 */
public class TrashBoxForm {
    private List<String> msgIds;

    public TrashBoxForm(List<String> msgIds) {
        this.msgIds = msgIds;
    }

    public TrashBoxForm() {
    }

    public List<String> getMsgIds() {
        return msgIds;
    }

    public void setMsgIds(List<String> msgIds) {
        this.msgIds = msgIds;
    }
}
