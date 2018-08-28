package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

/**
 * Created by khanhlvb on 8/13/18.
 */
@Entity
@Table(name = "Business_Partners")
public class BusinessPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String partnerCode;

    @NotNull
    private String name;

    private String kanaName;

    private int companyType;

    private String companySpecificType;

    private int stockShare;

    @NotNull
    private String domain1;

    private String domain2;

    private String domain3;

    private boolean ourCompany;

    public BusinessPartner() {
    }

    public BusinessPartner(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public String getName() {
        return name;
    }

    public String getKanaName() {
        return kanaName;
    }

    public int getCompanyType() {
        return companyType;
    }

    public String getCompanySpecificType() {
        return companySpecificType;
    }

    public int getStockShare() {
        return stockShare;
    }

    public String getDomain1() {
        return domain1;
    }

    public String getDomain2() {
        return domain2;
    }

    public String getDomain3() {
        return domain3;
    }

    public boolean isOurCompany() {
        return ourCompany;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKanaName(String kanaName) {
        this.kanaName = kanaName;
    }

    public void setCompanyType(int companyType) {
        this.companyType = companyType;
    }

    public void setCompanySpecificType(String companySpecificType) {
        this.companySpecificType = companySpecificType;
    }

    public void setStockShare(int stockShare) {
        this.stockShare = stockShare;
    }

    public void setDomain1(String domain1) {
        this.domain1 = domain1;
    }

    public void setDomain2(String domain2) {
        this.domain2 = domain2;
    }

    public void setDomain3(String domain3) {
        this.domain3 = domain3;
    }

    public void setOurCompany(boolean ourCompany) {
        this.ourCompany = ourCompany;
    }

    //Builder Class
    public static class Builder{
        private long id;
        private String partnerCode;
        private String name;
        private String kanaName;
        private int companyType;
        private String companySpecificType;
        private int stockShare;
        private String domain1;
        private String domain2;
        private String domain3;
        private boolean ourCompany;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setPartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setKanaName(String kanaName) {
            this.kanaName = kanaName;
            return this;
        }

        public Builder setCompanyType(int companyType) {
            this.companyType = companyType;
            return this;
        }

        public Builder setCompanySpecificType(String companySpecificType) {
            this.companySpecificType = companySpecificType;
            return this;
        }

        public Builder setStockShare(int stockShare) {
            this.stockShare = stockShare;
            return this;
        }

        public Builder setDomain1(String domain1) {
            this.domain1 = domain1;
            return this;
        }

        public Builder setDomain2(String domain2) {
            this.domain2 = domain2;
            return this;
        }

        public Builder setDomain3(String domain3) {
            this.domain3 = domain3;
            return this;
        }

        public Builder setOurCompany(boolean ourCompany) {
            this.ourCompany = ourCompany;
            return this;
        }

        public BusinessPartner build(){
            BusinessPartner partner = new BusinessPartner();
            partner.id = this.id;
            partner.partnerCode = this.partnerCode;
            partner.name = this.name;
            partner.kanaName = this.kanaName;
            partner.companyType = this.companyType;
            if(this.companyType == CompanyTypes.OTHER) {
                partner.companySpecificType = this.companySpecificType;
            } else {
                partner.companySpecificType = null;
            }
            partner.stockShare = this.stockShare;
            partner.domain1 = this.domain1;
            partner.domain2 = this.domain2;
            partner.domain3 = this.domain3;
            partner.ourCompany = this.ourCompany;
            return partner;
        }

        public long getId() {
            return id;
        }

        public String getPartnerCode() {
            return partnerCode;
        }

        public String getName() {
            return name;
        }

        public String getKanaName() {
            return kanaName;
        }

        public int getCompanyType() {
            return companyType;
        }

        public String getCompanySpecificType() {
            return companySpecificType;
        }

        public int getStockShare() {
            return stockShare;
        }

        public String getDomain1() {
            return domain1;
        }

        public String getDomain2() {
            return domain2;
        }

        public String getDomain3() {
            return domain3;
        }

        public boolean isOurCompany() {
            return ourCompany;
        }

        public static class CompanyTypes {
            public static final int LTD = 1;
            public static final int LIMITED = 2;
            public static final int GROUP = 3;
            public static final int JOINT_STOCK = 4;
            public static final int FOUNDATION = 5;
            public static final int CORPORATION = 6;
            public static final int OTHER = 7;
        }
    }

    public static class PartnerComparator implements Comparator<BusinessPartner> {
        public int compare(BusinessPartner o1, BusinessPartner o2) {
            int value1 = o1.getKanaName().compareTo(o2.getKanaName());
            if (value1 == 0) {
                return o1.getName().compareTo(o1.getName());
            }
            return value1;
        }
    }
}
