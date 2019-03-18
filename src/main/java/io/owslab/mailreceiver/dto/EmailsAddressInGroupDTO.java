package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.EmailsAddressInGroup;
import io.owslab.mailreceiver.model.PeopleInChargePartner;

public class EmailsAddressInGroupDTO {
    private long id;
    private long groupId;
    private String name;
    private String emailAddress;

    public EmailsAddressInGroupDTO(){

    }

    public EmailsAddressInGroupDTO(EmailsAddressInGroup emailsAddressInGroup, PeopleInChargePartner peopleInChargePartner){
        this.id = emailsAddressInGroup.getId();
        this.groupId = emailsAddressInGroup.getGroupId();
        this.name = peopleInChargePartner.getPepleName();
        this.emailAddress = peopleInChargePartner.getEmailAddress();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
