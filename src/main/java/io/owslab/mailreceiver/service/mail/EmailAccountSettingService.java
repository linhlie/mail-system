package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailAccountSettingsDAO;
import io.owslab.mailreceiver.form.ReceiveAccountForm;
import io.owslab.mailreceiver.form.SendAccountForm;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 3/27/18.
 */

@Service
public class EmailAccountSettingService {
    @Autowired
    private EmailAccountSettingsDAO emailAccountSettingsDAO;

    public ReceiveAccountForm getReceiveAccountForm(long accountId){
        EmailAccountSetting accountSetting = findOneReceive(accountId);
        ReceiveAccountForm form = accountSetting != null ? new ReceiveAccountForm(accountSetting) : new ReceiveAccountForm();
        if(form.getMailServerPort() == 0){ form.setMailServerPort(993); };
        return form;
    }

    public SendAccountForm getSendAccountForm(long accountId){
        EmailAccountSetting accountSetting = findOneSend(accountId);
        SendAccountForm form = accountSetting != null ? new SendAccountForm(accountSetting) : new SendAccountForm();
        if(form.getMailServerPort() == 0) { form.setMailServerPort(25); };
        return form;
    }

    public EmailAccountSetting findOneReceive(long accountId){
        return findOneWithAccountIdAndType(accountId, EmailAccountSetting.Type.RECEIVE);
    }

    public EmailAccountSetting findOneSend(long accountId){
        return findOneWithAccountIdAndType(accountId, EmailAccountSetting.Type.SEND);
    }

    public EmailAccountSetting findOneSendByEmail(String email){
        List<EmailAccountSetting> emailAccountSettingList = emailAccountSettingsDAO.findByUserNameAndType(email, EmailAccountSetting.Type.SEND);
        return emailAccountSettingList.size() > 0 ? emailAccountSettingList.get(0) : null;
    }

    private EmailAccountSetting findOneWithAccountIdAndType(long accountId, int type){
        List<EmailAccountSetting> emailAccountSettingList = emailAccountSettingsDAO.findByAccountIdAndType(accountId, type);
        return emailAccountSettingList.size() > 0 ? emailAccountSettingList.get(0) : null;
    }

    public void save(EmailAccountSetting accountSetting){
        emailAccountSettingsDAO.save(accountSetting);
    }
}

