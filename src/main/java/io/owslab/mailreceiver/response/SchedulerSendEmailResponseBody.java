package io.owslab.mailreceiver.response;

import io.owslab.mailreceiver.dto.FileDTO;

import java.util.List;

public class SchedulerSendEmailResponseBody extends AjaxResponseBody {
    private List<FileDTO> listFile;

    public SchedulerSendEmailResponseBody(String msg, boolean status) {
        super(msg, status);
    }

    public SchedulerSendEmailResponseBody(String msg) {
        this(msg, false);
    }

    public SchedulerSendEmailResponseBody() {
        this("");
    }

    public List<FileDTO> getListFile() {
        return listFile;
    }

    public void setListFile(List<FileDTO> listFile) {
        this.listFile = listFile;
    }
}
