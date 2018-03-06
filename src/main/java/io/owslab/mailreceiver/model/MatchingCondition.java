package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by khanhlvb on 3/6/18.
 */
@Entity
@Table(name = "Matching_Conditions")
public class MatchingCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @Column(name="[group]")
    private boolean group;

    @NotNull
    private int combine;

    @NotNull
    private int item;

    @NotNull
    @Column(name="[condition]")
    private int condition;

    private String value;

    @NotNull
    private int type;

    @Transient // means "not a DB field"
    private int remove; // boolean flag

    public MatchingCondition() {
    }

    public MatchingCondition(long id) {
        this.id = id;
    }

    public MatchingCondition(boolean group, int combine, int item, int condition, String value, int type) {
        this.group = group;
        this.combine = combine;
        this.item = item;
        this.condition = condition;
        this.value = value;
        this.type = type;
    }

    public MatchingCondition(boolean group, int combine, int item, int condition, String value, int type, int remove) {
        this.group = group;
        this.combine = combine;
        this.item = item;
        this.condition = condition;
        this.value = value;
        this.type = type;
        this.remove = remove;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public int getCombine() {
        return combine;
    }

    public void setCombine(int combine) {
        this.combine = combine;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRemove() {
        return remove;
    }

    public void setRemove(int remove) {
        this.remove = remove;
    }

    public class Type {
        public static final int SOURCE = 0;
        public static final int DESTINATION = 1;
        public static final int MATCHING = 2;
    }
}
