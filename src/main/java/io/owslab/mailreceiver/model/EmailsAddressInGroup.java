package io.owslab.mailreceiver.model;

import javax.persistence.*;

@Entity
@Table(name = "emails_address_in_group")
public class EmailsAddressInGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long groupId;
    private long peopleInChargeId;

    public EmailsAddressInGroup() {
    }


    public EmailsAddressInGroup(long groupId, long peopleInChargeId) {
        this.groupId = groupId;
        this.peopleInChargeId = peopleInChargeId;
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

    public long getPeopleInChargeId() {
        return peopleInChargeId;
    }

    public void setPeopleInChargeId(long peopleInChargeId) {
        this.peopleInChargeId = peopleInChargeId;
    }
}
