package io.owslab.mailreceiver.response;

import io.owslab.mailreceiver.dto.DetailMailDTO;

/**
 * Created by khanhlvb on 6/26/18.
 */

public class DetailMailResponseBody extends AjaxResponseBody {
    private DetailMailDTO mail;

    public DetailMailResponseBody(String msg, boolean status, int errorCode) {
        super(msg, status);
    }

    public DetailMailResponseBody(String msg) {
        super(msg, false);
    }

    public DetailMailResponseBody() {
        this("");
    }

    public DetailMailDTO getMail() {
        return mail;
    }

    public void setMail(DetailMailDTO mail) {
        this.mail = mail;
    }
}
