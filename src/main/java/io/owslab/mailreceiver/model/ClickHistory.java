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
    private String type;
    private Date createdAt;

    public ClickHistory() {
    }

    public ClickHistory(long id){
        this.id = id;
    }

    public ClickHistory(String type){
        this.type = type;
        this.createdAt = new Date();
    }

    public ClickHistory(String type, Date createdAt) {
        this.type = type;
        this.createdAt = createdAt;
    }

    public class ClickType {
        public static final String EXTRACT_SOURCE = "元のみ抽出";
        public static final String EXTRACT_DESTINATION = "先のみ抽出";
        public static final String MATCHING = "マッチング";
        public static final String MATCHING_SOURCE = "マッチング後、「元へ」";
        public static final String MATCHING_DESTINATION = "マッチング後、「先へ」";
        public static final String REPLY_SOURCE = "元抽出後、「返信」";
        public static final String REPLY_DESTINATION = "先抽出後、「返信」";
    }
}
