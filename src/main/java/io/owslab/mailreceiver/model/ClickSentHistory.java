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
    private String type;
    private Date createdAt;

    public ClickSentHistory() {
    }

    public ClickSentHistory(long id){
        this.id = id;
    }

    public ClickSentHistory(String type){
        this.type = type;
        this.createdAt = new Date();
    }

    public ClickSentHistory(String type, Date createdAt) {
        this.type = type;
        this.createdAt = createdAt;
    }

    public class ClickSentType {
        public static final String MATCHING_SOURCE = ClickHistory.ClickType.MATCHING_SOURCE;
        public static final String MATCHING_DESTINATION = ClickHistory.ClickType.MATCHING_DESTINATION;
        public static final String REPLY_SOURCE = ClickHistory.ClickType.REPLY_SOURCE;
        public static final String REPLY_DESTINATION = ClickHistory.ClickType.REPLY_DESTINATION;
    }
}

