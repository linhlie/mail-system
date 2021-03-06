package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "Fuzzy_Words")
public class FuzzyWord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "word_id", referencedColumnName = "id", nullable = false)
    private Word originalWord;

    @ManyToOne
    @JoinColumn(name = "with_word_id", referencedColumnName = "id", nullable = false)
    private Word associatedWord;

    @NotNull
    private int fuzzyType;

    public FuzzyWord() {
    }

    public FuzzyWord(long id) {
        this.id = id;
    }

    public FuzzyWord(Word originalWord, Word associatedWord, int fuzzyType) {
        this.originalWord = originalWord;
        this.associatedWord = associatedWord;
        this.fuzzyType = fuzzyType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Word getOriginalWord() {
        return originalWord;
    }

    public void setOriginalWord(Word originalWord) {
        this.originalWord = originalWord;
    }

    public Word getAssociatedWord() {
        return associatedWord;
    }

    public void setAssociatedWord(Word associatedWord) {
        this.associatedWord = associatedWord;
    }

    public int getFuzzyType() {
        return fuzzyType;
    }

    public void setFuzzyType(int fuzzyType) {
        this.fuzzyType = fuzzyType;
    }

    public class Type {
        public static final int SAME = 1;
        public static final int EXCLUSION = 0;
    }
}
