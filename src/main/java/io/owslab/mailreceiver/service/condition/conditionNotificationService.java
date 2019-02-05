package io.owslab.mailreceiver.service.condition;

import io.owslab.mailreceiver.dao.ConditionNotificationDAO;
import io.owslab.mailreceiver.model.ConditionNotification;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class conditionNotificationService {
    private static final int PAGE_SIZE = 10;
    @Autowired
    AccountService accountService;

    @Autowired
    ConditionNotificationDAO conditionDAO;

    public void add(ConditionNotification conditionNotification) throws Exception {
        if(conditionNotification == null || conditionNotification.getCondition() == null){
            throw new Exception("Can't add condition notification");
        }
        conditionNotification.setFromAccountId(accountService.getLoggedInAccountId());
        conditionNotification.setSentAt(new Date());
        conditionDAO.save(conditionNotification);
    }

    public void update(ConditionNotification conditionNotification) throws Exception {
        if(conditionNotification == null){
            throw new Exception("Can't update condition notification");
        }
        ConditionNotification condition = conditionDAO.findOne(conditionNotification.getId());
        if(condition == null){
            return;
        }else{
            condition.setStatus(conditionNotification.getStatus());
            conditionDAO.save(condition);
        }
    }

    public List<ConditionNotification> getConditionNotifications(long acccountId, int conditionType, int pageNumber){
        int listSize = pageNumber * PAGE_SIZE;
        List<ConditionNotification> list = conditionDAO.findByToAccountIdAndConditionTypeLimit(acccountId, conditionType, listSize);
        return list;
    }
}
