package io.owslab.mailreceiver.enums;

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
    OTHER(10, "Other");

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
            case "Other":
            default:
                return OTHER;
        }
    }
}
