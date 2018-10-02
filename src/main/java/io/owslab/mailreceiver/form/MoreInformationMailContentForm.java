package io.owslab.mailreceiver.form;

public class MoreInformationMailContentForm {
	private String emailAddress;
	private Long engineerId;
	
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public Long getEngineerId() {
		return engineerId;
	}
	public void setEngineerId(Long engineerId) {
		this.engineerId = engineerId;
	}
}
