package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 3/2/18.
 */

public enum ConditionOption {
    NONE(-1, ""),
    INC(0, "含む"),
    NINC(1, "含まない"),
    EQ(2, "等しい"),
    NE(3, "異なる"),
    GE(4, "以上"),
    GT(5, "超"),
    LE(6, "以下"),
    LT(7, "未満"),
    WITHIN(8, "範囲内");

    private final int value;
    private final String text;

    ConditionOption(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static ConditionOption fromValue(int value) {
        switch(value) {
            case -1:
                return NONE;
            case 0:
                return INC;
            case 1:
                return NINC;
            case 2:
                return EQ;
            case 3:
                return NE;
            case 4:
                return GE;
            case 5:
                return GT;
            case 6:
                return LE;
            case 7:
                return LT;
            case 8:
                return WITHIN;
        }
        return null;
    }

    public static ConditionOption fromOperator(String operator) {
        switch(operator) {
            case "contains":
                return INC;
            case "not_contains":
                return NINC;
            case "equal":
                return EQ;
            case "not_equal":
                return NE;
            case "greater_or_equal":
                return GE;
            case "greater":
                return GT;
            case "less_or_equal":
                return LE;
            case "less":
                return LT;
            case "in":
                return WITHIN;
        }
        return null;
    }
}
