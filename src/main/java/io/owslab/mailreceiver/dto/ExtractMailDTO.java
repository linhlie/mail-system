package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.Email;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * Created by khanhlvb on 4/17/18.
 */
public class ExtractMailDTO extends PreviewMailDTO {
    private String messageId;

    public ExtractMailDTO(Email email) {
        super(email);
        this.setMessageId(email.getMessageId());
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
