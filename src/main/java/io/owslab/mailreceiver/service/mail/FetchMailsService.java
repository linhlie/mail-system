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

    private List<FetchMailProgress> mailProgressList = new ArrayList<>();

    public void start(){
        List<EmailAccount> list = emailAccountDAO.findByDisabled(false);
        List<Callable<Void>> callables = new ArrayList<>();
        if(list.size() > 0) {
            for(int i = 0, n = list.size(); i < n; i++){
                EmailAccount account = list.get(i);
                EmailAccountSetting accountSetting = emailAccountSettingService.findOneReceive(account.getId());
                if(accountSetting != null && accountSetting.getMailProtocol() == EmailAccountSetting.Protocol.IMAP){
                    callables.add(toCallable(new IMAPFetchMailJob(accountSetting, account)));
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

    public FetchMailProgress getMailProgressInstance(){
        FetchMailProgress progress = new FetchMailProgress();
        mailProgressList.add(progress);
        return progress;
    }

    public class FetchMailProgress {
        private int done;
        private int total;

        public FetchMailProgress() {
            this.done = 0;
            this.total = 0;
        }

        public FetchMailProgress(int done, int total) {
            this.done = done;
            this.total = total;
        }

        public int getDone() {
            return done;
        }

        public void setDone(int done) {
            this.done = done;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public void increase(){
            this.setDone(this.getDone()+1);
        }

        public void increaseTotal(){
            this.setTotal(this.getTotal()+1);
        }

        public void decreaseTotal(){
            this.setTotal(this.getTotal()-1);
        }

        public void completed(){
            this.setDone(this.getTotal());
        }
    }

    public FetchMailProgress getTotalFetchMailProgress(){
        FetchMailProgress result = new FetchMailProgress();
        for(FetchMailProgress mailProgress : mailProgressList){
            result.setDone(result.getDone() + mailProgress.getDone());
            result.setTotal(result.getTotal() + mailProgress.getTotal());
        }
        return result;
    }
}

