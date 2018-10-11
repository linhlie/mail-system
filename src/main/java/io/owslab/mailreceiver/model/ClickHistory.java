package io.owslab.mailreceiver.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by khanhlvb on 7/9/18.
 */
@Entity
@Table(name = "Click_Histories")
public class ClickHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int type;
    private Date createdAt;
    private long accountId;

    public ClickHistory() {
    }

    public ClickHistory(long id){
        this.id = id;
    }

    public ClickHistory(int type, long accountId){
        this.type = type;
        this.createdAt = new Date();
        this.accountId = accountId;
    }

    public class ClickType {
        public static final int EXTRACT_SOURCE = 1;
        public static final int EXTRACT_DESTINATION = 2;
        public static final int MATCHING = 3;
        public static final int EMAIL_MATCHING_ENGINEER = 4;
    }
}

