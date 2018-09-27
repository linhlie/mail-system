package io.owslab.mailreceiver.enums;

/**
 * Created by khanhlvb on 9/25/18.
 */
public enum AuthenticationProtocol {
    PLAIN(0, "通常のパスワード認証"),
    ENCRYPTED(1, "暗号化されたパスワード認証"),
    GSSAPI(2, "Kerberos/GSSAPI"),
    NTLM(3, "NTLM"),
    TLS(4, "TLS証明書"),
    OAUTH2(5, "OAuth2");

    private final int value;
    private final String text;

    AuthenticationProtocol(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static AuthenticationProtocol fromValue(int value) {
        switch(value) {
            case 0:
                return PLAIN;
            case 1:
                return ENCRYPTED;
            case 2:
                return GSSAPI;
            case 3:
                return NTLM;
            case 4:
                return TLS;
            case 5:
                return OAUTH2;
        }
        return PLAIN;
    }
}
