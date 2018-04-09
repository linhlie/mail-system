package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailAccountDAO;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dao.EmailAccountSettingsDAO;
import io.owslab.mailreceiver.job.IMAPFetchMailJob;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by khanhlvb on 1/19/18.
 */
@Service
public class FetchMailsService {
    private static final Logger logger = LoggerFactory.getLogger(FetchMailsService.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    @Autowired
    private EmailAccountSettingService emailAccountSettingService;

    @Autowired
    private EmailAccountDAO emailAccountDAO;

    @Autowired
    private EmailDAO emailDAO;

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @Autowired
    private MailBoxService mailBoxService;

    public void start(){
        List<EmailAccount> list = emailAccountDAO.findByDisabled(false);
        List<Callable<Void>> callables = new ArrayList<>();
        if(list.size() > 0) {
            for(int i = 0, n = list.size(); i < n; i++){
                EmailAccount account = list.get(i);
                EmailAccountSetting accountSetting = emailAccountSettingService.findOneReceive(account.getId());
                if(accountSetting != null && accountSetting.getMailProtocol() == EmailAccountSetting.Protocol.IMAP){
                    callables.add(toCallable(new IMAPFetchMailJob(emailDAO, fileDAO, enviromentSettingService, accountSetting, account)));
                }
            }
        }
        try {
            List<Future<Void>> futures = executorService.invokeAll(callables);
            mailBoxService.getAll(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Callable<Void> toCallable(final Runnable runnable) {
        return new Callable<Void>() {
            @Override
            public Void call() {
                runnable.run();
                return null;
            }
        };
    }
}

