package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 9/25/18.
 */
public enum MailProtocol {
    IMAP(0, "imap"),
    POP3(1, "pop3"),
    SMTP(2, "smtp");

    private final int value;
    private final String text;

    MailProtocol(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static MailProtocol fromValue(int value) {
        switch(value) {
            case 0:
                return IMAP;
            case 1:
                return POP3;
            case 2:
                return SMTP;
        }
        return IMAP;
    }
}
