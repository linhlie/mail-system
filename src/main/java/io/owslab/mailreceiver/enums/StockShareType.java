package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 8/27/18.
 */
public enum StockShareType {
    BEFORE(1, "前"),
    AFTER(2, "後"),
    UNKNOWN(3, "無し");

    private final int value;
    private final String text;

    StockShareType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static StockShareType fromValue(int value) {
        switch(value) {
            case 1:
                return BEFORE;
            case 2:
                return AFTER;
            case 3:
                return UNKNOWN;
        }
        return UNKNOWN;
    }

    public static StockShareType fromText(String text) {
        switch(String.valueOf(text)) {
            case "前":
                return BEFORE;
            case "後":
                return AFTER;
            case "無し":
            default:
                    return UNKNOWN;
        }
    }
}
