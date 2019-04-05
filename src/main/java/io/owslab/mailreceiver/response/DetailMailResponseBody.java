package io.owslab.mailreceiver.response;

import io.owslab.mailreceiver.dto.DetailMailDTO;
import io.owslab.mailreceiver.model.Engineer;

/**
 * Created by khanhlvb on 6/26/18.
 */

public class DetailMailResponseBody extends AjaxResponseBody {
    private DetailMailDTO mail;
    private Engineer engineer;
    private long emailAccountId;

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

    public Engineer getEngineer() {
        return engineer;
    }

    public void setEngineer(Engineer engineer) {
        this.engineer = engineer;
    }

    public long getEmailAccountId() {
        return emailAccountId;
    }

    public void setEmailAccountId(long emailAccountId) {
        this.emailAccountId = emailAccountId;
    }
}
