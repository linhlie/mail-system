package io.owslab.mailreceiver.form;

import java.util.List;

import io.owslab.mailreceiver.dto.EngineerMatchingDTO;
import io.owslab.mailreceiver.model.Engineer;
import io.owslab.mailreceiver.utils.FilterRule;

public class EmailMatchingEngineerForm {
    private boolean distinguish;
    private boolean spaceEffective;
    private FilterRule destinationConditionData;
    private boolean handleDuplicateSender;
    private boolean handleDuplicateSubject;
    private boolean handleSameDomain;
    private List<EngineerMatchingDTO> listEngineerMatchingDTO;

    public EmailMatchingEngineerForm() {
    }

	public boolean isDistinguish() {
		return distinguish;
	}

	public void setDistinguish(boolean distinguish) {
		this.distinguish = distinguish;
	}

	public boolean isSpaceEffective() {
		return spaceEffective;
	}

	public void setSpaceEffective(boolean spaceEffective) {
		this.spaceEffective = spaceEffective;
	}

	public FilterRule getDestinationConditionData() {
		return destinationConditionData;
	}

	public void setDestinationConditionData(FilterRule destinationConditionData) {
		this.destinationConditionData = destinationConditionData;
	}

	public boolean isHandleDuplicateSender() {
		return handleDuplicateSender;
	}

	public void setHandleDuplicateSender(boolean handleDuplicateSender) {
		this.handleDuplicateSender = handleDuplicateSender;
	}

	public boolean isHandleDuplicateSubject() {
		return handleDuplicateSubject;
	}

	public void setHandleDuplicateSubject(boolean handleDuplicateSubject) {
		this.handleDuplicateSubject = handleDuplicateSubject;
	}

	public boolean isHandleSameDomain() {
		return handleSameDomain;
	}

	public void setHandleSameDomain(boolean handleSameDomain) {
		this.handleSameDomain = handleSameDomain;
	}

	public List<EngineerMatchingDTO> getListEngineerMatchingDTO() {
		return listEngineerMatchingDTO;
	}

	public void setListEngineerMatchingDTO(
			List<EngineerMatchingDTO> listEngineerMatchingDTO) {
		this.listEngineerMatchingDTO = listEngineerMatchingDTO;
	}

}
