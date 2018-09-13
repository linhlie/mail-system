package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.BusinessPartnerDAO;
import io.owslab.mailreceiver.dao.DomainUnregisterDAO;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.DomainUnregister;
import io.owslab.mailreceiver.model.Email;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainService {
	
    @Autowired
    private BusinessPartnerDAO partnerDAO;
    
    @Autowired
    private DomainUnregisterDAO domainDAO;
    
	public List<DomainUnregister> getAll() {
		return domainDAO.findAll();
	}
	
	public void delete(long id) {
		domainDAO.delete(id);
	}
	
    public void deleteDomainByListDomain(List<String> listDomain){
    	domainDAO.deleteByDomainIn(listDomain);
    }
	
	public void deleteByDomain(String domain) {
		domainDAO.deleteByDomain(domain);
	}
	
	public void saveDomainUnregistered(List<Email> listEmail){
		if(listEmail.size()==0){
			return;
		}
		LinkedHashMap<String, DomainUnregister> mapDomain = getDomain(listEmail);
		List<DomainUnregister> listDomainUnregister = domainDAO.findAll();
		
		for(DomainUnregister domain : listDomainUnregister){
			if(mapDomain.containsKey(domain.getDomain())){
				mapDomain.remove(domain.getDomain());
			}
		}
		
		List<BusinessPartner> listPartner= (List<BusinessPartner>) partnerDAO.findAll();
		for(BusinessPartner partner : listPartner){
			String domain1 = partner.getDomain1();
			String domain2 = partner.getDomain2();
			String domain3 = partner.getDomain3();
			
			if(domain1 != null && !domain1.equals("") && mapDomain.containsKey(domain1.toLowerCase())){
				mapDomain.remove(domain1);
			}
			if(domain2 != null && !domain2.equals("") && mapDomain.containsKey(domain2.toLowerCase())){
				mapDomain.remove(domain2);
			}
			if(domain3 != null && !domain3.equals("") && mapDomain.containsKey(domain3.toLowerCase())){
				mapDomain.remove(domain3);
			}
		}
		domainDAO.save(mapDomain.values());
	}
	
	public void startUpdateDomainUnregister(){
		List<DomainUnregister> listDomainUnregister = getAll();
		List<BusinessPartner> listPartner= (List<BusinessPartner>) partnerDAO.findAll();
		LinkedHashMap<String, DomainUnregister> mapDomainRegister = getDomainFromPartner(listPartner);
		for(DomainUnregister domain : listDomainUnregister){
			if(mapDomainRegister.containsKey(domain.getDomain())){
				domainDAO.deleteByDomain(domain.getDomain());
			}
		}
	}
	
    public void deleteDomainByDomain(String domain1, String domain2, String domain3){
    	if(domain1!=null && !domain1.equals("")){
    		deleteByDomain(domain1.toLowerCase());
    	}
    	if(domain2!=null && !domain2.equals("")){
    		deleteByDomain(domain2.toLowerCase());
    	}
    	if(domain3!=null && !domain3.equals("")){
    		deleteByDomain(domain3.toLowerCase());
    	}
    }
	
	public LinkedHashMap<String, DomainUnregister> getDomain(List<Email> listEmail){
		LinkedHashMap<String, DomainUnregister> hashMap = new LinkedHashMap<String, DomainUnregister>();
		for(Email mail : listEmail){
			String from = mail.getFrom();
			if(from!=null && !from.equals("")){
				DomainUnregister domain = new DomainUnregister();
				int index = from.indexOf("@");
				domain.setDomain(from.substring(index+1).toLowerCase());
				hashMap.put(domain.getDomain(), domain);
			}
		}
		return hashMap;
	}
	
	public LinkedHashMap<String, DomainUnregister> getDomainFromPartner(List<BusinessPartner> listPartner){
		LinkedHashMap<String, DomainUnregister> hashMap = new LinkedHashMap<String, DomainUnregister>();
		for(BusinessPartner partner : listPartner){
			String domain1 =partner.getDomain1();
			String domain2 =partner.getDomain2();
			String domain3 =partner.getDomain3();
			if(domain1!=null && !domain1.equals("")){
				DomainUnregister domain = new DomainUnregister();
				domain.setDomain(domain1.toLowerCase());
				hashMap.put(domain.getDomain(), domain);
	    	}
	    	if(domain2!=null && !domain2.equals("")){
	    		DomainUnregister domain = new DomainUnregister();
				domain.setDomain(domain2.toLowerCase());
				hashMap.put(domain.getDomain(), domain);
	    	}
	    	if(domain3!=null && !domain3.equals("")){
	    		DomainUnregister domain = new DomainUnregister();
				domain.setDomain(domain3.toLowerCase());
				hashMap.put(domain.getDomain(), domain);
	    	}
		}
		return hashMap;
	}

}
