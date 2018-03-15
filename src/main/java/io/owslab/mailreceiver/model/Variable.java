package io.owslab.mailreceiver.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by khanhlvb on 3/15/18.
 */
@Entity
public class Variable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String variable_name;

    private String value;

    public Variable() {
    }

    public Variable(String value) {
        this.value = value;
    }

    public String getVariable_name() {
        return variable_name;
    }

    public void setVariable_name(String variable_name) {
        this.variable_name = variable_name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
