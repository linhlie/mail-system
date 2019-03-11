package io.owslab.mailreceiver.model;

import io.owslab.mailreceiver.form.SchedulerSendEmailForm;
import io.owslab.mailreceiver.form.SendMailForm;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "Scheduler_Send_Email")
public class SchedulerSendEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotNull
    private long accountId;
    @NotNull
    @Column(name="[from]")
    private String from;
    @NotNull
    private String subject;
    @Column(name="[to]")
    private String to;
    private String cc;
    private String bcc;
    private Date sentAt;
    private boolean hasAttachment;
    private String body;
    private long accountSentMailId;

    private int typeSendEmail;
    private String dateSendEmail;
    private String hourSendEmail;
    private int status;

    public SchedulerSendEmail(){

    }

    public SchedulerSendEmail(SchedulerSendEmailForm form, EmailAccount emailAccount, long accountId){
        SendMailForm sendMailForm = form.getSendMailForm();
        this.id = form.getId();
        this.accountId = accountId;
        this.accountSentMailId = emailAccount.getId();
        this.from = emailAccount.getAccount();
        this.subject = sendMailForm.getSubject();
        this.to = sendMailForm.getReceiver();
        this.cc = sendMailForm.getCc();
        this.sentAt = new Date();
        if((sendMailForm.getUploadAttachment() != null && sendMailForm.getUploadAttachment().size() > 0) ||  (sendMailForm.getOriginAttachment() != null && sendMailForm.getOriginAttachment().size() > 0)){
            this.hasAttachment = true;
        }else{
            this.hasAttachment = false;
        }
        this.body = sendMailForm.getContent();
        this.typeSendEmail = form.getTypeSendEmail();
        this.dateSendEmail = form.getDateSendMail();
        this.hourSendEmail = form.getHourSendMail();
        this.status = Status.ACTIVE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getAccountSentMailId() {
        return accountSentMailId;
    }

    public void setAccountSentMailId(long accountSentMailId) {
        this.accountSentMailId = accountSentMailId;
    }

    public int getTypeSendEmail() {
        return typeSendEmail;
    }

    public void setTypeSendEmail(int typeSendEmail) {
        this.typeSendEmail = typeSendEmail;
    }

    public String getDateSendEmail() {
        return dateSendEmail;
    }

    public void setDateSendEmail(String dateSendEmail) {
        this.dateSendEmail = dateSendEmail;
    }

    public String getHourSendEmail() {
        return hourSendEmail;
    }

    public void setHourSendEmail(String hourSendEmail) {
        this.hourSendEmail = hourSendEmail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class Type {
        public static final int NOW = 0;
        public static final int SEND_BY_HOUR = 1;
        public static final int SEND_BY_DAY = 2;
        public static final int SEND_BY_MONTH = 3;
    }

    public static class Status {
        public static final int INACTIVE = 0;
        public static final int ACTIVE = 1;
        public static final int SENDING = 2;
        public static final int ERROR = 3;
    }

}
