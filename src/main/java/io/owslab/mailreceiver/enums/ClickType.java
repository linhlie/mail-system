package io.owslab.mailreceiver.enums;

import io.owslab.mailreceiver.model.Greeting;

public enum ClickType {
    EXTRACT_SOURCE(1, "元のみ抽出"),
    EXTRACT_DESTINATION(2, "先のみ抽出"),
    MATCHING(3, "マッチング"),
    MATCHING_SOURCE(4, "マッチング後、「元へ」"),
    MATCHING_DESTINATION(5, "マッチング後、「先へ」"),
    REPLY_SOURCE(6, "元抽出後、「返信」"),
    REPLY_DESTINATION(7, "先抽出後、「返信」"),
    EMAIL_MATCHING_ENGINEER(8, "DB⇔メールマッチング"),
    REPLY_EMAIL_MATCHING_ENGINEER(9, "DB⇔メールマッチング後、「返信」"),
    SEND_TO_ENGINEER(10, "DB⇔メールマッチング後、「技術者へ」"),
    REPLY_EMAIL_STATISTIC(11, "REPLY_EMAIL_STATISTIC"),
    REPLY_EMAIL_VIA_INBOX(12, "[Inbox]"),
    SEND_EMAIL_SCHEDULE(13, "[Schedule]"),
    OTHER(14, "Other");

    private final int value;
    private final String text;

    ClickType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static ClickType fromValue(int value) {
        switch(value) {
            case 1:
                return EXTRACT_SOURCE;
            case 2:
                return EXTRACT_DESTINATION;
            case 3:
                return MATCHING;
            case 4:
                return MATCHING_SOURCE;
            case 5:
                return MATCHING_DESTINATION;
            case 6:
                return REPLY_SOURCE;
            case 7:
                return REPLY_DESTINATION;
            case 8:
                return EMAIL_MATCHING_ENGINEER;
            case 9:
                return REPLY_EMAIL_MATCHING_ENGINEER;
            case 10:
                return SEND_TO_ENGINEER;
            case 11:
                return REPLY_EMAIL_STATISTIC;
            case 12:
                return REPLY_EMAIL_VIA_INBOX;
            case 13:
                return SEND_EMAIL_SCHEDULE;
        }
        return OTHER;
    }

    public static ClickType fromText(String text) {
        switch(String.valueOf(text)) {
            case "元のみ抽出":
                return EXTRACT_SOURCE;
            case "先のみ抽出":
                return EXTRACT_DESTINATION;
            case "マッチング":
                return MATCHING;
            case "マッチング後、「元へ」":
                return MATCHING_SOURCE;
            case "マッチング後、「先へ」":
                return MATCHING_DESTINATION;
            case "元抽出後、「返信」":
                return REPLY_SOURCE;
            case "先抽出後、「返信」":
                return REPLY_DESTINATION;
            case "DB⇔メールマッチング":
                return EMAIL_MATCHING_ENGINEER;
            case "DB⇔メールマッチング後、「返信」":
                return REPLY_EMAIL_MATCHING_ENGINEER;
            case "DB⇔メールマッチング後、「技術者へ」":
                return SEND_TO_ENGINEER;
            case "REPLY_EMAIL_STATISTIC":
                return REPLY_EMAIL_STATISTIC;
            case "[Inbox]":
                return REPLY_EMAIL_VIA_INBOX;
            case "[Schedule]":
                return SEND_EMAIL_SCHEDULE;
            case "Other":
            default:
                return OTHER;
        }
    }

    public static int getGreetingType(int clickType){
        switch (clickType){
            case 4:
                return Greeting.Type.MATCHING_SOURCE;
            case 5:
                return Greeting.Type.MATCHING_DESTINATION;
            case 6:
            case 7:
            case 9:
            case 11:
                return Greeting.Type.REPLY;
            case 10:
                return Greeting.Type.SEND_TO_ENGINEER;
            case 12:
            case 13:
                return Greeting.Type.SEND_TO_MULTIL_EMAIL_ADDRESS;
        }
        return Greeting.Type.REPLY;
    }
}
