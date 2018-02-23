package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by khanhlvb on 1/24/18.
 */
public class FuzzyWordForm {

    @NotBlank
    private String original;

    private int fuzzyType;

    @NotBlank
    private String associatedWord;

    public FuzzyWordForm() {
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public int getFuzzyType() {
        return fuzzyType;
    }

    public void setFuzzyType(int fuzzyType) {
        this.fuzzyType = fuzzyType;
    }

    public String getAssociatedWord() {
        return associatedWord;
    }

    public void setAssociatedWord(String associatedWord) {
        this.associatedWord = associatedWord;
    }
}
