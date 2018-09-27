package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 9/25/18.
 */
public enum EncryptionProtocol {
    NONE(0, "なし"),
    SSL(1, "SSL/TLS"),
    STARTTLS(2, "STARTTLS");

    private final int value;
    private final String text;

    EncryptionProtocol(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static EncryptionProtocol fromValue(int value) {
        switch(value) {
            case 0:
                return NONE;
            case 1:
                return SSL;
            case 2:
                return STARTTLS;
        }
        return NONE;
    }
}
