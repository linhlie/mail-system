package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.enums.AlertLevel;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.PeopleInChargePartner;
import io.owslab.mailreceiver.utils.FullNumberRange;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.LinkedHashMap;
import java.util.List;

public class PreviewMailDTO {
    private long accountId;
    private String from;
    private String subject;
    private String to;
    private String sentAt;
    private String receivedAt;
    private String replyTo;
    private String alertLevel;
    private String alertContent;
    private String partnerName;
    private String peopleInChargeAlertLevel;
    private String peopleInChargeAlertContent;
    private String peopleInChargeName;
    private String peopleinChargeEmail;

    public PreviewMailDTO(Email email) {
        this.setAccountId(email.getAccountId());
        this.setFrom(email.getFrom());
        this.setSubject(email.getSubject());
        this.setTo(email.getTo());
        this.setReplyTo(email.getReplyTo());
        this.setSentAt(DateFormatUtils.format(email.getSentAt(), "yyyy-MM-dd HH:mm:ss", DetailMailDTO.TIME_ZONE, null));
        this.setReceivedAt(DateFormatUtils.format(email.getReceivedAt(), "yyyy-MM-dd HH:mm:ss", DetailMailDTO.TIME_ZONE, null));
    }

    public PreviewMailDTO(Email email, List<BusinessPartner> listPartner, LinkedHashMap<String, PeopleInChargePartner> lisPeople) {
        this.setAccountId(email.getAccountId());
        this.setFrom(email.getFrom());
        this.setSubject(email.getSubject());
        this.setTo(email.getTo());
        this.setReplyTo(email.getReplyTo());
        this.setSentAt(DateFormatUtils.format(email.getSentAt(), "yyyy-MM-dd HH:mm:ss", DetailMailDTO.TIME_ZONE, null));
        this.setReceivedAt(DateFormatUtils.format(email.getReceivedAt(), "yyyy-MM-dd HH:mm:ss", DetailMailDTO.TIME_ZONE, null));

        BusinessPartner businessPartner = null;
        PeopleInChargePartner people = null;
        String emailAddress = email.getFrom();
        if (emailAddress!=null && !emailAddress.equals("")){
            int index = emailAddress.indexOf("@");
            String domain = emailAddress.toLowerCase().substring(index+1);
            for(BusinessPartner partner : listPartner){
                if(domain.equalsIgnoreCase(partner.getDomain1()) || domain.equalsIgnoreCase(partner.getDomain2()) || domain.equalsIgnoreCase(partner.getDomain3())){
                    businessPartner = partner;
                    break;
                }
            }

            people = lisPeople.get(emailAddress.toLowerCase());
        }

        if(businessPartner != null){
            String alertLevel = AlertLevel.fromValue(businessPartner.getAlertLevel()).getText();
            this.alertLevel = alertLevel;
            this.alertContent =businessPartner.getAlertContent();
            this.partnerName =businessPartner.getName();
        }

        if(people != null){
            String alertLevel = AlertLevel.fromValue(people.getAlertLevel()).getText();
            this.peopleInChargeAlertLevel = alertLevel;
            this.peopleInChargeAlertContent =people.getAlertContent();
            this.peopleInChargeName = people.getLastName()+ "　" + people.getFirstName();
            this.peopleinChargeEmail = people.getEmailAddress();
        }
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

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getAlertContent() {
        return alertContent;
    }

    public void setAlertContent(String alertContent) {
        this.alertContent = alertContent;
    }

    public String getPeopleInChargeAlertLevel() {
        return peopleInChargeAlertLevel;
    }

    public void setPeopleInChargeAlertLevel(String peopleInChargeAlertLevel) {
        this.peopleInChargeAlertLevel = peopleInChargeAlertLevel;
    }

    public String getPeopleInChargeAlertContent() {
        return peopleInChargeAlertContent;
    }

    public void setPeopleInChargeAlertContent(String peopleInChargeAlertContent) {
        this.peopleInChargeAlertContent = peopleInChargeAlertContent;
    }

    public String getPeopleInChargeName() {
        return peopleInChargeName;
    }

    public void setPeopleInChargeName(String peopleInChargeName) {
        this.peopleInChargeName = peopleInChargeName;
    }

    public String getPeopleinChargeEmail() {
        return peopleinChargeEmail;
    }

    public void setPeopleinChargeEmail(String peopleinChargeEmail) {
        this.peopleinChargeEmail = peopleinChargeEmail;
    }
}