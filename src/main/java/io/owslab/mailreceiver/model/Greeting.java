package io.owslab.mailreceiver.model;

import javax.persistence.*;

@Entity
@Table(name = "greeting")
public class Greeting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long accountCreatedId;
    private long emailAccountId;
    private String title;
    private String greeting;
    private int greetingType;
    private boolean active;

    public Greeting(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccountCreatedId() {
        return accountCreatedId;
    }

    public void setAccountCreatedId(long accountCreatedId) {
        this.accountCreatedId = accountCreatedId;
    }

    public long getEmailAccountId() {
        return emailAccountId;
    }

    public void setEmailAccountId(long emailAccountId) {
        this.emailAccountId = emailAccountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public int getGreetingType() {
        return greetingType;
    }

    public void setGreetingType(int greetingType) {
        this.greetingType = greetingType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
