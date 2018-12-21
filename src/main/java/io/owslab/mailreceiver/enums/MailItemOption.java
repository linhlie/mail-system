package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 3/2/18.
 */
public enum MailItemOption {
    NONE(-1, ""),
    SENDER(0, "送信者"),
    RECEIVER(1, "受信者"),
    SUBJECT(2, "件名"),
    BODY(3, "本文"),
    NUMBER(4, "数値"),
    NUMBER_UPPER(5, "数値(上代)"),
    NUMBER_LOWER(6, "数値(下代)"),
    ATTACHMENT(7, "添付ファイル"),
    RECEIVED_DATE(8, "受信日"),
    CC(9, "CC"),
    BCC(10, "BCC"),
    AND_RECEIVER_CC_BCC(11, "全て(受信者・CC・BCC)"),
    OR_RECEIVER_CC_BCC(12, "いずれか(受信者・CC・BCC)"),
    AND_SUBJECT_BODY(13, "全て(件名・本文)"),
    OR_SUBJECT_BODY(14, "いずれか(件名・本文)"),
    MARK(15, "マーク"),
    PASS_RECEIVE_RULE (16, "受信ルール合致");

    private final int value;
    private final String text;

    MailItemOption(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static MailItemOption fromValue(int value) {
        switch(value) {
            case -1:
                return NONE;
            case 0:
                return SENDER;
            case 1:
                return RECEIVER;
            case 2:
                return SUBJECT;
            case 3:
                return BODY;
            case 4:
                return NUMBER;
            case 5:
                return NUMBER_UPPER;
            case 6:
                return NUMBER_LOWER;
            case 7:
                return ATTACHMENT;
            case 8:
                return RECEIVED_DATE;
            case 9:
                return CC;
            case 10:
                return BCC;
            case 11:
                return AND_RECEIVER_CC_BCC;
            case 12:
                return OR_RECEIVER_CC_BCC;
            case 13:
                return AND_SUBJECT_BODY;
            case 14:
                return OR_SUBJECT_BODY;
            case 15:
                return MARK;
            case 16:
                return PASS_RECEIVE_RULE;
        }
        return null;
    }
}
