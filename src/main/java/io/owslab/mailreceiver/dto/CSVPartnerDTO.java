package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.enums.CompanyType;
import io.owslab.mailreceiver.enums.StockShareType;
import io.owslab.mailreceiver.model.BusinessPartner;

/**
 * Created by khanhlvb on 8/27/18.
 */
public class CSVPartnerDTO {
    private String name;
    private String kanaName;
    private String companyType;
    private String stockShare;
    private String partnerCode;
    private String domain1;
    private String domain2;
    private String domain3;
    private String ourCompany;

    public CSVPartnerDTO(BusinessPartner partner) {
        this.setName(partner.getName());
        this.setKanaName(partner.getKanaName());
        CompanyType companyType = CompanyType.fromValue(partner.getCompanyType());
        String companyTypeStr = companyType.equals(CompanyType.OTHER) ? partner.getCompanySpecificType() : companyType.getText();
        this.setCompanyType(companyTypeStr);
        StockShareType stockShare = StockShareType.fromValue(partner.getStockShare());
        this.setStockShare(stockShare.getText());
        this.setPartnerCode(partner.getPartnerCode());
        this.setDomain1(partner.getDomain1());
        this.setDomain2(partner.getDomain2());
        this.setDomain3(partner.getDomain3());
        this.setOurCompany(Boolean.toString(partner.isOurCompany()).toUpperCase());
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

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getStockShare() {
        return stockShare;
    }

    public void setStockShare(String stockShare) {
        this.stockShare = stockShare;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getDomain1() {
        return domain1;
    }

    public void setDomain1(String domain1) {
        this.domain1 = domain1;
    }

    public String getDomain2() {
        return domain2;
    }

    public void setDomain2(String domain2) {
        this.domain2 = domain2;
    }

    public String getDomain3() {
        return domain3;
    }

    public void setDomain3(String domain3) {
        this.domain3 = domain3;
    }

    public String getOurCompany() {
        return ourCompany;
    }

    public void setOurCompany(String ourCompany) {
        this.ourCompany = ourCompany;
    }
}
