package io.owslab.mailreceiver.model;

import io.owslab.mailreceiver.form.ReceiveRuleForm;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by khanhlvb on 7/17/18.
 */
@Entity
@Table(name = "Receive_Rules")
public class ReceiveRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String name;

    private int type;

    private String rule;

    private Date lastUpdate;

    public ReceiveRule() {

    }

    public ReceiveRule(long id) {
        this.id = id;
    }

    public ReceiveRule(String name, int type, String rule, Date lastUpdate) {
        this.name = name;
        this.type = type;
        this.rule = rule;
        this.lastUpdate = lastUpdate;
    }

    public ReceiveRule(ReceiveRuleForm form) {
        this.setName(form.getName());
        this.setType(form.getType());
        this.setRule(form.getRule());
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
