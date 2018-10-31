package io.owslab.mailreceiver.response;

import io.owslab.mailreceiver.model.Word;

import java.util.List;

public class FuzzyWordResponseBody extends AjaxResponseBody{

    private List<Word> listWord;

    public FuzzyWordResponseBody(String msg, boolean status) {
        super(msg, status);
    }

    public FuzzyWordResponseBody(String msg) {
        this(msg, false);
    }

    public FuzzyWordResponseBody() {
        this("");
    }

    public List<Word> getListWord() {
        return listWord;
    }

    public void setListWord(List<Word> listWord) {
        this.listWord = listWord;
    }
}
