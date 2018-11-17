package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 8/27/18.
 */
public enum AlertLevel {
    NONE(0, "無し"),
    LOW(1, "底"),
    MEDIUM(2, "中"),
    HIGH(3, "高");

    private final int value;
    private final String text;

    AlertLevel(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static AlertLevel fromValue(int value) {
        switch(value) {
            case 0:
                return NONE;
            case 1:
                return LOW;
            case 2:
                return MEDIUM;
            case 3:
                return HIGH;
        }
        return NONE;
    }

    public static AlertLevel fromText(String text) {
        switch(String.valueOf(text)) {
            case "無し":
                return NONE;
            case "底":
                return LOW;
            case "中":
                return MEDIUM;
            case "高":
                return HIGH;
            default:
                return NONE;
        }
    }
}
