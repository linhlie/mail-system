package io.owslab.mailreceiver.response;

import io.owslab.mailreceiver.dto.DetailMailDTO;

import java.util.List;

/**
 * Created by khanhlvb on 7/9/18.
 */
public class DashboardResponseBody extends AjaxResponseBody {
    private String latestReceive;
    private List<String> clickCount;
    private List<String> receiveMailNumber;
    private List<String> sendPerClick;

    public DashboardResponseBody(String msg, boolean status, int errorCode) {
        super(msg, status);
    }

    public DashboardResponseBody(String msg) {
        super(msg, false);
    }

    public DashboardResponseBody() {
        this("");
    }

    public String getLatestReceive() {
        return latestReceive;
    }

    public void setLatestReceive(String latestReceive) {
        this.latestReceive = latestReceive;
    }

    public List<String> getClickCount() {
        return clickCount;
    }

    public void setClickCount(List<String> clickCount) {
        this.clickCount = clickCount;
    }

    public List<String> getReceiveMailNumber() {
        return receiveMailNumber;
    }

    public void setReceiveMailNumber(List<String> receiveMailNumber) {
        this.receiveMailNumber = receiveMailNumber;
    }

    public List<String> getSendPerClick() {
        return sendPerClick;
    }

    public void setSendPerClick(List<String> sendPerClick) {
        this.sendPerClick = sendPerClick;
    }
}