package io.owslab.mailreceiver.response;

import java.util.List;

/**
 * Created by khanhlvb on 2/8/18.
 */
public class AjaxResponseBody {
    private String msg;

    private boolean status;

    private List list;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public AjaxResponseBody(String msg, boolean status) {
        this.msg = msg;
        this.status = status;
    }

    public AjaxResponseBody(String msg) {
        this(msg, false);
    }

    public AjaxResponseBody() {
        this("");
    }
}
