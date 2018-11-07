package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.PeopleInChargePartnerDAO;
import io.owslab.mailreceiver.dto.PartnerForPeopleInChargeDTO;
import io.owslab.mailreceiver.dto.PeopleInChargePartnerDTO;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.PeopleInChargePartner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "short_term_matching")
public class PeopleInChargePartnerService {

    @Autowired
    PeopleInChargePartnerDAO peopleInChargePartnerDAO;

    @Autowired
    BusinessPartnerService partnerService;

    public PeopleInChargePartner getById(long id){
        return peopleInChargePartnerDAO.findOne(id);
    }

    public void deletePeople(long id) {
        peopleInChargePartnerDAO.delete(id);
    }

    public List<PeopleInChargePartnerDTO> getByPartnerId(long partnerId){
        List<PeopleInChargePartner> listPeople =  peopleInChargePartnerDAO.findByPartnerId(partnerId);
        List<PeopleInChargePartnerDTO> listPeopleDTO =  new ArrayList<>();
        for(PeopleInChargePartner people : listPeople){
            PeopleInChargePartnerDTO peopleDTO = new PeopleInChargePartnerDTO(people);
            listPeopleDTO.add(peopleDTO);
        }
        return listPeopleDTO;
    }

    //need transaction
    public void addPeopleInChargePartner(PeopleInChargePartner people) throws Exception {
        BusinessPartner partner = partnerService.findOne(people.getPartnerId());
        if(partner==null){
            throw new Exception("Partner doesn't esxit");
        }
        PeopleInChargePartner peopleInCharge = peopleInChargePartnerDAO.findByEmailAddress(people.getEmailAddress());
        if(peopleInCharge != null){
            throw new Exception("Email esxited");
        }
        if(people.isEmailInChargePartner()){
            PeopleInChargePartner peopleInChargePartner = peopleInChargePartnerDAO.findByEmailInChargePartner(true);
            if(peopleInChargePartner == null){
                peopleInChargePartnerDAO.save(people);
            }else{
                peopleInChargePartner.setEmailInChargePartner(false);
                peopleInChargePartnerDAO.save(peopleInChargePartner);
                peopleInChargePartnerDAO.save(people);
            }
        }else{
            peopleInChargePartnerDAO.save(people);
        }
    }

    //need transaction
    public void editPeopleInChargePartner(PeopleInChargePartner people) throws Exception {
        PeopleInChargePartner peopleInCharge = peopleInChargePartnerDAO.findByEmailAddress(people.getEmailAddress());
        if(peopleInCharge != null && peopleInCharge.getId() != people.getId()){
            throw new Exception("Email esxited");
        }
        if(people.isEmailInChargePartner()){
            PeopleInChargePartner peopleInChargePartner = peopleInChargePartnerDAO.findByEmailInChargePartner(true);
            if(peopleInChargePartner != null && peopleInChargePartner.getId() != people.getId()){
                peopleInChargePartner.setEmailInChargePartner(false);
                peopleInChargePartnerDAO.save(peopleInChargePartner);
                peopleInChargePartnerDAO.save(people);
            }else{
                peopleInChargePartnerDAO.save(people);
            }
        }else{
            peopleInChargePartnerDAO.save(people);
        }
    }

}
