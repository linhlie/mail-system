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
        public static final int MATCHING_SOURCE = 4;
        public static final int MATCHING_DESTINATION = 5;
        public static final int REPLY_SOURCE = 6;
        public static final int REPLY_DESTINATION = 7;
        public static final int EMAIL_MATCHING_ENGINEER = 8;
        public static final int REPLY_EMAIL_MATCHING_ENGINEER = 9;
    }

    public class ClickTypeOld {
        public static final String EXTRACT_SOURCE = "元のみ抽出";
        public static final String EXTRACT_DESTINATION = "先のみ抽出";
        public static final String MATCHING = "マッチング";
        public static final String MATCHING_SOURCE = "マッチング後、「元へ」";
        public static final String MATCHING_DESTINATION = "マッチング後、「先へ」";
        public static final String REPLY_SOURCE = "元抽出後、「返信」";
        public static final String REPLY_DESTINATION = "先抽出後、「返信」";
        public static final String EMAIL_MATCHING_ENGINEER = "DB⇔メールマッチング";
        public static final String REPLY_EMAIL_MATCHING_ENGINEER = "DB⇔メールマッチング後、「返信」";
    }
}

