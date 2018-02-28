package io.owslab.mailreceiver.service.settings;

import io.owslab.mailreceiver.dao.ReceiveEmailAccountSettingsDAO;
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
    private ReceiveEmailAccountSettingsDAO accountSettingsDAO;

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
}
