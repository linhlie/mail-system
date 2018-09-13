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
	
	public void deleteByDomain(String domain) {
		domainDAO.deleteByDomain(domain);
	}
	
	public void saveDomainUnregistered(List<Email> listEmail){
		LinkedHashMap<String, DomainUnregister> mapDomain = getDomain(listEmail);
		List<DomainUnregister> listDomainUnregister = domainDAO.findAll();
		
		for(DomainUnregister domain : listDomainUnregister){
			if(mapDomain.containsKey(domain.getDomain())){
				mapDomain.remove(domain.getDomain());
			}
		}
		
		List<BusinessPartner> listPartner= (List<BusinessPartner>) partnerDAO.findAll();
		for(BusinessPartner partner : listPartner){
			if(mapDomain.containsKey(partner.getDomain1())){
				mapDomain.remove(partner.getDomain1());
			}
			if(mapDomain.containsKey(partner.getDomain2())){
				mapDomain.remove(partner.getDomain2());
			}
			if(mapDomain.containsKey(partner.getDomain3())){
				mapDomain.remove(partner.getDomain3());
			}
		}

		domainDAO.save(mapDomain.values());
	}

	public void startUpdateDomainUnregister(){
		List<DomainUnregister> listDomainUnregister = getAll();
		if(listDomainUnregister.size() == 0) return;
		List<BusinessPartner> listPartner= (List<BusinessPartner>) partnerDAO.findAll();
		List<String> listDomainRegister = getDomainFromPartner(listPartner);
		for(DomainUnregister domain : listDomainUnregister){
			if(listDomainRegister.contains(domain.getDomain())){
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
			DomainUnregister domain = new DomainUnregister();
			int index = mail.getFrom().indexOf("@");
			domain.setDomain(mail.getFrom().substring(index+1).toLowerCase());
			hashMap.put(domain.getDomain(), domain);
		}
		return hashMap;
	}
	
	public List<String> getDomainFromPartner(List<BusinessPartner> listPartner){
		List<String> domainList = new ArrayList<String>();
		for(BusinessPartner partner : listPartner){
			String domain1 =partner.getDomain1();
			String domain2 =partner.getDomain2();
			String domain3 =partner.getDomain3();
			if(domain1!=null && !domain1.equals("")){
				domainList.add(domain1.toLowerCase());
	    	}
	    	if(domain2!=null && !domain2.equals("")){
				domainList.add(domain2.toLowerCase());
	    	}
	    	if(domain3!=null && !domain3.equals("")){
				domainList.add(domain3.toLowerCase());
	    	}
		}
		return domainList;
	}

}
