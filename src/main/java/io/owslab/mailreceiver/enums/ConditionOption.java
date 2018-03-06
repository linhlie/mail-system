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
}
