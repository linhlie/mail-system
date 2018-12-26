package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.enums.AuthenticationProtocol;
import io.owslab.mailreceiver.enums.EncryptionProtocol;
import io.owslab.mailreceiver.enums.MailProtocol;
import io.owslab.mailreceiver.model.EmailAccountSetting;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

/**
 * Created by khanhlvb on 9/25/18.
 */
public class MailUtils {
    public synchronized static Store createStore(EmailAccountSetting accountSettings, boolean isDebug) throws NoSuchProviderException {
        Properties properties = buildProperties(accountSettings);
        MailProtocol mailProtocol = MailProtocol.fromValue(accountSettings.getMailProtocol());
        String protocol = mailProtocol.getText();
        properties.put("mail.debug", isDebug);
        Session emailSession = Session.getInstance(properties);
        emailSession.setDebug(isDebug);
        Store store = emailSession.getStore(protocol);
        return store;
    }
    public synchronized static Store createStore(EmailAccountSetting accountSettings) throws NoSuchProviderException {
        return createStore(accountSettings, false);
    }

    public synchronized static Properties buildProperties(EmailAccountSetting accountSettings) {
        Properties properties = new Properties();
        MailProtocol mailProtocol = MailProtocol.fromValue(accountSettings.getMailProtocol());
        String protocol = mailProtocol.getText();
        EncryptionProtocol encryptionProtocol = EncryptionProtocol.fromValue(accountSettings.getEncryptionProtocol());
        AuthenticationProtocol authenticationProtocol = AuthenticationProtocol.fromValue(accountSettings.getAuthenticationProtocol());
        properties.put("mail." + protocol + ".host", accountSettings.getMailServerAddress());
        properties.put("mail." + protocol + ".port", accountSettings.getMailServerPort());
        if(mailProtocol.equals(MailProtocol.SMTP)) {
            properties.put("mail.smtp.auth", "true");
        }
        switch (encryptionProtocol) {
            case NONE:
                properties.put("mail." + protocol +".ssl.enable", "false");
                break;
            case SSL:
                properties.put("mail." + protocol +".ssl.enable", "true");
                break;
            case STARTTLS:
                if(!mailProtocol.equals(MailProtocol.SMTP)) {
                    properties.put("mail." + protocol +".ssl.enable", "true");
                }
                properties.put("mail." + protocol +".starttls.enable", "true");
                break;
        }
        return properties;
    }
}
