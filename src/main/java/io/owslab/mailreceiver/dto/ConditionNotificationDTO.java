package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.ConditionNotification;
import io.owslab.mailreceiver.utils.ConvertDate;

public class ConditionNotificationDTO {
    private long id;
    private String fromAccount;
    private String toAccount;
    private String condition;
    private int conditionType;
    private String sentAt;
    private int status;

    public ConditionNotificationDTO(){

    }

    public ConditionNotificationDTO(ConditionNotification conditionNotification, String fromAccount, String toAccount){
        this.id  = conditionNotification.getId();
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.condition = conditionNotification.getCondition();
        this.conditionType = conditionNotification.getConditionType();
        this.sentAt = ConvertDate.convertDateToYYMMDDHHMM(conditionNotification.getSentAt());
        this.status = conditionNotification.getStatus();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
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

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}