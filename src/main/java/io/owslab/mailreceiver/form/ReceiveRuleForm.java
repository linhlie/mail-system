package io.owslab.mailreceiver.form;

/**
 * Created by khanhlvb on 7/17/18.
 */
public class ReceiveRuleForm {
    private String name;
    private int type;
    private String rule;

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
}
