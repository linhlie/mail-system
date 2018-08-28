package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 8/27/18.
 */
public enum CompanyType {
    LTD(1, "株式会社"),
    LIMITED(2, "有限会社"),
    GROUP(3, "合名会社"),
    JOINT_STOCK(4, "合資会社"),
    FOUNDATION(5, "財団法人"),
    CORPORATION(6, "社団法人"),
    OTHER(7, "その他");

    private final int value;
    private final String text;

    CompanyType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static CompanyType fromValue(int value) {
        switch(value) {
            case 1:
                return LTD;
            case 2:
                return LIMITED;
            case 3:
                return GROUP;
            case 4:
                return JOINT_STOCK;
            case 5:
                return FOUNDATION;
            case 6:
                return CORPORATION;
            case 7:
                return OTHER;
        }
        return OTHER;
    }

    public static CompanyType fromText(String text) {
        switch(text) {
            case "株式会社":
                return LTD;
            case "有限会社":
                return LIMITED;
            case "合名会社":
                return GROUP;
            case "合資会社":
                return JOINT_STOCK;
            case "財団法人":
                return FOUNDATION;
            case "社団法人":
                return CORPORATION;
            case "その他":
                return OTHER;
        }
        return OTHER;
    }
}
