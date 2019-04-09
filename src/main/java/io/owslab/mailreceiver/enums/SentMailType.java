package io.owslab.mailreceiver.enums;

public enum SentMailType {
    MATCHING_SOURCE(1, "「元へ」"),
    MATCHING_DESTINATION(2, "「先へ」"),
    REPLY_SOURCE(3, "「返信」"),
    REPLY_DESTINATION(4, "「返信」"),
    REPLY_EMAIL_MATCHING_ENGINEER(5, "DB「返信」"),
    SEND_TO_ENGINEER(6, "DB「技術者へ」"),
    SEND_VIA_INBOX(7, "[Inbox]"),
    SEND_MAIL_SCHEDULER(8, "スケジューラ"),
    OTHER(9, "Other");

    private final int value;
    private final String text;

    SentMailType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static SentMailType fromValue(int value) {
        switch(value) {
            case 1:
                return MATCHING_SOURCE;
            case 2:
                return MATCHING_DESTINATION;
            case 3:
                return REPLY_SOURCE;
            case 4:
                return REPLY_DESTINATION;
            case 5:
                return REPLY_EMAIL_MATCHING_ENGINEER;
            case 6:
                return SEND_TO_ENGINEER;
            case 7:
                return SEND_VIA_INBOX;
            case 8:
                return SEND_MAIL_SCHEDULER;
        }
        return OTHER;
    }
}
