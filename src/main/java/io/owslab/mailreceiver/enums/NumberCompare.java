package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 3/8/18.
 */
public enum NumberCompare {
    AUTOMATCH(1, ""),
    EQ(2, "等しい"),
    NE(3, "異なる"),
    GE(4, "以上"),
    GT(5, "超"),
    LE(6, "以下"),
    LT(7, "未満");

    private final int value;
    private final String text;

    NumberCompare(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static NumberCompare fromConditionOption(ConditionOption condition) {
        switch(condition) {
            case EQ:
            case WITHIN:
                return EQ;
            case NE:
                return NE;
            case GE:
                return GE;
            case GT:
                return GT;
            case LE:
                return LE;
            case LT:
                return LT;
        }
        return null;
    }

    public static NumberCompare fromReplace(int value) {
        switch(value) {
            case 0:
                return GE;
            case 1:
                return LE;
            case 2:
                return LT;
            case 3:
                return GT;
            case 4:
                return EQ;
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
