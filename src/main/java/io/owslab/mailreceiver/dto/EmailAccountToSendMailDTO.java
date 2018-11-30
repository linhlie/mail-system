package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.EmailAccount;

public class EmailAccountToSendMailDTO extends EmailAccount {

    private String cc;

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

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }
}
