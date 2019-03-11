package io.owslab.mailreceiver.form;

public class SchedulerSendEmailForm {
    private long id;
    private SendMailForm sendMailForm;
    private int typeSendEmail;
    private String dateSendMail;
    private String hourSendMail;

    public SendMailForm getSendMailForm() {
        return sendMailForm;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSendMailForm(SendMailForm sendMailForm) {
        this.sendMailForm = sendMailForm;
    }

    public int getTypeSendEmail() {
        return typeSendEmail;
    }

    public void setTypeSendEmail(int typeSendEmail) {
        this.typeSendEmail = typeSendEmail;
    }

    public String getDateSendMail() {
        return dateSendMail;
    }

    public void setDateSendMail(String dateSendMail) {
        this.dateSendMail = dateSendMail;
    }

    public String getHourSendMail() {
        return hourSendMail;
    }

    public void setHourSendMail(String hourSendMail) {
        this.hourSendMail = hourSendMail;
    }

}
