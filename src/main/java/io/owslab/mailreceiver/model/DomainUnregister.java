package io.owslab.mailreceiver.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "domain_unregister")
public class DomainUnregister {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @Column(name = "domain", length = 255, nullable = false)
    private String domain;
    
    @Column(name = "status", nullable = false)
    private int status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
    public static class Status {
        public static final int ALLOW_REGISTER = 1;
        public static final int AVOID_REGISTER = 2;
    }
	
}
