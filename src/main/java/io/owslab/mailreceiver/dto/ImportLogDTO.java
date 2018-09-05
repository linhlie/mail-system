package io.owslab.mailreceiver.dto;

/**
 * Created by khanhlvb on 9/4/18.
 */
public class ImportLogDTO {
    private String type;
    private int line;
    private String info;
    private String detail;

    public ImportLogDTO(String type, int line, String info, String detail) {
        this.type = type;
        this.line = line;
        this.info = info;
        this.detail = detail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
