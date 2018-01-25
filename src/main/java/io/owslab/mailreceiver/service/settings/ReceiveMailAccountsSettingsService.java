package io.owslab.mailreceiver.service.settings;

import io.owslab.mailreceiver.dao.ReceiveEmailAccountSettingsDAO;
import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 1/24/18.
 */
@Service
public class ReceiveMailAccountsSettingsService {

    @Autowired
    private ReceiveEmailAccountSettingsDAO accountSettingsDAO;

    public List<ReceiveEmailAccountSetting> list() {
        List<ReceiveEmailAccountSetting> list = (List<ReceiveEmailAccountSetting>) accountSettingsDAO.findAll();
        return list;
    }

    public List<ReceiveEmailAccountSetting> findById(long id){
        return accountSettingsDAO.findById(id);
    }

    public void save(ReceiveEmailAccountSetting account){
        accountSettingsDAO.save(account);
    }
}
