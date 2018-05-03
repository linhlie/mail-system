package io.owslab.mailreceiver.utils;

/**
 * Created by khanhlvb on 5/3/18.
 */
public class KeyWordItem {
    private String word;
    private String keyWord;

    public KeyWordItem(String word) {
        this.word = word;
    }

    public KeyWordItem(String word, String keyWord) {
        this.word = word;
        this.keyWord = keyWord;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}
