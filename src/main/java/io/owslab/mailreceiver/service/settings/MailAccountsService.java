package io.owslab.mailreceiver.service.settings;

import io.owslab.mailreceiver.dao.EmailAccountDAO;
import io.owslab.mailreceiver.dao.EmailAccountSettingsDAO;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 1/24/18.
 */
@Service
public class MailAccountsService {

    @Autowired
    private EmailAccountDAO emailAccountDAO;

    @Autowired
    private EmailDAO emailDAO;

    public Page<EmailAccount> list(PageRequest pageRequest) {
        Page<EmailAccount> list = emailAccountDAO.findAll(pageRequest);
        return list;
    }

    public List<EmailAccount> findById(long id){
        return emailAccountDAO.findById(id);
    }

    public EmailAccount save(EmailAccount account){
        return emailAccountDAO.save(account);
    }

    public void delete(long accountId, boolean deleteMail){
        //TODO: transactional
        emailAccountDAO.delete(accountId);
        if(deleteMail){
            List<Email> emailsForDelete = emailDAO.findByAccountIdOrderBySentAtDesc(accountId);
            emailDAO.delete(emailsForDelete);
        }
    }
}
