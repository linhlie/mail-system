package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.BusinessPartnerGroup;

/**
 * Created by khanhlvb on 8/27/18.
 */
public class CSVPartnerGroupDTO {
    private String partnerName;
    private String partnerCode;
    private String withPartnerName;
    private String withPartnerCode;

    public CSVPartnerGroupDTO() {
    }

    public CSVPartnerGroupDTO(String partnerName, String partnerCode, String withPartnerName, String withPartnerCode) {
        this.partnerName = partnerName;
        this.partnerCode = partnerCode;
        this.withPartnerName = withPartnerName;
        this.withPartnerCode = withPartnerCode;
    }

    public CSVPartnerGroupDTO(BusinessPartnerGroup group) {
        BusinessPartner partner = group.getPartner();
        BusinessPartner withPartner = group.getWithPartner();
        this.setPartnerName(partner.getName());
        this.setPartnerCode(partner.getPartnerCode());
        this.setWithPartnerName(withPartner.getName());
        this.setWithPartnerCode(withPartner.getPartnerCode());
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getWithPartnerName() {
        return withPartnerName;
    }

    public void setWithPartnerName(String withPartnerName) {
        this.withPartnerName = withPartnerName;
    }

    public String getWithPartnerCode() {
        return withPartnerCode;
    }

    public void setWithPartnerCode(String withPartnerCode) {
        this.withPartnerCode = withPartnerCode;
    }
}
