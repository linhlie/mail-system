package io.owslab.mailreceiver.exception;

/**
 * Created by khanhlvb on 4/23/18.
 */
public class ErrorCodeException extends Exception {
    private int errorCode;

    public ErrorCodeException(String message, int errorCode) {
        super(message);
        this.setErrorCode(errorCode);
    }

    public ErrorCodeException(int errorCode){
        super();
        this.setErrorCode(errorCode);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
