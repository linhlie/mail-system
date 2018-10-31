package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
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

    @Column(name="`group_word`")
    private String groupWord;

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getGroupWord() { return groupWord; }

    public void setGroupWord(String groupWord) { this.groupWord = groupWord; }
}
