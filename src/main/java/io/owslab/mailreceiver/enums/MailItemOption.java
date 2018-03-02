package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 3/2/18.
 */
public enum MailItemOption {
    SENDER(0, "送信者"),
    RECEIVER(1, "受信者"),
    SUBJECT(2, "件名"),
    BODY(3, "本文"),
    NUMBER(4, "数値"),
    NUMBER_UPPER(5, "数値(上代)"),
    NUMBER_LOWER(6, "数値(下代)"),
    HAS_ATTACHMENT(7, "添付ファイル有り"),
    NO_ATTACHMENT(8, "添付ファイル無し"),
    RECEIVED_DATE(9, "受信日");

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
}
