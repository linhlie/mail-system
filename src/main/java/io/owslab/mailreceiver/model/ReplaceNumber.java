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
    @Column(name="[character]")
    private String character;

    @NotNull
    private int replaceValue;

    @Transient // means "not a DB field"
    private int remove; // boolean flag

    @Transient
    private String replaceValueStr;

    public ReplaceNumber() {
    }

    public ReplaceNumber(long id) {
        this.id = id;
    }

    public ReplaceNumber(String character, int replaceValue) {
        this.character = character;
        this.replaceValue = replaceValue;
    }

    public ReplaceNumber(ReplaceNumber other) {
        this.character = other.getCharacter();
        this.replaceValue = other.getReplaceValue();
    }

    public ReplaceNumber(String character, int replaceValue, int remove) {
        this.character = character;
        this.replaceValue = replaceValue;
        this.remove = remove;
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

    public void setReplaceValue(String replaceValueStr) {
        replaceValueStr = replaceValueStr.replaceAll("\\s+","");
        replaceValueStr = replaceValueStr.replaceAll(",", "");
        replaceValueStr = "1" + replaceValueStr;
        this.replaceValue = Integer.parseInt(replaceValueStr);
    }

    public String getReplaceValueStr(){
        String replaceValueStr = Integer.toString(this.getReplaceValue());
        return replaceValueStr.substring(1);
    }

    public String getReplaceValueStrFromRaw(){
        return this.replaceValueStr;
    }

    public void setReplaceValueStr(String replaceValueStr) {
        this.replaceValueStr = replaceValueStr;
    }

    public int getRemove() {
        return remove;
    }

    public void setRemove(int remove) {
        this.remove = remove;
    }
}