package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.BusinessPartnerDAO;
import io.owslab.mailreceiver.exception.DuplicatePartnerCodeException;
import io.owslab.mailreceiver.model.BusinessPartner;
import org.omg.CORBA.PUBLIC_MEMBER;
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

    public List<BusinessPartner> getAll() {
        return (List<BusinessPartner>) partnerDAO.findAll();
    }

    public void add(BusinessPartner.Builder builder) throws DuplicatePartnerCodeException {
        String partnerCode = builder.getPartnerCode();
        BusinessPartner existPartner = findOneByPartnerCode(partnerCode);
        if(existPartner != null) throw new DuplicatePartnerCodeException("識別IDは既に存在します。");
        partnerDAO.save(builder.build());
    }

    public void update(BusinessPartner.Builder builder) {
        String partnerCode = builder.getPartnerCode();

    }

    private BusinessPartner findOneByPartnerCode(String partnerCode){
        List<BusinessPartner> partners = partnerDAO.findByPartnerCode(partnerCode);
        return partners.size() > 0 ? partners.get(0) : null;
    }

    public void delete(long id){
        partnerDAO.delete(id);
    }
}
