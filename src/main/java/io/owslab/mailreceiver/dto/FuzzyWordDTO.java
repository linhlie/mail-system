package io.owslab.mailreceiver.dto;

public class FuzzyWordDTO {
    private long id;
    private String word;
    private String wordExclusion;

    public FuzzyWordDTO(){

    }

    public FuzzyWordDTO(long id, String word, String wordExclusion){
        this.id = id;
        this.word = word;
        this.wordExclusion = wordExclusion;
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
        this.word = word;
    }

    public String getWordExclusion() {
        return wordExclusion;
    }

    public void setWordExclusion(String wordExclusion) {
        this.wordExclusion = wordExclusion;
    }
}
