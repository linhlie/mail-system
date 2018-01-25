package io.owslab.mailreceiver.model;

import javax.persistence.*;

/**
 * Created by khanhlvb on 1/26/18.
 */
@Entity
@Table(name = "KeyValues")
public class EnviromentSetting {

    @Id
    @Column(name="[key]")
    private String key;

    @Column(name="[value]")
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EnviromentSetting() {
    }

    public EnviromentSetting(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

