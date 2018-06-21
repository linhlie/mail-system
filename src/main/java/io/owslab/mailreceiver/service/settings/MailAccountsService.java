package io.owslab.mailreceiver.service.settings;

import io.owslab.mailreceiver.dao.EmailAccountDAO;
import io.owslab.mailreceiver.dao.EmailAccountSettingsDAO;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

    public Page<EmailAccount> list(PageRequest pageRequest) {
        Page<EmailAccount> list = emailAccountDAO.findAll(pageRequest);
        return list;
    }

    @Cacheable(key="\"MailAccountsService:findById:\"+#id")
    public List<EmailAccount> findById(long id){
        return emailAccountDAO.findById(id);
    }

    @CacheEvict(allEntries = true)
    public EmailAccount save(EmailAccount account){
        return emailAccountDAO.save(account);
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
}
