package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.*;
import io.owslab.mailreceiver.dto.EmailsAddressInGroupDTO;
import io.owslab.mailreceiver.dto.FileDTO;
import io.owslab.mailreceiver.enums.ClickType;
import io.owslab.mailreceiver.form.EmailsAddressInGroupForm;
import io.owslab.mailreceiver.form.IdsForm;
import io.owslab.mailreceiver.form.SchedulerSendEmailForm;
import io.owslab.mailreceiver.form.SendMailForm;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.service.expansion.PeopleInChargePartnerService;
import io.owslab.mailreceiver.service.greeting.GreetingService;
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

    @Autowired
    SendMailService sendMailService;

    @Autowired
    GreetingService greetingService;

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
        long userId = accountService.getLoggedInAccountId();
        String sendEmailId = scheduler.getSendMailForm().getAccountId();
        if(sendEmailId == null) return;
        if(scheduler.getTypeSendEmail() == SchedulerSendEmail.Type.NOW){
            preSendEmail(scheduler.getSendMailForm(), userId, true);
            if(scheduler.getId()>0){
                schedulerSendEmailDAO.delete(scheduler.getId());
            }
            return;
        }
        long sendEmailAccountId = Long.parseLong(sendEmailId);
        EmailAccount emailAccount = mailAccountsService.getEmailAccountById(sendEmailAccountId);
        SchedulerSendEmail schedulerSendEmail = new SchedulerSendEmail(scheduler, emailAccount, userId);
        if(schedulerSendEmail.getTypeSendEmail() == SchedulerSendEmail.Type.SEND_BY_HOUR){
            Date date = ConvertDate.convertDateScheduler(schedulerSendEmail.getDateSendEmail() + " " + schedulerSendEmail.getHourSendEmail());
            Date now = new Date();
            if(ConvertDate.compareMinuteOfDate(now, date)>=-1){
                throw new Exception("scheduler date must after now");
            }
        }
        schedulerSendEmail = schedulerSendEmailDAO.save(schedulerSendEmail);

        SendMailForm form = scheduler.getSendMailForm();
        List<Long> uploadAttachment = form.getUploadAttachment();
        List<SchedulerSendEmailFile> listSchedulerFile = new ArrayList<>();
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

    public List<FileDTO> getListFileUploadDTO(long scheduleId){
        List<Long>  listId  = getListFileUpload(scheduleId);
        List<UploadFile> listFile = uploadFileDAO.findByIdIn(listId);
        List<FileDTO> result = new ArrayList<>();
        for(UploadFile file : listFile){
            result.add(new FileDTO(file));
        }
        return  result;
    }

    public void preSendEmail(SendMailForm form, long userId,  boolean canDelete) throws Exception {
        String receivers = form.getReceiver();
        String[] receiverArr = receivers.split(",");
        for(int i=0;i<receiverArr.length;i++){
            if(receiverArr[i]==null || receiverArr[i].equals("")){
                continue;
            }
            String contentReplace = greetingService.greetingDecode(form.getContent(), receiverArr[i], userId, -1);
            SendMailForm formForOne = new SendMailForm(form, receiverArr[i], contentReplace);
            sendMailService.sendMailScheduler(formForOne, canDelete, userId);
        }
    }
}