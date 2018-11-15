package io.owslab.mailreceiver.dto;

public class EmailAccountEngineerDTO {
    private long id;
    private String account;
    private boolean disabled;
    private boolean alertSend;
    private String signature;
    private String greeting;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isAlertSend() {
        return alertSend;
    }

    public void setAlertSend(boolean alertSend) {
        this.alertSend = alertSend;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
