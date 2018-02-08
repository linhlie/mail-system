package io.owslab.mailreceiver.response;

/**
 * Created by khanhlvb on 2/8/18.
 */
public class AjaxResponseBody {
    String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AjaxResponseBody(String msg) {
        this.msg = msg;
    }

    public AjaxResponseBody() {
    }
}
