package io.owslab.mailreceiver.validator;

import io.owslab.mailreceiver.form.FullAccountForm;
import io.owslab.mailreceiver.form.MailAccountForm;
import io.owslab.mailreceiver.form.ReceiveAccountForm;
import io.owslab.mailreceiver.form.SendAccountForm;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import io.owslab.mailreceiver.service.mail.EmailAccountSettingService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.mail.*;
import java.util.List;
import java.util.Properties;

@Component
public class MailAccountSettingValidator implements Validator {

    @Autowired
    private EmailAccountSettingService emailAccountSettingService;
    @Autowired
    private MailAccountsService mailAccountsService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == FullAccountForm.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        FullAccountForm fullAccountForm = (FullAccountForm) target;
        MailAccountForm mailAccountForm = fullAccountForm.getMailAccountForm();
        ReceiveAccountForm receiveAccountForm = fullAccountForm.getReceiveAccountForm();
        SendAccountForm sendAccountForm = fullAccountForm.getSendAccountForm();
        EmailAccount emailAccount = new EmailAccount(mailAccountForm);
        EmailAccountSetting newReceiveAccountSetting = new EmailAccountSetting(receiveAccountForm, true);
        newReceiveAccountSetting.setAccountId(emailAccount.getId());
        EmailAccountSetting newSendAccountSetting = new EmailAccountSetting(sendAccountForm, true);
        newSendAccountSetting.setAccountId(emailAccount.getId());
        try {
            check(emailAccount, newReceiveAccountSetting);
        } catch (Exception e) {
            e.printStackTrace();
            errors.rejectValue("rUserName", "Authentication.fullAccountForm.rUserName");
        }

        try {
            checkSend(emailAccount, newSendAccountSetting);
        } catch (Exception e) {
            e.printStackTrace();
            errors.rejectValue("sUserName", "Authentication.fullAccountForm.sUserName");
        }
    }

    private void check(EmailAccount account, EmailAccountSetting accountSetting) throws Exception {
        try {
            Store store = MailUtils.createStore(accountSetting);
            if(accountSetting.getUserName() != null && accountSetting.getUserName().length() > 0){
                store.connect(accountSetting.getMailServerAddress(), accountSetting.getUserName(), accountSetting.getPassword());
            } else {
                store.connect(accountSetting.getMailServerAddress(), account.getAccount(), accountSetting.getPassword());
            }
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("check failed");
        }
    }

    private void checkSend(EmailAccount account, EmailAccountSetting accountSetting) throws Exception {
        try {
            Properties props = MailUtils.buildProperties(accountSetting);
            Session session = Session.getInstance(props, null);
            String host = accountSetting.getMailServerAddress();
            int port = accountSetting.getMailServerPort();
            String user;
            if(accountSetting.getUserName() != null && accountSetting.getUserName().length() > 0){
                user = accountSetting.getUserName();
            } else {
                user = account.getAccount();
            }
            String pwd = accountSetting.getPassword();
            Transport transport = session.getTransport("smtp");
            transport.connect(host, port, user, pwd);
            transport.close();
        }
        catch(AuthenticationFailedException e) {
            throw new Exception("check failed");
        }
        catch(MessagingException e) {
            throw new Exception("check failed");
        }
    }
}
