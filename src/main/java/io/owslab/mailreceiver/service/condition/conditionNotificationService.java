package io.owslab.mailreceiver.service.condition;

import io.owslab.mailreceiver.dao.ConditionNotificationDAO;
import io.owslab.mailreceiver.model.ConditionNotification;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class conditionNotificationService {

    @Autowired
    AccountService accountService;

    @Autowired
    ConditionNotificationDAO conditionDAO;

    public void save(ConditionNotification conditionNotification) throws Exception {
        if(conditionNotification == null || conditionNotification.getCondition() == null){
            throw new Exception("Can't save condition notification");
        }
        conditionNotification.setFromAccountId(accountService.getLoggedInAccountId());
        conditionNotification.setSentAt(new Date());
        conditionDAO.save(conditionNotification);
    }

//    public List<ConditionNotification> getConditionNotifications(long acccountId, int conditionType){
//    }
}
