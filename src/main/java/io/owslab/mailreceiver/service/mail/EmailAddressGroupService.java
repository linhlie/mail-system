package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailAddressGroupDAO;
import io.owslab.mailreceiver.dao.EmailsAddressInGroupDAO;
import io.owslab.mailreceiver.dto.EmailsAddressInGroupDTO;
import io.owslab.mailreceiver.form.IdsForm;
import io.owslab.mailreceiver.model.EmailAddressGroup;
import io.owslab.mailreceiver.model.EmailsAddressInGroup;
import io.owslab.mailreceiver.model.PeopleInChargePartner;
import io.owslab.mailreceiver.service.expansion.PeopleInChargePartnerService;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EmailAddressGroupService {

    @Autowired
    EmailAddressGroupDAO emailAddressGroupDAO;

    @Autowired
    EmailsAddressInGroupDAO emailsAddressInGroupDAO;

    @Autowired
    PeopleInChargePartnerService peopleInChargePartnerService;

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

    public void deleteGroup(long id){
        emailAddressGroupDAO.delete(id);
    }

    public List<EmailAddressGroup> getGroupList(){
        long accountId = accountService.getLoggedInAccountId();
        List<EmailAddressGroup> listGroup = emailAddressGroupDAO.findByAccountCreateIdOrderByGroupNameAsc(accountId);
        return listGroup;
    }

    public List<EmailAddressGroup> searchGroup(String groupName){
        long accountId = accountService.getLoggedInAccountId();
        List<EmailAddressGroup> listGroup = emailAddressGroupDAO.findByAccountCreateIdAndGroupNameContainsOrderByGroupNameAsc(accountId, groupName);
        return listGroup;
    }

    public List<EmailsAddressInGroupDTO> getEmailList(long groupId){
        List<EmailsAddressInGroup> listEmail = emailsAddressInGroupDAO.findByGroupId(groupId);
        List<EmailsAddressInGroupDTO> listRelust = new ArrayList<>();
        for(EmailsAddressInGroup emailsAddressInGroup: listEmail){
            PeopleInChargePartner people = peopleInChargePartnerService.getById(emailsAddressInGroup.getPeopleInChargeId());
            if(people == null){
                continue;
            }
            EmailsAddressInGroupDTO email = new EmailsAddressInGroupDTO(emailsAddressInGroup, people);
            listRelust.add(email);
        }
        return listRelust;
    }

    public List<EmailsAddressInGroupDTO> searchEmailList(long groupId, String search){
        List<EmailsAddressInGroup> listEmail = emailsAddressInGroupDAO.findByGroupId(groupId);
        List<EmailsAddressInGroupDTO> listRelust = new ArrayList<>();
        for(EmailsAddressInGroup emailsAddressInGroup: listEmail){
            PeopleInChargePartner people = peopleInChargePartnerService.getById(emailsAddressInGroup.getPeopleInChargeId());
            if(people == null){
                continue;
            }
            EmailsAddressInGroupDTO email = new EmailsAddressInGroupDTO(emailsAddressInGroup, people);
            String name = people.getPepleName().toLowerCase();
            search = search.toLowerCase();
            if(name.contains(search) || people.getEmailAddress().contains(search)){
                listRelust.add(email);
            }
        }
        return listRelust;
    }

    public void addEmailAddressToList(EmailsAddressInGroup emailsAddressInGroup) throws Exception {
        EmailAddressGroup emailAddressGroup = emailAddressGroupDAO.findOne(emailsAddressInGroup.getGroupId());
        if(emailAddressGroup==null){
            throw new Exception("[addEmailAddressToList] This group isn't exist");
        }
        List<EmailsAddressInGroup> listGroup = emailsAddressInGroupDAO.findByGroupIdAndPeopleInChargeId(emailsAddressInGroup.getGroupId(), emailsAddressInGroup.getPeopleInChargeId());
        if(listGroup !=null && listGroup.size()>0){
            throw new Exception("[EmailsAddressInGroup] Email is exist in group");
        }
        emailsAddressInGroupDAO.save(emailsAddressInGroup);
    }

    public void deleteEmail(long id) {
        emailsAddressInGroupDAO.delete(id);
    }

    public List<String> getEmailReceivers(IdsForm form){
        Set<String> emails = new HashSet<>();
        List<EmailAddressGroup> addressGroupList = emailAddressGroupDAO.findByIdIn(form.getListId());
        for(EmailAddressGroup emailAddressGroup : addressGroupList){
            List<EmailsAddressInGroup> listEmail = emailsAddressInGroupDAO.findByGroupId(emailAddressGroup.getId());
            for(EmailsAddressInGroup email : listEmail){
                PeopleInChargePartner people = peopleInChargePartnerService.getById(email.getPeopleInChargeId());
                if(people != null){
                    emails.add(people.getEmailAddress());
                }
            }
        }
        List<String> result = new ArrayList<>();
        result.addAll(emails);
        return result;
    }
}
