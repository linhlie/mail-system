package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.*;
import io.owslab.mailreceiver.dto.EmailsAddressInGroupDTO;
import io.owslab.mailreceiver.form.EmailsAddressInGroupForm;
import io.owslab.mailreceiver.form.IdsForm;
import io.owslab.mailreceiver.form.SchedulerSendEmailForm;
import io.owslab.mailreceiver.form.SendMailForm;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.service.expansion.PeopleInChargePartnerService;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.utils.ConvertDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeBodyPart;
import java.util.*;

@Service
public class EmailAddressGroupService {

    @Autowired
    EmailAddressGroupDAO emailAddressGroupDAO;

    @Autowired
    EmailsAddressInGroupDAO emailsAddressInGroupDAO;

    @Autowired
    SchedulerSendEmailDAO schedulerSendEmailDAO;

    @Autowired
    PeopleInChargePartnerService peopleInChargePartnerService;

    @Autowired
    AccountService accountService;

    @Autowired
    MailAccountsService mailAccountsService;

    @Autowired
    SchedulerSendEmailFileDAO schedulerSendEmailFileDAO;

    @Autowired
    FileDAO fileDAO;

    @Autowired
    UploadFileDAO uploadFileDAO;

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

    public void addListEmailAddress(EmailsAddressInGroupForm form) throws Exception {
        if(form==null || form.getListPeopleId() == null){
            throw new Exception("[addListEmailAddress] form null");
        }
        for(long id : form.getListPeopleId()){
            addEmailAddressToList(new EmailsAddressInGroup(form.getGroupId(), id));
        }

    }

    public void addEmailAddressToList(EmailsAddressInGroup emailsAddressInGroup) throws Exception {
        EmailAddressGroup emailAddressGroup = emailAddressGroupDAO.findOne(emailsAddressInGroup.getGroupId());
        if(emailAddressGroup==null){
            throw new Exception("[addEmailAddressToList] This group isn't exist");
        }
        List<EmailsAddressInGroup> listGroup = emailsAddressInGroupDAO.findByGroupIdAndPeopleInChargeId(emailsAddressInGroup.getGroupId(), emailsAddressInGroup.getPeopleInChargeId());
        if(listGroup !=null && listGroup.size()>0){
            return;
        }
        emailsAddressInGroupDAO.save(emailsAddressInGroup);
    }

    public void deleteEmail(long id) {
        emailsAddressInGroupDAO.delete(id);
    }

    public List<String> getEmailReceivers(IdsForm form){
        if(form == null){
            return new ArrayList<>();
        }
        Set<String> emails = new HashSet<>();
        List<EmailAddressGroup> addressGroupList = emailAddressGroupDAO.findByIdIn(form.getListEmailGroupId());
        for(EmailAddressGroup emailAddressGroup : addressGroupList){
            List<EmailsAddressInGroup> listEmail = emailsAddressInGroupDAO.findByGroupId(emailAddressGroup.getId());
            for(EmailsAddressInGroup email : listEmail){
                PeopleInChargePartner people = peopleInChargePartnerService.getById(email.getPeopleInChargeId());
                if(people != null){
                    emails.add(people.getEmailAddress());
                }
            }
        }
        for(String s : form.getListEmailAddress()){
            emails.add(s);
        }

        List<String> result = new ArrayList<>();
        result.addAll(emails);
        return result;
    }

    public void createScheduler(SchedulerSendEmailForm scheduler) throws Exception {
        if(scheduler==null || scheduler.getSendMailForm()==null){
            return;
        }
        long accountId = accountService.getLoggedInAccountId();
        String sendEmailId = scheduler.getSendMailForm().getAccountId();
        if(sendEmailId == null) return;
        long sendEmailAccountId = Long.parseLong(sendEmailId);
        EmailAccount emailAccount = mailAccountsService.getEmailAccountById(sendEmailAccountId);
        SchedulerSendEmail schedulerSendEmail = new SchedulerSendEmail(scheduler, emailAccount, accountId);
        if(schedulerSendEmail.getTypeSendEmail() == SchedulerSendEmail.Type.SEND_BY_HOUR){
            Date date = ConvertDate.convertDateScheduler(schedulerSendEmail.getDateSendEmail() + " " + schedulerSendEmail.getHourSendEmail());
            Date now = new Date();
            if(ConvertDate.compareMinuteOfDate(now, date)>=-1){
                throw new Exception("scheduler date must after now");
            }
        }
        schedulerSendEmail = schedulerSendEmailDAO.save(schedulerSendEmail);

        SendMailForm form = scheduler.getSendMailForm();
        List<Long> originAttachment = form.getOriginAttachment();
        List<Long> uploadAttachment = form.getUploadAttachment();
        List<SchedulerSendEmailFile> listSchedulerFile = new ArrayList<>();
        if(originAttachment != null && originAttachment.size() > 0) {
            List<AttachmentFile> files = fileDAO.findByIdInAndDeleted(originAttachment, false);
            for (AttachmentFile attachmentFile : files){
                listSchedulerFile.add(new SchedulerSendEmailFile(schedulerSendEmail.getId(), attachmentFile.getId()));
            }
        }
        if(uploadAttachment != null && uploadAttachment.size() > 0) {
            List<UploadFile> uploadFiles = uploadFileDAO.findByIdIn(uploadAttachment);
            for (UploadFile uploadFile : uploadFiles){
                listSchedulerFile.add(new SchedulerSendEmailFile(schedulerSendEmail.getId(), uploadFile.getId()));
            }
        }
        schedulerSendEmailFileDAO.save(listSchedulerFile);
    }

    public List<SchedulerSendEmail> getAllScheduler(){
        long accountId = accountService.getLoggedInAccountId();
        return schedulerSendEmailDAO.findByAccountIdOrderBySentAt(accountId);
    }

    public List<SchedulerSendEmail> getScheduler(long id){
        return schedulerSendEmailDAO.findById(id);
    }

    public void deleteSchedulerSendEmail(long id) {
        schedulerSendEmailDAO.delete(id);
    }

    public void changeStatusScheduler(SchedulerSendEmail scheduler) {
        schedulerSendEmailDAO.save(scheduler);
    }

    public List<SchedulerSendEmail> getSchedulerByStatus(){
        return schedulerSendEmailDAO.findByStatus(SchedulerSendEmail.Status.ACTIVE);
    }

    public List<Long> getListFileUpload(long scheduleId){
        List<SchedulerSendEmailFile>  list = schedulerSendEmailFileDAO.findBySchedulerSendEmailId(scheduleId);
        List<Long> result = new ArrayList<>();
        for(SchedulerSendEmailFile file : list){
            result.add(file.getUploadFilesId());
        }
        return  result;
    }
}