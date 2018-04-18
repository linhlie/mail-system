package io.owslab.mailreceiver.startup;

import io.owslab.mailreceiver.dao.AccountDAO;
import io.owslab.mailreceiver.enums.CombineOption;
import io.owslab.mailreceiver.enums.ConditionOption;
import io.owslab.mailreceiver.enums.MailItemOption;
import io.owslab.mailreceiver.enums.MatchingItemOption;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.replace.NumberRangeService;
import io.owslab.mailreceiver.service.schedulers.BuildMatchEmailWordScheduler;
import io.owslab.mailreceiver.service.schedulers.DeleteOldMailsScheduler;
import io.owslab.mailreceiver.service.schedulers.FetchMailScheduler;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.utils.FileAssert;
import io.owslab.mailreceiver.utils.SelectOption;
import io.owslab.mailreceiver.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class ApplicationStartup {

    private static final String ADMIN_USER_NAME = "admin";
    private static final String ADMIN_PASSWORD = "Ows@2018";
    private static final String MEMBER_USER_NAME = "user001";
    private static final String MEMBER_PASSWORD = "OwsUser@2018";

    public static final String DEFAULT_STORAGE_PATH = "./storages/";

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @Autowired
    private FetchMailScheduler fetchMailScheduler;

    @Autowired
    private DeleteOldMailsScheduler deleteOldMailsScheduler;

    @Autowired
    private BuildMatchEmailWordScheduler buildMatchEmailWordScheduler;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NumberRangeService numberRangeService;

    @Autowired
    private MailBoxService mailBoxService;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    @Bean
    public List<SelectOption> combineOptions() {
        List<SelectOption> list = new ArrayList<SelectOption>();
        for (CombineOption option : CombineOption.values()) {
            list.add(new SelectOption(option.getValue(), option.getText()));
        }
        return list;
    }

    @Bean
    public List<SelectOption> conditionOptions() {
        List<SelectOption> list = new ArrayList<SelectOption>();
        for (ConditionOption option : ConditionOption.values()) {
            list.add(new SelectOption(option.getValue(), option.getText()));
        }
        return list;
    }

    @Bean
    public List<SelectOption> mailItemOptions() {
        List<SelectOption> list = new ArrayList<SelectOption>();
        for (MailItemOption option : MailItemOption.values()) {
            list.add(new SelectOption(option.getValue(), option.getText()));
        }
        return list;
    }

    @Bean
    public List<SelectOption> matchingItemOptions() {
        List<SelectOption> list = new ArrayList<SelectOption>();
        for (MatchingItemOption option : MatchingItemOption.values()) {
            list.add(new SelectOption(option.getValue(), option.getText()));
        }
        return list;
    }

    @EventListener
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        Utils.init();
        initStorageDirectory();
        enviromentSettingService.init();
        addAdminAccount();
        addMemberAccount();
        mailBoxService.getAll(true);
        fetchMailScheduler.start();
        deleteOldMailsScheduler.start();
//        buildMatchEmailWordScheduler.start();
//        Email testEmail = new Email();
//        testEmail.setOptimizedBody("              There         +. -. are more than 以上-2K and less than +12万YEN　    ~　   15円 numbers here +13.2千~.2千 2 ~ 000,000 ３４千");
//        numberRangeService.buildNumberRangeForInput(testEmail.getOptimizedBody());
        return;
    }

    private void addAdminAccount(){
        List<Account> adminList = accountDAO.findByUserRole(Account.Role.ADMIN);
        Account admin = accountService.findOne(ADMIN_USER_NAME);
        if(admin == null && adminList.size() == 0){
            admin = new Account();
            admin.setUserName(ADMIN_USER_NAME);
            admin.setEncryptedPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setActive(true);
            admin.setUserRole(Account.Role.ADMIN);
            accountDAO.save(admin);
            logger.info("addAdminAccount");
        }
    }

    private void addMemberAccount(){
        Account user = accountService.findOne(MEMBER_USER_NAME);
        if(user == null){
            user = new Account();
            user.setUserName(MEMBER_USER_NAME);
            user.setEncryptedPassword(passwordEncoder.encode(MEMBER_PASSWORD));
            user.setActive(true);
            user.setUserRole(Account.Role.MEMBER);
            accountDAO.save(user);
        }
    }

    private void initStorageDirectory(){
        File directory = new File(DEFAULT_STORAGE_PATH);
        if (! directory.exists()){
            directory.mkdirs();
        }
    }
}
