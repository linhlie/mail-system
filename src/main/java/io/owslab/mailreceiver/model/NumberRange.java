package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by khanhlvb on 2/23/18.
 */
@Entity
@Table(name = "Number_Ranges")
public class NumberRange {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "letter_id", referencedColumnName = "id")
    private ReplaceLetter letter;

    @NotNull
    private String messageId;

    private int appearOrder;

    private Double number;

    public NumberRange() {
    }

    public NumberRange(long id) {
        this.id = id;
    }

    public NumberRange(ReplaceLetter letter, String messageId, int appearOrder, Double number) {
        this.letter = letter;
        this.messageId = messageId;
        this.appearOrder = appearOrder;
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ReplaceLetter getLetter() {
        return letter;
    }

    public void setLetter(ReplaceLetter letter) {
        this.letter = letter;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getAppearOrder() {
        return appearOrder;
    }

    public void setAppearOrder(int appearOrder) {
        this.appearOrder = appearOrder;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }
}
