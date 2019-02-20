package io.owslab.mailreceiver.form;

import java.util.List;

public class EmailStatisticDetailForm {
    private List<String> listMessageId;

    public List<String> getListMessageId() {
        return listMessageId;
    }

    public void setListMessageId(List<String> listMessageId) {
        this.listMessageId = listMessageId;
    }
}
