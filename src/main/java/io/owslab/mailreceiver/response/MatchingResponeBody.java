package io.owslab.mailreceiver.response;

import io.owslab.mailreceiver.dto.PreviewMailDTO;
import org.aspectj.weaver.loadtime.Aj;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by khanhlvb on 4/13/18.
 */
public class MatchingResponeBody extends AjaxResponseBody {
    private Map<String, PreviewMailDTO> mailList;

    public MatchingResponeBody(String msg, boolean status) {
        super(msg, status);
    }

    public MatchingResponeBody(String msg) {
        this(msg, false);
    }

    public MatchingResponeBody() {
        this("");
    }

    public Map<String, PreviewMailDTO> getMailList() {
        return mailList;
    }

    public void setMailList(Map<String, PreviewMailDTO> mailList) {
        this.mailList = mailList;
    }
}
