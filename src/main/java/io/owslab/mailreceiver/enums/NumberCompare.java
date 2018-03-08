package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 3/8/18.
 */
public enum NumberCompare {
    EQ(2),
    NE(3),
    GE(4),
    GT(5),
    LE(6),
    LT(7);

    private final int value;

    NumberCompare(int value) {
        this.value = value;
    }

    public static NumberCompare fromConditionOption(ConditionOption condition) {
        switch(condition) {
            case EQ:
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
}
