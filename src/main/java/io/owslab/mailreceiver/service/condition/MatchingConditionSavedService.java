package io.owslab.mailreceiver.service.condition;

import io.owslab.mailreceiver.dao.MatchingConditionDAO;
import io.owslab.mailreceiver.dao.MatchingConditionSavedDAO;
import io.owslab.mailreceiver.model.MatchingConditionSaved;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingConditionSavedService {

    @Autowired
    MatchingConditionSavedDAO conditionSavedDAO;

    @Autowired
    MatchingConditionDAO matchingConditionDAO;

    @Autowired
    MatchingConditionSavedDAO matchingConditionSavedDAO;
    @Autowired
    AccountService accountService;


    public List<MatchingConditionSaved> getListConditionSaved(){
        long accountId = accountService.getLoggedInAccountId();
        return conditionSavedDAO.findByAccountCreatedId(accountId);
    }

    public void saveConditionSaved(MatchingConditionSaved form) throws Exception {
        if(form == null) {
            throw new Exception("[MatchingConditionSavedService] form doesn't null");
        }
        long accountId = accountService.getLoggedInAccountId();
        List<MatchingConditionSaved> list = conditionSavedDAO.findByAccountCreatedIdAndConditionNameAndConditionType(accountId, form.getConditionName(), form.getConditionType());
        if(list.size() > 0){
            form.setId(list.get(0).getId());
        }
        form.setAccountCreatedId(accountId);
        conditionSavedDAO.save(form);
    }

    public void delete(long id) {
        conditionSavedDAO.delete(id);
    }

    public List<MatchingConditionSaved> findByConditionTypeAndAccountCreatedId(int conditionType){
        long accountCreatedId = accountService.getLoggedInAccountId();
        return conditionSavedDAO.findByConditionTypeAndAccountCreatedId(conditionType, accountCreatedId);
    }
}
