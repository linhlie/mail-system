package io.owslab.mailreceiver.utils;

public class UserStatisticSentMail {
    private String username;
    private int quantity;

    public UserStatisticSentMail(String username, int quantity){
        this.username = username;
        this.quantity = quantity;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
