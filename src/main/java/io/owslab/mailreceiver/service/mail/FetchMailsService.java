package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dao.EmailAccountSettingsDAO;
import io.owslab.mailreceiver.job.IMAPFetchMailJob;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by khanhlvb on 1/19/18.
 */
@Service
public class FetchMailsService {
    private static final Logger logger = LoggerFactory.getLogger(FetchMailsService.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    @Autowired
    private EmailAccountSettingsDAO emailAccountSettingsDAO;

    @Autowired
    private EmailDAO emailDAO;

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    public void start(){
        List<EmailAccountSetting> list = emailAccountSettingsDAO.findByTypeAndDisabled(EmailAccountSetting.Type.RECEIVE, false);
        if(list.size() > 0) {
            for(int i = 0, n = list.size(); i < n; i++){
                EmailAccountSetting account = list.get(i);
                if(account.getMailProtocol() == EmailAccountSetting.Protocol.IMAP){
                    executorService.execute(new IMAPFetchMailJob(emailDAO, fileDAO, enviromentSettingService, account));
                }
            }
        }
    }
}

