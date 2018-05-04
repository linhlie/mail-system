package io.owslab.mailreceiver.response;

/**
 * Created by khanhlvb on 4/23/18.
 */
public class CodeResponseBody extends AjaxResponseBody {
    private int errorCode;

    public CodeResponseBody(String msg, boolean status, int errorCode) {
        super(msg, status);
        this.setErrorCode(errorCode);
    }

    public CodeResponseBody(String msg) {
        super(msg, false);
    }

    public CodeResponseBody() {
        this("");
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
