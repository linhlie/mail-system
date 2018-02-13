package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Replace_Numbers")
public class ReplaceNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String character;

    @NotNull
    private int replaceValue;

    public ReplaceNumber() {
    }

    public ReplaceNumber(long id) {
        this.id = id;
    }

    public ReplaceNumber(String character, int replaceValue) {
        this.character = character;
        this.replaceValue = replaceValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public int getReplaceValue() {
        return replaceValue;
    }

    public void setReplaceValue(int replaceValue) {
        this.replaceValue = replaceValue;
    }

    public String getReplaceValueStr(){
        String replaceValueStr = Integer.toString(this.getReplaceValue());
        return replaceValueStr.substring(1);
    }
}