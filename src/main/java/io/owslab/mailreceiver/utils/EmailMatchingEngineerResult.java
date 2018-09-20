package io.owslab.mailreceiver.utils;

import java.util.ArrayList;
import java.util.List;

import io.owslab.mailreceiver.dto.EmailDTO;
import io.owslab.mailreceiver.dto.EngineerMatchingDTO;
import io.owslab.mailreceiver.model.Email;

public class EmailMatchingEngineerResult {
	private EngineerMatchingDTO engineerMatchingDTO;
	private List<EmailDTO> listEmailDTO = new ArrayList<EmailDTO>();
	private List<String> listMatchingWord = new ArrayList<String>();
	
	public EngineerMatchingDTO getEngineerMatchingDTO() {
		return engineerMatchingDTO;
	}
	public void setEngineerMatchingDTO(EngineerMatchingDTO engineerMatchingDTO) {
		this.engineerMatchingDTO = engineerMatchingDTO;
	}
	public List<EmailDTO> getListEmailDTO() {
		return listEmailDTO;
	}
	public void setListEmailDTO(List<EmailDTO> listEmailDTO) {
		this.listEmailDTO = listEmailDTO;
	}
    public boolean addEmailDTO(Email email, FullNumberRange matchRange, FullNumberRange range){
        return this.listEmailDTO.add(new EmailDTO(email, matchRange, range));
    }
	public List<String> getListMatchingWord() {
		return listMatchingWord;
	}
	public void setListMatchingWord(List<String> listMatchingWord) {
		this.listMatchingWord = listMatchingWord;
	}
}
