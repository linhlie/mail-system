package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.EmailAccount;

public class EmailAccountToSendMailDTO extends EmailAccount {

    private String cc;
    private String greeting;

    public EmailAccountToSendMailDTO(){

    }

    public EmailAccountToSendMailDTO(EmailAccount account, String cc){
        this.setId(account.getId());
        this.setAccount(account.getAccount());
        this.setDisabled(account.isDisabled());
        this.setAlertSend(account.isAlertSend());
        this.setSignature(account.getSignature());
        this.setCc(cc);
    }

    public EmailAccountToSendMailDTO(EmailAccount account, String cc, String greeting){
        this.setId(account.getId());
        this.setAccount(account.getAccount());
        this.setDisabled(account.isDisabled());
        this.setAlertSend(account.isAlertSend());
        this.setSignature(account.getSignature());
        this.setCc(cc);
        this.greeting = greeting;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
