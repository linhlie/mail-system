package io.owslab.mailreceiver.model;

import javax.persistence.*;

@Entity
@Table(name = "people_in_charge_partner")
public class PeopleInChargePartner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String lastName;
    private String firstName;
    private String department;
    private String position;
    private String emailAddress;
    private boolean emailInChargePartner;
    private String numberPhone1;
    private String numberPhone2;
    private String note;
    private boolean pause;
    private long partnerId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public boolean isEmailInChargePartner() {
        return emailInChargePartner;
    }

    public void setEmailInChargePartner(boolean emailInChargePartner) {
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

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }
}
