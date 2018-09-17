package io.owslab.mailreceiver.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "relationship_engineer_partner")
public class RelationshipEngineerPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @ManyToOne
    @JoinColumn(name = "engineer_id", referencedColumnName = "id", nullable = false)
    private Engineer engineer;
    
    @ManyToOne
    @JoinColumn(name = "partner_id", referencedColumnName = "id", nullable = false)
    private BusinessPartner partner;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Engineer getEngineer() {
		return engineer;
	}

	public void setEngineer(Engineer engineer) {
		this.engineer = engineer;
	}

	public BusinessPartner getPartner() {
		return partner;
	}

	public void setPartner(BusinessPartner partner) {
		this.partner = partner;
	}
    
    
}
