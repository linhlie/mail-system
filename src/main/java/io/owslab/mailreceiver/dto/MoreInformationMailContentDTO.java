package io.owslab.mailreceiver.dto;

import java.util.ArrayList;
import java.util.List;

public class MoreInformationMailContentDTO {
	private String partnerInfor;
	private String engineerIntroduction;
	private List<String> domainPartnersOfEngineer = new ArrayList<String>();
	
	public String getPartnerInfor() {
		return partnerInfor;
	}
	public void setPartnerInfor(String partnerInfor) {
		this.partnerInfor = partnerInfor;
	}
	public String getEngineerIntroduction() {
		return engineerIntroduction;
	}
	public void setEngineerIntroduction(String engineerIntroduction) {
		this.engineerIntroduction = engineerIntroduction;
	}
	public void addDomainPartnersOfEngineer(String domain) {
		 this.domainPartnersOfEngineer.add(domain);
	}
	public List<String> getDomainPartnersOfEngineer() {
		return domainPartnersOfEngineer;
	}
	public void setDomainPartnersOfEngineer(List<String> domainPartnersOfEngineer) {
		this.domainPartnersOfEngineer = domainPartnersOfEngineer;
	}
}
