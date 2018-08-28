package io.owslab.mailreceiver.model;

import javax.persistence.*;

/**
 * Created by khanhlvb on 8/14/18.
 */
@Entity
@Table(name = "Business_Partner_Groups")
public class BusinessPartnerGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "partner_id", referencedColumnName = "id", nullable = false)
    private BusinessPartner partner;

    @ManyToOne
    @JoinColumn(name = "with_partner_id", referencedColumnName = "id", nullable = false)
    private BusinessPartner withPartner;

    public BusinessPartnerGroup() {
    }

    public BusinessPartnerGroup(long id) {
        this.id = id;
    }

    public BusinessPartnerGroup(BusinessPartner partner, BusinessPartner withPartner) {
        this.partner = partner;
        this.withPartner = withPartner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BusinessPartner getPartner() {
        return partner;
    }

    public void setPartner(BusinessPartner partner) {
        this.partner = partner;
    }

    public BusinessPartner getWithPartner() {
        return withPartner;
    }

    public void setWithPartner(BusinessPartner withPartner) {
        this.withPartner = withPartner;
    }
}
