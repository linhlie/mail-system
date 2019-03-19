package io.owslab.mailreceiver.service.condition;

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
    AccountService accountService;


    public List<MatchingConditionSaved> getListConditionSaved(){
        long accountId = accountService.getLoggedInAccountId();
        return conditionSavedDAO.findByAccountCreatedId(accountId);
    }

    public void addConditionSaved(MatchingConditionSaved form) throws Exception {
        if(form == null) {
            throw new Exception("[MatchingConditionSavedService] form doesn't null");
        }
        long accountId = accountService.getLoggedInAccountId();
        List<MatchingConditionSaved> list = conditionSavedDAO.findByAccountCreatedIdAndConditionNameAndConditionType(accountId, form.getConditionName(), form.getConditionType());
        if(list.size() > 0){
            throw new Exception("[MatchingConditionSavedService] condition name existed");
        }
        form.setAccountCreatedId(accountId);
        conditionSavedDAO.save(form);
    }

    public void delete(long id) {
        conditionSavedDAO.delete(id);
    }
}
