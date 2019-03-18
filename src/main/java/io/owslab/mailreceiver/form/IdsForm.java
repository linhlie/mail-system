package io.owslab.mailreceiver.form;

import java.util.List;

public class IdsForm {
    List<Long> listEmailGroupId;
    List<String> listEmailAddress;

    public List<Long> getListEmailGroupId() {
        return listEmailGroupId;
    }

    public void setListEmailGroupId(List<Long> listEmailGroupId) {
        this.listEmailGroupId = listEmailGroupId;
    }

    public List<String> getListEmailAddress() {
        return listEmailAddress;
    }

    public void setListEmailAddress(List<String> listEmailAddress) {
        this.listEmailAddress = listEmailAddress;
    }
}
