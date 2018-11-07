package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.PeopleInChargePartner;

public class PeopleInChargePartnerDTO {
    private long id;
    private String name;
    private String department;
    private String position;
    private String emailAddress;
    private boolean pause;
    private boolean emailInChargePartner;

    public PeopleInChargePartnerDTO(){

    }

    public PeopleInChargePartnerDTO(PeopleInChargePartner people){
        this.id = people.getId();
        this.name = people.getLastName()+"　"+people.getFirstName();
        this.department = people.getDepartment();
        this.position = people.getPosition();
        this.emailAddress = people.getEmailAddress();
        this.pause = people.isPause();
        this.emailInChargePartner = people.isEmailInChargePartner();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isEmailInChargePartner() {
        return emailInChargePartner;
    }

    public void setEmailInChargePartner(boolean emailInChargePartner) {
        this.emailInChargePartner = emailInChargePartner;
    }
}
