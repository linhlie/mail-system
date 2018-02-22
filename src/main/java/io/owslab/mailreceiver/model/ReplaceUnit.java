package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Replace_Units")
public class ReplaceUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String unit;

    @NotNull
    private String replaceUnit;

    @Transient
    private int remove;

    public ReplaceUnit() {}

    public ReplaceUnit(long id) {
        this.id = id;
    }

    public ReplaceUnit(String unit, String replaceUnit) {
        this.unit = unit;
        this.replaceUnit = replaceUnit;
    }

    public ReplaceUnit(String unit, String replaceUnit, int remove) {
        this.unit = unit;
        this.replaceUnit = replaceUnit;
        this.remove = remove;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getReplaceUnit() {
        return replaceUnit;
    }

    public void setReplaceUnit(String replaceUnit) {
        this.replaceUnit = replaceUnit;
    }

    public int getRemove() {
        return remove;
    }

    public void setRemove(int remove) {
        this.remove = remove;
    }
}