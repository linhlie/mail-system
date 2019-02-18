package io.owslab.mailreceiver.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "condition_notification")
public class ConditionNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long fromAccountId;
    private long toAccountId;
    @Column(name="[condition]")
    private String condition;
    private int conditionType;
    private Date sentAt;
    private int status;

    public ConditionNotification(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getConditionType() {
        return conditionType;
    }

    public void setConditionType(int conditionType) {
        this.conditionType = conditionType;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public class Status {
        public static final int NEW = 0;
        public static final int ACCEPT = 1;
        public static final int REJECT = 2;
    }

    public class Condition_Type {
        public static final int SOURCE_CONDITION = 0;
        public static final int DESTINATION_CONDITION = 1;
        public static final int MATCHING_CONDITION = 2;
        public static final int ENGINEER_MATCHING_CONDITION = 3;
        public static final int STATISTIC_CONDITION = 4;
    }
}