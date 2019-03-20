package io.owslab.mailreceiver.service.condition;

import io.owslab.mailreceiver.dao.ConditionNotificationDAO;
import io.owslab.mailreceiver.dto.ConditionNotificationDTO;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.ConditionNotification;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class ConditionNotificationService {
    public static final int sendAll=-100;
    private static final int PAGE_SIZE = 10;
    @Autowired
    AccountService accountService;

    @Autowired
    ConditionNotificationDAO conditionDAO;

    public void add(ConditionNotification conditionNotification) throws Exception {
        if(conditionNotification == null || conditionNotification.getCondition() == null){
            throw new Exception("Can't add condition notification");
        }
        long accoutId = accountService.getLoggedInAccountId();
        if(conditionNotification.getToAccountId()==sendAll){
            for (Account account : accountService.getAllUserRoleAccounts())
            {
                List<ConditionNotification>list= new ArrayList<>();
                if (account.getId()!=accountService.getLoggedInAccountId()){
                    list.add(new ConditionNotification(accoutId, account.getId(),conditionNotification.getCondition(),conditionNotification.getConditionType(),new Date(),
                            ConditionNotification.Status.NEW));
                }
                conditionDAO.save(list);
            }
        }

        else {
            conditionNotification.setFromAccountId(accountService.getLoggedInAccountId());
            conditionNotification.setSentAt(new Date());
            conditionNotification.setStatus(ConditionNotification.Status.NEW);
            conditionDAO.save(conditionNotification);
        }
    }

    public void update(ConditionNotificationDTO conditionNotificationDTO) throws Exception {
        if(conditionNotificationDTO == null){
            throw new Exception("Can't update condition notification");
        }
        ConditionNotification condition = conditionDAO.findOne(conditionNotificationDTO.getId());
        if(condition == null){
            return;
        }
        condition.setStatus(conditionNotificationDTO.getStatus());
        conditionDAO.save(condition);
    }

    public List<ConditionNotificationDTO> getConditionNotifications(long acccountId, int conditionType, int pageNumber){
        List<ConditionNotification> list = conditionDAO.findByToAccountIdAndConditionTypeLimit(acccountId, conditionType, PAGE_SIZE);
        return convertConditionNotificationList(list);
    }

    public long getNewConditionNotifications(long acccountId, int conditionType){
        return conditionDAO.countConditionNotificationByToAccountIdAndConditionTypeAndStatus(acccountId, conditionType, ConditionNotification.Status.NEW);
    }

    public List<ConditionNotificationDTO> showMoreConditionNotifications(ConditionNotificationDTO conditionNotificationDTO) throws Exception {
        if(conditionNotificationDTO == null){
            throw new Exception("Can't get more condition notifications");
        }
        long acccountId = accountService.getLoggedInAccountId();
        List<ConditionNotification> list = conditionDAO.getMoreConditionNotifications(acccountId, conditionNotificationDTO.getConditionType(), conditionNotificationDTO.getSentAt(), PAGE_SIZE);
        return convertConditionNotificationList(list);
    }

    private List<ConditionNotificationDTO> convertConditionNotificationList(List<ConditionNotification> list){
        List<ConditionNotificationDTO> result = new ArrayList<>();
        for(ConditionNotification conditionNotification : list){
            Account account = accountService.findById(conditionNotification.getFromAccountId());
            if (account != null){
                ConditionNotificationDTO notificationDTO = new ConditionNotificationDTO(conditionNotification, account.getAccountName());
                result.add(notificationDTO);
            }
        }
        return result;
    }
}
