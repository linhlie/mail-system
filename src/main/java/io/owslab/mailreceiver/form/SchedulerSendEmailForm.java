package io.owslab.mailreceiver.form;

public class SchedulerSendEmailForm {
    private SendMailForm sendMailForm;
    private int typeSendEmail;
    private String dateSendMail;
    private String hourSendMail;

    public SendMailForm getSendMailForm() {
        return sendMailForm;
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

    public static class Type {
        public static final int NOW = 0;
        public static final int SEND_BY_HOUR = 1;
        public static final int SEND_BY_DAY = 2;
        public static final int SEND_BY_MONTH = 3;
    }

}
