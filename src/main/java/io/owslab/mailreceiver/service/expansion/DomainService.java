package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.BusinessPartnerDAO;
import io.owslab.mailreceiver.dao.BusinessPartnerGroupDAO;
import io.owslab.mailreceiver.dao.DomainUnregisterDAO;
import io.owslab.mailreceiver.dao.RelationshipEngineerPartnerDAO;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.BusinessPartnerGroup;
import io.owslab.mailreceiver.model.DomainUnregister;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.RelationshipEngineerPartner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainService {

	private static final Logger logger = LoggerFactory.getLogger(DomainService.class);
	
    @Autowired
    private BusinessPartnerDAO partnerDAO;
    
    @Autowired
    private BusinessPartnerGroupDAO partnerGroupDAO;
    
    @Autowired
    private DomainUnregisterDAO domainDAO;
    
    @Autowired
    private BusinessPartnerService partnerService;
    
    @Autowired
    private EngineerService engineerService;
    
    @Autowired
    private RelationshipEngineerPartnerDAO relationshipEngineerPartnerDAO;
    
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
		List<String> mailAddressList = new ArrayList<>();
		for(Email mail : listEmail){
			mailAddressList.add(mail.getFrom());
		}
		saveDomainUnregisteredWithMailAddresses(mailAddressList);
	}

	public void saveDomainUnregisteredWithMailAddresses(List<String> listEmail){
		if(listEmail.size()==0){
			return;
		}
		LinkedHashMap<String, DomainUnregister> mapDomain = getDomainFromMailAddresses(listEmail);
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
		try {
			for(DomainUnregister domainUnregister : mapDomain.values()) {
				domainDAO.save(domainUnregister);
			}
		} catch (Exception e) {
			String error = ExceptionUtils.getStackTrace(e);
			logger.error("saveDomainUnregistered failed: " + error);
		}
	}

	public void startUpdateDomainUnregister(){
		List<DomainUnregister> listDomainUnregister = getAll();
		if(listDomainUnregister.size() == 0) return;
		List<String> mustDeletedDomains = new ArrayList<>();
		List<BusinessPartner> listPartner= (List<BusinessPartner>) partnerDAO.findAll();
		List<String> listDomainRegister = getDomainFromPartner(listPartner);
		for(DomainUnregister domain : listDomainUnregister){
			if(listDomainRegister.contains(domain.getDomain())){
				mustDeletedDomains.add(domain.getDomain());
			}
		}
		domainDAO.deleteByDomainIn(mustDeletedDomains);
	}
	
    public void deleteDomainByDomain(String domain1, String domain2, String domain3){
		List<String> mustDeletedDomains = new ArrayList<>();
    	if(domain1!=null && !domain1.equals("")){
			mustDeletedDomains.add(domain1.toLowerCase());
    	}
    	if(domain2!=null && !domain2.equals("")){
			mustDeletedDomains.add(domain2.toLowerCase());
    	}
    	if(domain3!=null && !domain3.equals("")){
			mustDeletedDomains.add(domain3.toLowerCase());
    	}
    	domainDAO.deleteByDomainIn(mustDeletedDomains);
    }

	public LinkedHashMap<String, DomainUnregister> getDomain(List<Email> listEmail){
		List<String> mailAddressList = new ArrayList<>();
    	for(Email mail : listEmail){
			mailAddressList.add(mail.getFrom());
		}
		return getDomainFromMailAddresses(mailAddressList);
	}

	public LinkedHashMap<String, DomainUnregister> getDomainFromMailAddresses(List<String> listEmail){
		LinkedHashMap<String, DomainUnregister> hashMap = new LinkedHashMap<String, DomainUnregister>();
		for(String from : listEmail){
			if(from!=null && !from.equals("")){
				DomainUnregister domain = new DomainUnregister();
				int index = from.indexOf("@");
				domain.setDomain(from.substring(index+1).toLowerCase());
				hashMap.put(domain.getDomain(), domain);
			}
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
	
	public boolean checkDomainPartnerCurrent(String emailFrom, Long partnerId){
		List<String> listDomain = partnerService.getDomainPartner(partnerId);
		if(listDomain==null || listDomain.size()==0) return false;
		
		int index = emailFrom.indexOf("@");
		if(index<=0) return false;
		
		String domainEmail =  emailFrom.substring(index+1).toLowerCase();
		for(int i=0;i<listDomain.size();i++){
			if(domainEmail.equals(listDomain.get(i))){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkDomainPartnerGroup(String emailFrom, Long partnerId){
		List<Long> listPartnerId = partnerService.getWithPartnerIds(partnerId);
		if(listPartnerId==null || listPartnerId.size()==0) return false;
		
		int index = emailFrom.indexOf("@");
		if(index<=0) return false;
		
		String domainEmail =  emailFrom.substring(index+1).toLowerCase();
		for(int i=0;i<listPartnerId.size();i++){
			String partnerGroupId = listPartnerId.get(i)+"";
			List<String> listDomain = partnerService.getDomainPartner(Long.parseLong(partnerGroupId));
			if(listDomain==null || listDomain.size()==0) continue;
			for(int j=0;j<listDomain.size();j++){
				if(domainEmail.equals(listDomain.get(j))){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkDomainPartnerNotGood(String emailFrom, Long engineerId){
		List<Long> listPartnerId = engineerService.getPartnerIds(engineerId);
		if(listPartnerId==null || listPartnerId.size()==0) return false;
		
		int index = emailFrom.indexOf("@");
		if(index<=0) return false;
		
		String domainEmail =  emailFrom.substring(index+1).toLowerCase();
		for(int i=0;i<listPartnerId.size();i++){
			String partnerId = listPartnerId.get(i)+"";
			List<String> listDomain = partnerService.getDomainPartner(Long.parseLong(partnerId));
			if(listDomain==null || listDomain.size()==0) continue;
			for(int j=0;j<listDomain.size();j++){
				if(domainEmail.equals(listDomain.get(j))){
					return true;
				}
			}
		}
		return false;
	}

}
