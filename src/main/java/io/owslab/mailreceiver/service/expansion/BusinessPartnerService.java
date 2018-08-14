package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.BusinessPartnerDAO;
import io.owslab.mailreceiver.dao.BusinessPartnerGroupDAO;
import io.owslab.mailreceiver.exception.PartnerCodeException;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.BusinessPartnerGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 8/13/18.
 */
@Service
public class BusinessPartnerService {
    @Autowired
    private BusinessPartnerDAO partnerDAO;

    @Autowired
    private BusinessPartnerGroupDAO partnerGroupDAO;

    public List<BusinessPartner> getAll() {
        return (List<BusinessPartner>) partnerDAO.findAll();
    }

    public void add(BusinessPartner.Builder builder) throws PartnerCodeException {
        String partnerCode = builder.getPartnerCode();
        BusinessPartner existPartner = findOneByPartnerCode(partnerCode);
        if(existPartner != null) throw new PartnerCodeException("識別IDは既に存在します。");
        partnerDAO.save(builder.build());
    }

    public void update(BusinessPartner.Builder builder, long id) throws PartnerCodeException {
        builder.setId(id);
        String partnerCode = builder.getPartnerCode();
        BusinessPartner partner = findOne(id);
        if(partner == null) throw new PartnerCodeException("取引先は存在しません");
        BusinessPartner existPartner = findOneByPartnerCode(partnerCode);
        if(existPartner != null) {
            long existPartnerId = existPartner.getId();
            if(existPartnerId != id) throw new PartnerCodeException("識別IDは既に存在します。");
        }
        partnerDAO.save(builder.build());
    }

    private BusinessPartner findOneByPartnerCode(String partnerCode){
        List<BusinessPartner> partners = partnerDAO.findByPartnerCode(partnerCode);
        return partners.size() > 0 ? partners.get(0) : null;
    }

    private BusinessPartner findOne(long id){
        return partnerDAO.findOne(id);
    }

    public void delete(long id){
        partnerDAO.delete(id);
    }

    public List<BusinessPartnerGroup> findByPartner(long partnerId){
        return partnerGroupDAO.findByPartnerId(partnerId);
    }
}
