package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.PeopleInChargePartner;
import io.owslab.mailreceiver.utils.FullNumberRange;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by khanhlvb on 4/17/18.
 */
public class ExtractMailDTO extends PreviewMailDTO {
    private String messageId;
    private String range;

    public ExtractMailDTO(Email email, List<BusinessPartner> listPartner, LinkedHashMap<String, PeopleInChargePartner> lisPeople) {
        super(email, listPartner, lisPeople);
        this.setMessageId(email.getMessageId());
        List<FullNumberRange> rangeList = email.getRangeList();
        if(rangeList.size() > 0)
        this.setRange(rangeList.get(0).toString());
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }
}
