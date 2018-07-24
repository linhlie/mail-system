package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "Words")
public class Word implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String word;

    @OneToMany(mappedBy = "originalWord", fetch = FetchType.EAGER)
    private Set<FuzzyWord> originalWords;

    @OneToMany(mappedBy = "associatedWord", fetch = FetchType.EAGER)
    private Set<FuzzyWord> associatedWords;

    public Word() {}

    public Word(long id) {
        this.id = id;
    }

    public Word(String word, Set<FuzzyWord> originalWords, Set<FuzzyWord> associatedWords) {
        this.word = word;
        this.originalWords = originalWords;
        this.associatedWords = associatedWords;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        if(word != null){
            this.word = word.toLowerCase();
        }
    }

    public Set<FuzzyWord> getOriginalWords() {
        return originalWords;
    }

    public void setOriginalWords(Set<FuzzyWord> originalWords) {
        this.originalWords = originalWords;
    }

    public Set<FuzzyWord> getAssociatedWords() {
        return associatedWords;
    }

    public void setAssociatedWords(Set<FuzzyWord> associatedWords) {
        this.associatedWords = associatedWords;
    }
}
