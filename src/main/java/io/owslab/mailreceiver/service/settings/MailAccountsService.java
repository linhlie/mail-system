package io.owslab.mailreceiver.service.settings;

import io.owslab.mailreceiver.dao.EmailAccountDAO;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dto.EmailAccountEngineerDTO;
import io.owslab.mailreceiver.dto.EmailAccountToSendMailDTO;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import io.owslab.mailreceiver.service.expansion.EngineerService;
import io.owslab.mailreceiver.service.greeting.GreetingService;
import io.owslab.mailreceiver.service.mail.EmailAccountSettingService;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 1/24/18.
 */
@Service
@CacheConfig(cacheNames = "short_term_data")
public class MailAccountsService {

    @Autowired
    private EmailAccountDAO emailAccountDAO;

    @Autowired
    private EmailDAO emailDAO;

    @Autowired
    private EngineerService engineerService;

    @Autowired
    private BusinessPartnerService partnerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailAccountSettingService emailAccountSettingService;

    @Autowired
    private GreetingService greetingService;

    public EmailAccount getEmailAccountById(long id){
        return emailAccountDAO.findOne(id);
    }

    public Page<EmailAccount> list(PageRequest pageRequest) {
        Page<EmailAccount> list = emailAccountDAO.findAll(pageRequest);
        return list;
    }

    public List<EmailAccount> list() {
        return (List<EmailAccount>) emailAccountDAO.findAll();
    }

    public List<EmailAccountToSendMailDTO> getListEmailAccountToSendMail() {
        List<EmailAccount> emailAccounts =  list();
        List<EmailAccountToSendMailDTO> emailAccountDTOs =  new ArrayList<>();
        for(EmailAccount account : emailAccounts){
            EmailAccountSetting accountSetting = emailAccountSettingService.findOneSend(account.getId());
            if(accountSetting!=null){
                EmailAccountToSendMailDTO acc = new EmailAccountToSendMailDTO(account, accountSetting.getCc());
                emailAccountDTOs.add(acc);
            }
        }
        return emailAccountDTOs;
    }

    public List<EmailAccountToSendMailDTO> getListEmailAccount(int clickType, String emailAddress, long partnerId) throws Exception {
        long userLoggedId = accountService.getLoggedInAccountId();
        List<EmailAccount> emailAccounts =  list();
        List<EmailAccountToSendMailDTO> emailAccountDTOs =  new ArrayList<>();
        for(EmailAccount account : emailAccounts){
            EmailAccountSetting accountSetting = emailAccountSettingService.findOneSend(account.getId());
            if(accountSetting!=null){
                String greeting = greetingService.getGreetings(account.getId(), clickType, emailAddress, userLoggedId, partnerId);
                EmailAccountToSendMailDTO acc = new EmailAccountToSendMailDTO(account, accountSetting.getCc(), greeting);
                emailAccountDTOs.add(acc);
            }
        }
        return emailAccountDTOs;
    }

    public List<EmailAccountToSendMailDTO> getListEmailAccountSendToMany() throws Exception {
        long userLoggedId = accountService.getLoggedInAccountId();
        List<EmailAccount> emailAccounts =  list();
        List<EmailAccountToSendMailDTO> emailAccountDTOs =  new ArrayList<>();
        for(EmailAccount account : emailAccounts){
            EmailAccountSetting accountSetting = emailAccountSettingService.findOneSend(account.getId());
            if(accountSetting!=null){
                Greeting greeting = greetingService.getGreetingStructure(userLoggedId, account.getId(), Greeting.Type.SEND_TO_MULTIL_EMAIL_ADDRESS);
                EmailAccountToSendMailDTO acc = null;
                if(greeting != null){
                    acc = new EmailAccountToSendMailDTO(account, accountSetting.getCc(), greeting.getGreeting());
                }else{
                    acc = new EmailAccountToSendMailDTO(account, accountSetting.getCc());
                }
                emailAccountDTOs.add(acc);
            }
        }
        return emailAccountDTOs;
    }

    @Cacheable(key="\"MailAccountsService:findById:\"+#id")
    public List<EmailAccount> findById(long id){
        return emailAccountDAO.findById(id);
    }

    @CacheEvict(allEntries = true)
    public EmailAccount save(EmailAccount account){
        if(account.isAlertSend()) {
            List<EmailAccount> alertSends = findAlertSend();
            if(alertSends != null && alertSends.size() > 0) {
                for(EmailAccount deAlert : alertSends) {
                    deAlert.setAlertSend(false);
                }
                emailAccountDAO.save(alertSends);
            }
        }
        return emailAccountDAO.save(account);
    }

    public EmailAccount findOneAlertSend() {
        List<EmailAccount> alertSends = findAlertSend();
        return alertSends != null && alertSends.size() > 0 ? alertSends.get(0) : null;
    }

    public List<EmailAccount> findAlertSend() {
        List<EmailAccount> alertSends = emailAccountDAO.findByAlertSend(true);
        return alertSends;
    }

    @CacheEvict(allEntries = true)
    public void delete(long accountId, boolean deleteMail){
        //TODO: transactional
        emailAccountDAO.delete(accountId);
        if(deleteMail){
            List<Email> emailsForDelete = emailDAO.findByAccountIdOrderBySentAtDesc(accountId);
            emailDAO.delete(emailsForDelete);
        }
    }
    @Cacheable(key="\"MailAccountsService:findAccountAddress:\"+#id")
    public String findAccountAddress(long id) {
        List<EmailAccount> accountList = findById(id);
        String account = "";
        if(accountList.size() > 0) {
            String realAccount = accountList.get(0).getAccount();
            if(realAccount != null) {
                account = realAccount;
            }
        }
        return account;
    }

    public List<EmailAccountEngineerDTO> getEmailAccountForSendMailEngineer(long engineerId) throws Exception {
        List<EmailAccount> listAccount = list();
        Engineer engineer = engineerService.getEngineerById(engineerId);
        if(engineer == null ||  engineer.getMailAddress() == null){
            throw new Exception("Email engineer has been remove or doesn't exsit");
        }
        String username = accountService.getLastNameUserLogged();
        List<EmailAccountEngineerDTO> accountDTOs =  new ArrayList<>();
        for(EmailAccount account : listAccount){
            EmailAccountEngineerDTO accountDTO = new EmailAccountEngineerDTO();
            accountDTO.setId(account.getId());
            accountDTO.setAccount(account.getAccount());
            accountDTO.setAlertSend(account.isAlertSend());
            accountDTO.setDisabled(account.isDisabled());
            accountDTO.setSignature(account.getSignature());
            int index = account.getAccount().indexOf("@");
            if(index>0){
                String domain = (account.getAccount().substring(index+1).toLowerCase());
                List<BusinessPartner> partners = partnerService.getPartnersByDomain(domain);
                String greeting = "";
                if(partners.size()>0){
                    if(partners.get(0).getId() == engineer.getPartnerId()){
                        greeting = engineer.getLastName() + "さん<br />" + "お疲れ様です　" + username + "です。<br /><br />";
                    }else{
                        BusinessPartner partnerEngineer = partnerService.findOne(engineer.getPartnerId());
                        if(partnerEngineer != null){
                            greeting = partnerEngineer.getName() +"　" + engineer.getLastName() + "様<br />" + "お世話になっております。" + partners.get(0).getName() + "の" + username + "です。<br /><br />";
                        }

                    }
                    accountDTO.setGreeting(greeting);
                }
            }
            accountDTOs.add(accountDTO);
        }
        return accountDTOs;
    }
}
