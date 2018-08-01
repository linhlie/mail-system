package io.owslab.mailreceiver.service.errror;

/**
 * Created by khanhlvb on 8/1/18.
 */
public class ReportErrorService {

    private String currentError;


    public ReportErrorService() {
    }

    public String getCurrentError() {
        return currentError;
    }

    public void clear() {
        currentError = null;
    }

    public boolean hasSystemError() {
        return currentError != null;
    }

    public void setCurrentError(String error) {
        currentError = error;
    }

    public void sendReportError(String error) {
        System.out.println("sendReportError: " +  error);
        //TODO: sendReportError
    }
}
