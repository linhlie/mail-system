package io.owslab.mailreceiver.model;

import javax.persistence.*;

@Entity
@Table(name = "email_address_group")
public class EmailAddressGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String groupName;
    private long accountCreateId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getAccountCreateId() {
        return accountCreateId;
    }

    public void setAccountCreateId(long accountCreateId) {
        this.accountCreateId = accountCreateId;
    }
}
