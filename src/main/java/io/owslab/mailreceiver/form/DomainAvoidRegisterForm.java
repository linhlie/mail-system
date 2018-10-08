package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.DomainUnregister;

import java.util.List;

public class DomainAvoidRegisterForm {
	private List<DomainUnregister> domainsUpdate;
	private List<DomainUnregister> domainsDelete;
	
	public List<DomainUnregister> getDomainsUpdate() {
		return domainsUpdate;
	}
	public void setDomainsUpdate(List<DomainUnregister> domainsUpdate) {
		this.domainsUpdate = domainsUpdate;
	}
	public List<DomainUnregister> getDomainsDelete() {
		return domainsDelete;
	}
	public void setDomainsDelete(List<DomainUnregister> domainsDelete) {
		this.domainsDelete = domainsDelete;
	}
}
