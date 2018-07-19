package io.owslab.mailreceiver.dto;


import io.owslab.mailreceiver.model.ReceiveRule;

/**
 * Created by khanhlvb on 7/19/18.
 */
public class ReceiveRuleDTO {
    private long id;

    private int type;

    private String name;

    public ReceiveRuleDTO(ReceiveRule rule) {
        this.setId(rule.getId());
        this.setType(rule.getType());
        this.setName(rule.getName());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
