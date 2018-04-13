package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.utils.FullNumberRange;
import org.apache.commons.lang.time.DateFormatUtils;
import org.threeten.bp.DateTimeUtils;

import java.util.Date;

/**
 * Created by khanhlvb on 3/13/18.
 */
public class EmailDTO {
    private String messageId;

    private String matchRange;

    private String range;

    public EmailDTO(Email email) {
        this.setMessageId(email.getMessageId());
    }

    public EmailDTO(Email email, FullNumberRange matchRange, FullNumberRange range) {
        this(email);
        this.setMatchRange(matchRange);
        this.setRange(range);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMatchRange() {
        return matchRange;
    }

    public void setMatchRange(String matchRange) {
        this.matchRange = matchRange;
    }

    public void setMatchRange(FullNumberRange matchRange){
        this.matchRange = matchRange != null ? matchRange.toString() : null;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public void setRange(FullNumberRange range){
        this.range = range != null ? range.toString() : null;
    }
}
