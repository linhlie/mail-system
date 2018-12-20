package io.owslab.mailreceiver.dto;

import java.util.List;

public class InboxDTO {
    private List<EmailInboxDTO> listEmail;
    private int totalEmail;
    private int start;
    private int end;
    private int totalPages;

    public InboxDTO(){

    }

    public InboxDTO(List<EmailInboxDTO> listEmail, int totalEmail, int start, int end, int totalPages){
        this.listEmail = listEmail;
        this.totalEmail = totalEmail;
        this.start = start;
        this.end = end;
        this.totalPages = totalPages;
    }

    public List<EmailInboxDTO> getListEmail() {
        return listEmail;
    }

    public void setListEmail(List<EmailInboxDTO> listEmail) {
        this.listEmail = listEmail;
    }

    public int getTotalEmail() {
        return totalEmail;
    }

    public void setTotalEmail(int totalEmail) {
        this.totalEmail = totalEmail;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
