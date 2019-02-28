package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailAddressGroupDAO;
import io.owslab.mailreceiver.model.EmailAddressGroup;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailAddressGroupService {

    @Autowired
    EmailAddressGroupDAO emailAddressGroupDAO;

    @Autowired
    AccountService accountService;

    public void createGroup(EmailAddressGroup emailGroup) throws Exception {
        long accountId = accountService.getLoggedInAccountId();
        if(emailGroup == null || emailGroup.getGroupName() == null || emailGroup.getGroupName().trim().equals("")){
            return;
        }
        List<EmailAddressGroup> listGroup = emailAddressGroupDAO.findByGroupNameAndAccountCreateId(emailGroup.getGroupName(), accountId);
        if(listGroup !=null && listGroup.size()>0){
            throw new Exception("[createGroup] This group is exist");
        }
        EmailAddressGroup emailAddressGroup = new EmailAddressGroup();
        emailAddressGroup.setGroupName(emailGroup.getGroupName());
        emailAddressGroup.setAccountCreateId(accountId);
        emailAddressGroupDAO.save(emailAddressGroup);
    }

    public void updateGroup(EmailAddressGroup emailAddressGroup) throws Exception {
        long accountId = accountService.getLoggedInAccountId();
        if(emailAddressGroup == null || emailAddressGroup.getGroupName() == null || emailAddressGroup.getGroupName().trim().equals("")){
            return;
        }
        EmailAddressGroup groupCheck = emailAddressGroupDAO.findOne(emailAddressGroup.getId());
        if(groupCheck ==null){
            throw new Exception("[updateGroup] This group isn't exist");
        }
        List<EmailAddressGroup> listGroup = emailAddressGroupDAO.findByGroupNameAndAccountCreateId(emailAddressGroup.getGroupName(), accountId);
        if(listGroup !=null && listGroup.size()>0){
            throw new Exception("[updateGroup] This group is exist");
        }
        emailAddressGroupDAO.save(emailAddressGroup);
    }

    public void delete(long id){
        emailAddressGroupDAO.delete(id);
    }

    public List<EmailAddressGroup> getList(){
        long accountId = accountService.getLoggedInAccountId();
        List<EmailAddressGroup> listGroup = emailAddressGroupDAO.findByAccountCreateIdOrderByGroupNameAsc(accountId);
        return listGroup;
    }
}
