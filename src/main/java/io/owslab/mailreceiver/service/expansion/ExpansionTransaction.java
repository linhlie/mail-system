package io.owslab.mailreceiver.service.expansion;

import java.text.ParseException;
import java.util.List;

import io.owslab.mailreceiver.dao.EngineerDAO;
import io.owslab.mailreceiver.dao.RelationshipEngineerPartnerDAO;
import io.owslab.mailreceiver.exception.EngineerNotFoundException;
import io.owslab.mailreceiver.exception.PartnerNotFoundException;
import io.owslab.mailreceiver.form.EngineerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Engineer;
import io.owslab.mailreceiver.model.RelationshipEngineerPartner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ExpansionTransaction {

	@Autowired
	EngineerDAO engineerDAO;
	
	@Autowired
	RelationshipEngineerPartnerDAO relationshipEngineerPartnerDAO;
	
    @Autowired
    private BusinessPartnerService partnerService;

    @Transactional(propagation = Propagation.REQUIRES_NEW,  rollbackFor = Exception.class)
    public void updateEngineerAndRelation(Engineer engineer, List<Long> addPartnerIds, List<Long> removePartnerIds)  throws EngineerNotFoundException, PartnerNotFoundException {
        updateEngineer(engineer);
        addPartners(engineer, addPartnerIds);
        removePartners(engineer.getId(), removePartnerIds);
    }
    
    @Transactional(propagation = Propagation.MANDATORY )
    public void updateEngineer(Engineer engineer) throws EngineerNotFoundException, PartnerNotFoundException{
    	Engineer existEngineer = engineerDAO.findOne(engineer.getId());
        if (existEngineer == null) throw new EngineerNotFoundException("技術者が存在しません");
        BusinessPartner existPartner = partnerService.findOne(engineer.getPartnerId());
        if (existPartner == null) throw new PartnerNotFoundException("取引先が存在しません。");
        engineerDAO.save(engineer);
    }
    
    @Transactional(propagation = Propagation.MANDATORY )
    public void addPartners(Engineer engineer, List<Long> addPartnerIds) throws PartnerNotFoundException{
    	for(int i=0;i<addPartnerIds.size();i++){
            BusinessPartner existPartner = partnerService.findOne(addPartnerIds.get(i));
            if (existPartner == null) throw new PartnerNotFoundException("取引先が存在しません。");
            RelationshipEngineerPartner relation = new RelationshipEngineerPartner();
            relation.setEngineer(engineer);
            relation.setPartner(existPartner);
            relationshipEngineerPartnerDAO.save(relation);
    	}
    }
    
    @Transactional(propagation = Propagation.MANDATORY )
    public void removePartners(Long  engineerId, List<Long> addPartnerIds){
    	for(int i=0;i<addPartnerIds.size();i++){
            relationshipEngineerPartnerDAO.deleteByEngineerIdAndPartnerId(engineerId, addPartnerIds.get(i));
    	}
    }
	
    @Transactional(propagation = Propagation.REQUIRES_NEW,  rollbackFor = Exception.class)
    public void addEngineerAndRelation(EngineerForm form)  throws PartnerNotFoundException, ParseException {
    	Engineer engineer  = 	addEngineer(form.getBuilder().build());
        addPartners(engineer, form.getGroupAddIds());
    }
    
    @Transactional(propagation = Propagation.MANDATORY )
    public Engineer addEngineer(Engineer engineer) throws PartnerNotFoundException{
        BusinessPartner existPartner = partnerService.findOne(engineer.getPartnerId());
        if (existPartner == null) throw new PartnerNotFoundException("取引先が存在しません。");
        return engineerDAO.save(engineer);
    }
}
