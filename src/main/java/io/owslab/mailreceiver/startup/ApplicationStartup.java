package io.owslab.mailreceiver.startup;

import io.owslab.mailreceiver.dao.AccountDAO;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.service.replace.NumberRangeService;
import io.owslab.mailreceiver.service.schedulers.BuildMatchEmailWordScheduler;
import io.owslab.mailreceiver.service.schedulers.DeleteOldMailsScheduler;
import io.owslab.mailreceiver.service.schedulers.FetchMailScheduler;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationStartup {

    private static final String ADMIN_USER_NAME = "admin";
    private static final String ADMIN_PASSWORD = "Ows@2018";
    private static final String MEMBER_USER_NAME = "user001";
    private static final String MEMBER_PASSWORD = "OwsUser@2018";

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NumberRangeService numberRangeService;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    @EventListener
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        enviromentSettingService.init();
        addAdminAccount();
        addMemberAccount();
        fetchMailScheduler.start();
        deleteOldMailsScheduler.start();
        buildMatchEmailWordScheduler.start();
//        Email testEmail = new Email();
//        testEmail.setOptimizedBody("              There         +. -. are more than 以上-2K and less than +12万YEN　    ~　   15円 numbers here +13.2千~.2千 2 ~ 000,000 ３４千");
//        numberRangeService.buildNumberRangeForEmail(testEmail);
        return;
    }

    private void addAdminAccount(){
        List<Account> adminList = accountDAO.findByUserRole(Account.Role.ADMIN);
        Account admin = accountDAO.findOne(ADMIN_USER_NAME);
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
        Account user = accountDAO.findOne(MEMBER_USER_NAME);
        if(user == null){
            user = new Account();
            user.setUserName(MEMBER_USER_NAME);
            user.setEncryptedPassword(passwordEncoder.encode(MEMBER_PASSWORD));
            user.setActive(true);
            user.setUserRole(Account.Role.MEMBER);
            accountDAO.save(user);
        }
    }
}
