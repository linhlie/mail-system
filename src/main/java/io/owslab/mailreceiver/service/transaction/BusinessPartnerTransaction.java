package io.owslab.mailreceiver.service.transaction;

import io.owslab.mailreceiver.exception.BusinessPartnerException;
import io.owslab.mailreceiver.form.PartnerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.BusinessPartnerGroup;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import io.owslab.mailreceiver.service.expansion.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BusinessPartnerTransaction {

    @Autowired
    BusinessPartnerService partnerService;

    @Autowired
    DomainService domainService;

    @Transactional(propagation = Propagation.REQUIRES_NEW,  rollbackFor = BusinessPartnerException.class)
    public void addPartnerTransaction(PartnerForm form)throws BusinessPartnerException{
        BusinessPartner.Builder builder = form.getBuilder();
        String partnerCode = builder.getPartnerCode();
        BusinessPartner existPartner = partnerService.findOneByPartnerCode(partnerCode);
        if(existPartner != null) throw new BusinessPartnerException("識別IDは既に存在します。");
        BusinessPartner addedPartner = savePartner(builder.build());
        List<Long> groupAddIds = form.getGroupAddIds();
        saveGroupListPartner(addedPartner, groupAddIds);
    }

    @Transactional(propagation = Propagation.MANDATORY )
    public BusinessPartner savePartner(BusinessPartner partner) throws BusinessPartnerException{
        BusinessPartner newPartner = partnerService.savePartner(partner);
        if(newPartner == null) throw new BusinessPartnerException("Save partner fail");
        domainService.deleteDomainByDomain(newPartner.getDomain1(), newPartner.getDomain2(), newPartner.getDomain3());
        return newPartner;
    }

    @Cacheable(key="\"BusinessPartnerService:getWithPartnerIds:\"+#partner.getId()")
    @Transactional(propagation = Propagation.MANDATORY )
    public void saveGroupListPartner(BusinessPartner partner, List<Long> groupWithPartnerIds) throws BusinessPartnerException{
        List<BusinessPartnerGroup> partnerGroups = new ArrayList<>();
        for(Long groupWithPartnerId : groupWithPartnerIds) {
            BusinessPartner groupWithPartner = partnerService.findOne(groupWithPartnerId);
            if(groupWithPartner != null) {
                partnerGroups.add(new BusinessPartnerGroup(partner, groupWithPartner));
                partnerGroups.add(new BusinessPartnerGroup(groupWithPartner, partner));
            }
        }
        partnerService.savePartnerGroup(partnerGroups);
    }

    @CacheEvict(key="\"BusinessPartnerService:getDomainPartner:\"+#id")
    @Transactional(propagation = Propagation.REQUIRES_NEW,  rollbackFor = BusinessPartnerException.class)
    public void updatePartnerTransaction(PartnerForm form, long id)throws BusinessPartnerException{
        BusinessPartner.Builder builder = form.getBuilder();
        builder.setId(id);
        String partnerCode = builder.getPartnerCode();
        BusinessPartner partner = partnerService.findOne(id);
        if(partner == null) throw new BusinessPartnerException("取引先は存在しません");
        BusinessPartner existPartner = partnerService.findOneByPartnerCode(partnerCode);
        if(existPartner != null) {
            long existPartnerId = existPartner.getId();
            if(existPartnerId != id) throw new BusinessPartnerException("識別IDは既に存在します。");
        }
        BusinessPartner updatedPartner = savePartner(builder.build());
        List<Long> groupRemoveIds = form.getGroupRemoveIds();
        List<Long> groupAddIds = form.getGroupAddIds();
        partnerService.deleteGroupList(updatedPartner, groupRemoveIds);
        saveGroupListPartner(updatedPartner, groupAddIds);
    }
}
