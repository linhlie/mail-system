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

    private boolean hidden;

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

    public ReplaceLetter(String letter, int position, int replace, boolean hidden) {
        this.letter = letter;
        this.position = position;
        this.replace = replace;
        this.hidden = hidden;
    }

    public ReplaceLetter(String letter, int position, int replace, int remove, boolean hidden) {
        this.letter = letter;
        this.position = position;
        this.replace = replace;
        this.remove = remove;
        this.hidden = hidden;
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

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public class Replace {
        public static final int NONE = 4;
        public static final int GE = 0;
        public static final int LE = 1;
        public static final int LT = 2;
        public static final int GT = 3;
    }

    public class Position {
        public static final int BF = 0;
        public static final int AF = 1;
    }

    public class Hidden {
        public static final boolean TRUE = true;
        public static final boolean FALSE = false;
    }
}
