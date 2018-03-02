package io.owslab.mailreceiver.utils;

/**
 * Created by khanhlvb on 3/2/18.
 */
public class SelectOption {
    private int value;
    private String text;

    public SelectOption(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public class Combine {
        public static final int AND = 0;
        public static final int OR = 1;
    }
}
