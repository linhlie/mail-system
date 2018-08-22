package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.BusinessPartner;

/**
 * Created by khanhlvb on 8/22/18.
 */
public class PartnerDTO {
    private long id;
    private String name;
    private String kanaName;
    private boolean ourCompany;

    public PartnerDTO(BusinessPartner partner) {
        this.setId(partner.getId());
        this.setName(partner.getName());
        this.setKanaName(partner.getKanaName());
        this.setOurCompany(partner.isOurCompany());
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKanaName() {
        return kanaName;
    }

    public void setKanaName(String kanaName) {
        this.kanaName = kanaName;
    }

    public boolean isOurCompany() {
        return ourCompany;
    }

    public void setOurCompany(boolean ourCompany) {
        this.ourCompany = ourCompany;
    }
}

