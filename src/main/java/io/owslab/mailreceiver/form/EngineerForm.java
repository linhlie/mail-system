package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.Engineer;

import java.util.List;

/**
 * Created by khanhlvb on 8/17/18.
 */
public class EngineerForm {
    private Engineer.Builder builder;
    private List<Long> groupAddIds;
    private List<Long> groupRemoveIds;
    
	public Engineer.Builder getBuilder() {
		return builder;
	}
	public void setBuilder(Engineer.Builder builder) {
		this.builder = builder;
	}
	public List<Long> getGroupAddIds() {
		return groupAddIds;
	}
	public void setGroupAddIds(List<Long> groupAddIds) {
		this.groupAddIds = groupAddIds;
	}
	public List<Long> getGroupRemoveIds() {
		return groupRemoveIds;
	}
	public void setGroupRemoveIds(List<Long> groupRemoveIds) {
		this.groupRemoveIds = groupRemoveIds;
	}
    
    
}
