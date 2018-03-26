package io.owslab.mailreceiver.service.settings;

import io.owslab.mailreceiver.dao.EmailAccountSettingsDAO;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.model.Email;
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
public class ReceiveMailAccountsSettingsService {

    @Autowired
    private EmailAccountSettingsDAO accountSettingsDAO;

    @Autowired
    private EmailDAO emailDAO;

    public Page<EmailAccountSetting> list(PageRequest pageRequest) {
        Page<EmailAccountSetting> list = accountSettingsDAO.findAll(pageRequest);
        return list;
    }

    public List<EmailAccountSetting> findById(long id){
        return accountSettingsDAO.findById(id);
    }

    public void save(EmailAccountSetting account){
        accountSettingsDAO.save(account);
    }

    public void delete(long accountId, boolean deleteMail){
        //TODO: transactional
        accountSettingsDAO.delete(accountId);
        if(deleteMail){
            List<Email> emailsForDelete = emailDAO.findByAccountIdOrderBySentAtDesc(accountId);
            emailDAO.delete(emailsForDelete);
        }
    }
}
