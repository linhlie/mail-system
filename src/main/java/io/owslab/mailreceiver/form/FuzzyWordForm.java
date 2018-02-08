package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;

/**
 * Created by khanhlvb on 1/24/18.
 */
public class FuzzyWordForm {
    private String original;

    private int fuzzyType;

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
