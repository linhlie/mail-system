package io.owslab.mailreceiver.service.transaction;

import io.owslab.mailreceiver.dao.EngineerDAO;
import io.owslab.mailreceiver.dao.RelationshipEngineerPartnerDAO;
import io.owslab.mailreceiver.exception.EngineerException;
import io.owslab.mailreceiver.form.EngineerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Engineer;
import io.owslab.mailreceiver.model.RelationshipEngineerPartner;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Repository
@CacheConfig(cacheNames = "short_term_matching")
public class EngineerTransaction {

    @Autowired
    EngineerDAO engineerDAO;

    @Autowired
    RelationshipEngineerPartnerDAO relationshipEngineerPartnerDAO;

    @Autowired
    private BusinessPartnerService partnerService;

    @CacheEvict(key="\"EngineerService:getPartnerIds:\"+#id")
    @Transactional(propagation = Propagation.REQUIRES_NEW,  rollbackFor = Exception.class)
    public void updateEngineerAndRelationTransaction(EngineerForm form, long id) throws EngineerException, ParseException {
        Engineer engineer = form.getBuilder().build();
        engineer.setId(id);
        updateEngineer(engineer);
        addPartners(engineer, form.getGroupAddIds());
        removePartners(engineer.getId(), form.getGroupRemoveIds());
    }

    @Transactional(propagation = Propagation.MANDATORY )
    public void updateEngineer(Engineer engineer) throws EngineerException{
        Engineer existEngineer = engineerDAO.findOne(engineer.getId());
        if (existEngineer == null) throw new EngineerException("技術者が存在しません");
        BusinessPartner existPartner = partnerService.findOne(engineer.getPartnerId());
        if (existPartner == null) throw new EngineerException("取引先が存在しません。");
        engineerDAO.save(engineer);
    }

    @Transactional(propagation = Propagation.MANDATORY )
    public void addPartners(Engineer engineer, List<Long> addPartnerIds) throws EngineerException{
        List<RelationshipEngineerPartner> listRelation = new ArrayList<RelationshipEngineerPartner>();
        for(int i=0;i<addPartnerIds.size();i++){
            BusinessPartner existPartner = partnerService.findOne(addPartnerIds.get(i));
            RelationshipEngineerPartner relation = new RelationshipEngineerPartner();
            if (existPartner != null){
                relation.setEngineer(engineer);
                relation.setPartner(existPartner);
            }
            listRelation.add(relation);
        }
        relationshipEngineerPartnerDAO.save(listRelation);
    }

    @Transactional(propagation = Propagation.MANDATORY )
    public void removePartners(Long  engineerId, List<Long> addPartnerIds) throws  EngineerException{
        for(int i=0;i<addPartnerIds.size();i++){
            relationshipEngineerPartnerDAO.deleteByEngineerIdAndPartnerId(engineerId, addPartnerIds.get(i));
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,  rollbackFor = EngineerException.class)
    public void addEngineerAndRelationTransaction(EngineerForm form) throws EngineerException, ParseException {
        Engineer engineer = addEngineer(form.getBuilder().build());
        if(engineer == null) throw new EngineerException("save engineer fail");
        addPartners(engineer, form.getGroupAddIds());
    }

    @Transactional(propagation = Propagation.MANDATORY )
    public Engineer addEngineer(Engineer engineer) throws EngineerException{
        BusinessPartner existPartner = partnerService.findOne(engineer.getPartnerId());
        if (existPartner == null) throw new EngineerException("取引先が存在しません。");
        return engineerDAO.save(engineer);
    }
}
