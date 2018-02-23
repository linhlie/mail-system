package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Replace_Letters")
public class ReplaceLetter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String letter;

    @NotNull
    private int position;

    @NotNull
    @Column(name="[replace]")
    private int replace;

    @Transient
    private int remove;

    public ReplaceLetter() {}

    public ReplaceLetter(long id) {
        this.id = id;
    }

    public ReplaceLetter(String letter, int position, int replace) {
        this.letter = letter;
        this.position = position;
        this.replace = replace;
    }

    public ReplaceLetter(String letter, int position, int replace, int remove) {
        this.letter = letter;
        this.position = position;
        this.replace = replace;
        this.remove = remove;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getReplace() {
        return replace;
    }

    public void setReplace(int replace) {
        this.replace = replace;
    }

    public int getRemove() {
        return remove;
    }

    public void setRemove(int remove) {
        this.remove = remove;
    }
}
