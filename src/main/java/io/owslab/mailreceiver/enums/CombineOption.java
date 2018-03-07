package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 3/2/18.
 */
public enum CombineOption {
    NONE(-1, ""),
    AND(0, "AND"),
    OR(1, "OR");

    private final int value;
    private final String text;

    CombineOption(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static CombineOption fromValue(int value) {
        switch(value) {
            case -1:
                return NONE;
            case 0:
                return AND;
            case 1:
                return OR;
        }
        return null;
    }
}
