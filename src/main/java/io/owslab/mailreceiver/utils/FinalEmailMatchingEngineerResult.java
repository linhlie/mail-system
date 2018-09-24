package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.dto.PreviewMailDTO;

import java.util.List;
import java.util.Map;

public class FinalEmailMatchingEngineerResult {
	private List<EmailMatchingEngineerResult> listEngineerMatching;
    private Map<String, PreviewMailDTO> mailList;
    
    public FinalEmailMatchingEngineerResult(List<EmailMatchingEngineerResult> listEngineerMatching, Map<String, PreviewMailDTO> mailList) {
		this.listEngineerMatching = listEngineerMatching;
		this.mailList = mailList;
	}
    
	public List<EmailMatchingEngineerResult> getListEngineerMatching() {
		return listEngineerMatching;
	}


	public void setListEngineerMatching(
			List<EmailMatchingEngineerResult> listEngineerMatching) {
		this.listEngineerMatching = listEngineerMatching;
	}


	public Map<String, PreviewMailDTO> getMailList() {
		return mailList;
	}
	public void setMailList(Map<String, PreviewMailDTO> mailList) {
		this.mailList = mailList;
	}
}
