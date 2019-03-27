package io.owslab.mailreceiver.model;

import javax.persistence.*;

@Entity
@Table(name = "matching_condition_saved")
public class MatchingConditionSaved {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long accountCreatedId;
    private String conditionName;
    @Column(name="[condition]")
    private String condition;
    private int conditionType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccountCreatedId() {
        return accountCreatedId;
    }

    public void setAccountCreatedId(long accountCreatedId) {
        this.accountCreatedId = accountCreatedId;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
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

    public class Condition_Type {
        public static final int SOURCE_CONDITION = 1;
        public static final int DESTINATION_CONDITION = 2;
        public static final int MATCHING_CONDITION = 3;
    }
}
