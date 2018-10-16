package io.owslab.mailreceiver.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by khanhlvb on 7/9/18.
 */
@Entity
@Table(name = "Click_Sent_Histories")
public class ClickSentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int type;
    private Date createdAt;
    private long accountId;

    public ClickSentHistory() {
    }

    public ClickSentHistory(long id){
        this.id = id;
    }

    public ClickSentHistory(int type, long accountId){
        this.type = type;
        this.createdAt = new Date();
        this.accountId = accountId;
    }

    public ClickSentHistory(int type, Date createdAt) {
        this.type = type;
        this.createdAt = createdAt;
    }
}

