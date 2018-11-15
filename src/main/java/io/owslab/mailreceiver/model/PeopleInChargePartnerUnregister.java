package io.owslab.mailreceiver.model;

import javax.persistence.*;

@Entity
@Table(name = "people_in_charge_partner_unregister")
public class PeopleInChargePartnerUnregister {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "status", nullable = false)
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class Status {
        public static final int ALLOW_REGISTER = 1;
        public static final int AVOID_REGISTER = 2;
    }
}
