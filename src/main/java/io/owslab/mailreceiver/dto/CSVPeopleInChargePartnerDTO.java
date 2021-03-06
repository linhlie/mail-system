package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.enums.AlertLevel;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.PeopleInChargePartner;

import java.lang.reflect.Field;

/**
 * Created by khanhlvb on 9/5/18.
 */
public class CSVPeopleInChargePartnerDTO {
    private String partnerCode;
    private String domainPartner;
    private String lastName;
    private String firstName;
    private String department;
    private String position;
    private String emailAddress;
    private String emailInChargePartner;
    private String numberPhone1;
    private String numberPhone2;
    private String note;
    private String pause;
    private String alertLevel;
    private String alertContent;

    public CSVPeopleInChargePartnerDTO(){
    }

    public CSVPeopleInChargePartnerDTO(String partnerCode, String domainPartner, String lastName, String firstName, String department, String position, String emailAddress, String emailInChargePartner, String numberPhone1, String numberPhone2, String note, String pause) {
        this.partnerCode = partnerCode;
        this.domainPartner = domainPartner;
        this.lastName = lastName;
        this.firstName = firstName;
        this.department = department;
        this.position = position;
        this.emailAddress = emailAddress;
        this.emailInChargePartner = emailInChargePartner;
        this.numberPhone1 = numberPhone1;
        this.numberPhone2 = numberPhone2;
        this.note = note;
        this.pause = pause;
    }

    public CSVPeopleInChargePartnerDTO(PeopleInChargePartner people, BusinessPartner partner) {
        this.setPartnerCode(partner.getPartnerCode());
        this.setDomainPartner(partner.getDomain1());
        this.setLastName(people.getLastName());
        this.setFirstName(people.getFirstName());
        this.setDepartment(people.getDepartment());
        this.setPosition(people.getPosition());
        this.setEmailAddress(people.getEmailAddress());
        this.setEmailInChargePartner(Boolean.toString(people.isEmailInChargePartner()).toUpperCase());
        this.setNumberPhone1(people.getNumberPhone1());
        this.setNumberPhone2(people.getNumberPhone2());
        this.setNote(people.getNote());
        this.setPause(Boolean.toString(people.isPause()).toUpperCase());
        AlertLevel alert = AlertLevel.fromValue(people.getAlertLevel());
        if(alert.getValue()==0){
            this.setAlertLevel("");
        }else{
            this.setAlertLevel(alert.getText());
        }
        this.setAlertContent(people.getAlertContent());
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getDomainPartner() {
        return domainPartner;
    }

    public void setDomainPartner(String domainPartner) {
        this.domainPartner = domainPartner;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailInChargePartner() {
        return emailInChargePartner;
    }

    public void setEmailInChargePartner(String emailInChargePartner) {
        this.emailInChargePartner = emailInChargePartner;
    }

    public String getNumberPhone1() {
        return numberPhone1;
    }

    public void setNumberPhone1(String numberPhone1) {
        this.numberPhone1 = numberPhone1;
    }

    public String getNumberPhone2() {
        return numberPhone2;
    }

    public void setNumberPhone2(String numberPhone2) {
        this.numberPhone2 = numberPhone2;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPause() {
        return pause;
    }

    public void setPause(String pause) {
        this.pause = pause;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getAlertContent() {
        return alertContent;
    }

    public void setAlertContent(String alertContent) {
        this.alertContent = alertContent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(": ");
        for (Field f : getClass().getDeclaredFields()) {
            sb.append(f.getName());
            sb.append("=");
            try {
                sb.append(f.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append(", ");
        }
        return sb.toString();
    }

    public PeopleInChargePartner build(BusinessPartner partner){
        PeopleInChargePartner people = new PeopleInChargePartner();
        people.setLastName(this.lastName);
        people.setFirstName(this.firstName);
        people.setDepartment(this.department);
        people.setPosition(this.position);
        people.setEmailAddress(this.emailAddress);
        if(this.emailInChargePartner == null){
            people.setEmailInChargePartner(false);
        }else{
            boolean emailInCharge = this.emailInChargePartner.equalsIgnoreCase("TRUE");
            people.setEmailInChargePartner(emailInCharge);
        }
        people.setNumberPhone1(this.numberPhone1);
        people.setNumberPhone2(this.numberPhone2);
        people.setNote(this.note);
        if(this.pause == null){
            people.setPause(false);
        }else{
            boolean pause = this.pause.equalsIgnoreCase("TRUE");
            people.setPause(pause);
        }
        people.setPartnerId(partner.getId());
        AlertLevel alert = AlertLevel.fromText(this.alertLevel);
        people.setAlertLevel(alert.getValue());
        if(alert.getValue()==0){
            people.setAlertContent("");
        }else{
            people.setAlertContent(this.alertContent);
        }
        return people;
    }
}
