package io.owslab.mailreceiver.service.transaction;

import io.owslab.mailreceiver.exception.EmailAccountException;
import io.owslab.mailreceiver.form.FullAccountForm;
import io.owslab.mailreceiver.form.MailAccountForm;
import io.owslab.mailreceiver.form.ReceiveAccountForm;
import io.owslab.mailreceiver.form.SendAccountForm;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import io.owslab.mailreceiver.service.errror.ReportErrorService;
import io.owslab.mailreceiver.service.mail.EmailAccountSettingService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class EmailAccountTransaction {
    @Autowired
    MailAccountsService mailAccountsService;

    @Autowired
    EmailAccountSettingService emailAccountSettingService;


    @Transactional(propagation = Propagation.REQUIRES_NEW,  rollbackFor = EmailAccountException.class)
    public void saveEmailAccountTransaction(FullAccountForm fullAccountForm) throws  EmailAccountException{
        MailAccountForm mailAccountForm = fullAccountForm.getMailAccountForm();
        ReceiveAccountForm receiveAccountForm = fullAccountForm.getReceiveAccountForm();
        SendAccountForm sendAccountForm = fullAccountForm.getSendAccountForm();
        EmailAccount emailAccount = saveEmailAccount(new EmailAccount(mailAccountForm));
        EmailAccountSetting newReceiveAccountSetting = new EmailAccountSetting(receiveAccountForm, true);
        newReceiveAccountSetting.setAccountId(emailAccount.getId());
        saveEmailAccountSetting(newReceiveAccountSetting);
        EmailAccountSetting newSendAccountSetting = new EmailAccountSetting(sendAccountForm, true);
        newSendAccountSetting.setAccountId(emailAccount.getId());
        saveEmailAccountSetting(newSendAccountSetting);
        ReportErrorService.updateSendAccountInfo();
    }

    @Transactional(propagation = Propagation.MANDATORY )
    public EmailAccount saveEmailAccount(EmailAccount emailAccount) throws EmailAccountException{
        return mailAccountsService.save(emailAccount);
    }

    @Transactional(propagation = Propagation.MANDATORY )
    public void saveEmailAccountSetting(EmailAccountSetting emailAccountSetting) throws EmailAccountException{
        emailAccountSettingService.save(emailAccountSetting);
    }

}
