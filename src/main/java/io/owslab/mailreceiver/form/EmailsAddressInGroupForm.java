package io.owslab.mailreceiver.form;

import java.util.List;

public class EmailsAddressInGroupForm {
    private long groupId;
    private List<Long> listPeopleId;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public List<Long> getListPeopleId() {
        return listPeopleId;
    }

    public void setListPeopleId(List<Long> listPeopleId) {
        this.listPeopleId = listPeopleId;
    }
}
