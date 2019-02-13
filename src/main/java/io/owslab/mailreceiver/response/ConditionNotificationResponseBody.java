package io.owslab.mailreceiver.response;

import io.owslab.mailreceiver.dto.ConditionNotificationDTO;
import io.owslab.mailreceiver.model.ConditionNotification;

import java.util.List;

public class ConditionNotificationResponseBody extends AjaxResponseBody {
    private long sourceNotification;
    private long destinationNotification;
    private long matchingNotification;

    private List<ConditionNotificationDTO> sourceNotificationList;
    private List<ConditionNotificationDTO> destinationNotificationList;
    private List<ConditionNotificationDTO> matchingNotificationList;

    public ConditionNotificationResponseBody(String msg, boolean status, int errorCode) {
        super(msg, status);
    }

    public ConditionNotificationResponseBody(String msg) {
        super(msg, false);
    }

    public ConditionNotificationResponseBody() {
        this("");
    }

    public long getSourceNotification() {
        return sourceNotification;
    }

    public void setSourceNotification(long sourceNotification) {
        this.sourceNotification = sourceNotification;
    }

    public long getDestinationNotification() {
        return destinationNotification;
    }

    public void setDestinationNotification(long destinationNotification) {
        this.destinationNotification = destinationNotification;
    }

    public long getMatchingNotification() {
        return matchingNotification;
    }

    public void setMatchingNotification(long matchingNotification) {
        this.matchingNotification = matchingNotification;
    }

    public List<ConditionNotificationDTO> getSourceNotificationList() {
        return sourceNotificationList;
    }

    public void setSourceNotificationList(List<ConditionNotificationDTO> sourceNotificationList) {
        this.sourceNotificationList = sourceNotificationList;
    }

    public List<ConditionNotificationDTO> getDestinationNotificationList() {
        return destinationNotificationList;
    }

    public void setDestinationNotificationList(List<ConditionNotificationDTO> destinationNotificationList) {
        this.destinationNotificationList = destinationNotificationList;
    }

    public List<ConditionNotificationDTO> getMatchingNotificationList() {
        return matchingNotificationList;
    }

    public void setMatchingNotificationList(List<ConditionNotificationDTO> matchingNotificationList) {
        this.matchingNotificationList = matchingNotificationList;
    }
}