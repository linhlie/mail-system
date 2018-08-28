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

    public CSVPartnerDTO(){
    }

    public CSVPartnerDTO(String name, String kanaName, String companyType, String stockShare, String partnerCode, String domain1, String domain2, String domain3, String ourCompany) {
        super();
        this.name = name;
        this.kanaName = kanaName;
        this.companyType = companyType;
        this.stockShare = stockShare;
        this.partnerCode = partnerCode;
        this.domain1 = domain1;
        this.domain2 = domain2;
        this.domain3 = domain3;
        this.ourCompany = ourCompany;
    }

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

    @Override
    public String toString() {
        return "Partner [name=" + name + ", kanaName="
                + kanaName + ", companyType=" + companyType + ", stockShare="
                + stockShare + ", partnerCode=" + partnerCode + ", domain1="
                + domain1 + ", domain2=" + domain2 + ", domain3=" + domain3 +  ", ourCompany=" + ourCompany + "]";
    }

    public BusinessPartner build(){
        BusinessPartner partner = new BusinessPartner();
        partner.setPartnerCode(this.partnerCode);
        partner.setName(this.name);
        partner.setKanaName(this.kanaName);
        CompanyType companyType = CompanyType.fromText(this.companyType);
        partner.setCompanyType(companyType.getValue());
        String companySpecificType = companyType.equals(CompanyType.OTHER) ? this.companyType : null;
        partner.setCompanySpecificType(companySpecificType);
        StockShareType stockShare = StockShareType.fromText(this.stockShare);
        partner.setStockShare(stockShare.getValue());
        partner.setDomain1(this.domain1);
        partner.setDomain2(this.domain2);
        partner.setDomain3(this.domain3);
        partner.setOurCompany(this.ourCompany.equalsIgnoreCase("TRUE"));
        return partner;
    }
}
