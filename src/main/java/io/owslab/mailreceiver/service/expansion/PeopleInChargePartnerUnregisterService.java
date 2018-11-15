package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.PeopleInChargePartnerUnregisterDAO;
import io.owslab.mailreceiver.form.EmailsAvoidRegisterPeopleInChargeForm;
import io.owslab.mailreceiver.model.DomainUnregister;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.PeopleInChargePartnerUnregister;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PeopleInChargePartnerUnregisterService {
    private static final Logger logger = LoggerFactory.getLogger(PeopleInChargePartnerUnregisterService.class);

    @Autowired
    private PeopleInChargePartnerUnregisterDAO peopleInChargeUnregisterDAO;

    @Autowired
    private DomainService domainService;

    public List<PeopleInChargePartnerUnregister> getAll() {
        return peopleInChargeUnregisterDAO.findAll();
    }

    public List<PeopleInChargePartnerUnregister> getPeopleInChargeUnregisterByStatus(int status) {
        List<PeopleInChargePartnerUnregister> listPeople = peopleInChargeUnregisterDAO.findByStatus(status);
        Collections.sort(listPeople, new Comparator<PeopleInChargePartnerUnregister>() {
            @Override
            public int compare(PeopleInChargePartnerUnregister o1, PeopleInChargePartnerUnregister o2) {
                if(o1.getEmail() != null && o2.getEmail() != null){
                    int index1 = o1.getEmail().indexOf("@");
                    String domain1 = o1.getEmail().substring(index1+1);
                    int index2 = o2.getEmail().indexOf("@");
                    String domain2 = o2.getEmail().substring(index2+1);
                    return domain1.compareTo(domain2);
                }
                return 0;
            }
        });
        return listPeople;
    }

    public PeopleInChargePartnerUnregister getPeoleInChargeUnregisterByIdAndStatus(Long id, int status) {
        List<PeopleInChargePartnerUnregister>  peoleInChargeUnregister = peopleInChargeUnregisterDAO.findByIdAndStatus(id, status);
        return peoleInChargeUnregister != null && peoleInChargeUnregister.size() > 0 ? peoleInChargeUnregister.get(0) : null;
    }

    public void savePeopleInChargeUnregister(PeopleInChargePartnerUnregister people) {
        peopleInChargeUnregisterDAO.save(people);
    }

    public void saveListPeopleInChargeUnregister(List<PeopleInChargePartnerUnregister> listPeople) {
        peopleInChargeUnregisterDAO.save(listPeople);
    }

    public void delete(long id) {
        peopleInChargeUnregisterDAO.delete(id);
    }

    public void deleteByListPeopleInChargeUnregister(List<PeopleInChargePartnerUnregister> listPeople){
        peopleInChargeUnregisterDAO.delete(listPeople);
    }

    public void deleteByEmail(String email) {
        peopleInChargeUnregisterDAO.deleteByEmail(email);
    }

    public void changeFromAllowToAvoid(long id) {
        PeopleInChargePartnerUnregister people = getPeoleInChargeUnregisterByIdAndStatus(id, PeopleInChargePartnerUnregister.Status.ALLOW_REGISTER);
        if(people != null){
            people.setStatus(PeopleInChargePartnerUnregister.Status.AVOID_REGISTER);
            savePeopleInChargeUnregister(people);
        }
    }

    public void savePeopleInChargeUnregistered(List<Email> listEmail){
        if(listEmail.size()==0){
            return;
        }
        List<String> mailAddressList = new ArrayList<>();
        for(Email mail : listEmail){
            mailAddressList.add(mail.getFrom().toLowerCase());
        }
        if(mailAddressList.size()>0){
            savePeopleInChargeUnregisteredWithMailAddresses(mailAddressList);
        }
    }

    public void savePeopleInChargeUnregisteredWithMailAddresses(List<String> listEmail){
        if(listEmail.size()==0){
            return;
        }
        LinkedHashMap<String, PeopleInChargePartnerUnregister> mapPeople = getPeopleInChargeFromMailAddresses(listEmail);
        List<PeopleInChargePartnerUnregister> listPeopleInChargeUnregister = getAll();
        List<String> domainDeletes = new ArrayList<>();
        for(PeopleInChargePartnerUnregister people : listPeopleInChargeUnregister){
            String email = people.getEmail();
            if(mapPeople.containsKey(email)){
                mapPeople.remove(email);
            }
            int index = email.indexOf("@");
            String domain = email.substring(index+1);
            String predomain = email.substring(0,index);
            if(predomain.equalsIgnoreCase("*")){
                domainDeletes.add(domain);
            }
        }
        List<DomainUnregister> lisDomainUnregister = domainService.getDomainsByStatus(DomainUnregister.Status.AVOID_REGISTER);
        List<PeopleInChargePartnerUnregister> listPeopleInChargeRemove = new ArrayList<>();
        for(PeopleInChargePartnerUnregister peopleInChargeUnregister : mapPeople.values()) {
            String email = peopleInChargeUnregister.getEmail();
            int index = email.indexOf("@");
            String domain = email.substring(index+1);
            boolean removeFlag = false;
            for(String domaindelete : domainDeletes){
                if(domain.equals(domaindelete)){
                    listPeopleInChargeRemove.add(peopleInChargeUnregister);
                    removeFlag = true;
                    break;
                }
            }
            if(!removeFlag){
                for(DomainUnregister domainUnregister : lisDomainUnregister){
                    if(domain.equals(domainUnregister.getDomain())){
                        listPeopleInChargeRemove.add(peopleInChargeUnregister);
                        break;
                    }
                }
            }
        }
        for(PeopleInChargePartnerUnregister people : listPeopleInChargeRemove){
            String email = people.getEmail();
            if(mapPeople.containsKey(email)){
                mapPeople.remove(email);
            }
        }
        try {
            for(PeopleInChargePartnerUnregister peopleInChargeUnregister : mapPeople.values()) {
                savePeopleInChargeUnregister(peopleInChargeUnregister);
            }
        } catch (Exception e) {
            String error = ExceptionUtils.getStackTrace(e);
            logger.error("savePeopleInChargeUnregister failed: " + error);
        }
    }

    public LinkedHashMap<String, PeopleInChargePartnerUnregister> getPeopleInChargeFromMailAddresses(List<String> listEmail){
        LinkedHashMap<String, PeopleInChargePartnerUnregister> hashMap = new LinkedHashMap<String, PeopleInChargePartnerUnregister>();
        for(String from : listEmail){
            if(from!=null && !from.equals("")){
                PeopleInChargePartnerUnregister peole = new PeopleInChargePartnerUnregister();
                peole.setEmail(from.toLowerCase());
                peole.setStatus(PeopleInChargePartnerUnregister.Status.ALLOW_REGISTER);
                hashMap.put(peole.getEmail(), peole);
            }
        }
        return hashMap;
    }

    public void saveEmailsAvoidRegisterPeopleInCharge(EmailsAvoidRegisterPeopleInChargeForm form){
        if(form != null){
            List<PeopleInChargePartnerUnregister> listEmailUpdate = form.getEmailsUpdate();
            List<String> listEmailSpecial = new ArrayList<>();
            if(listEmailUpdate != null && listEmailUpdate.size()>0){
                for(int i=listEmailUpdate.size()-1;i>=0;i--){
                    if(checkEmailSpecial(listEmailUpdate.get(i))){
                        listEmailSpecial.add(listEmailUpdate.get(i).getEmail().substring(2));
                    }
                }
                saveListPeopleInChargeUnregister(listEmailUpdate);
            }
            List<PeopleInChargePartnerUnregister> listEmailDelete = form.getEmailsDelete();
            if(listEmailDelete != null && listEmailDelete.size()>0){
                deleteByListPeopleInChargeUnregister(listEmailDelete);
            }
            List<PeopleInChargePartnerUnregister> listPeopleDelete = new ArrayList<>();
            if(listEmailSpecial.size()>0){
                List<PeopleInChargePartnerUnregister> listEmail = getAll();
                for(PeopleInChargePartnerUnregister people : listEmail){
                    int index = people.getEmail().indexOf("@");
                    String domain = people.getEmail().substring(index+1);
                    for(String domainDelete : listEmailSpecial){
                        if (domain.equalsIgnoreCase(domainDelete) && !people.getEmail().equalsIgnoreCase("*@"+domain)){
                            listPeopleDelete.add(people);
                        }
                    }
                }
            }
            if(listPeopleDelete != null && listPeopleDelete.size()>0){
                deleteByListPeopleInChargeUnregister(listPeopleDelete);
            }
        }
    }

    public boolean checkEmailSpecial(PeopleInChargePartnerUnregister peopleInChargePartnerUnregister){
        String email = peopleInChargePartnerUnregister.getEmail();
        if(email == null || email.trim().equals("")){
            return false;
        }
        int index = email.indexOf("@");
        String domain = email.substring(index+1);
        domain = "*@"+domain;
        if(email.equalsIgnoreCase(domain)){
            return true;
        }
        return false;
    }
}
