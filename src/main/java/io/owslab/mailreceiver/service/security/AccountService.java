package io.owslab.mailreceiver.service.security;

import io.owslab.mailreceiver.dao.AccountDAO;
import io.owslab.mailreceiver.form.AdministratorSettingForm;
import io.owslab.mailreceiver.form.RegisterAccountForm;
import io.owslab.mailreceiver.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 3/1/18.
 */
@Service
public class AccountService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountDAO accountDAO;

    public Account createNewAccount(RegisterAccountForm form) {
        String encrytedPassword = this.passwordEncoder.encode(form.getPassword());
        Account account = new Account(form.getUserName(), encrytedPassword);
        accountDAO.save(account);
        return account;
    }

    public void updateAdmin(AdministratorSettingForm form){
        Account admin = getAdmin();
        if(admin != null) {
            admin.setUserName(form.getUserName());
            admin.setEncryptedPassword(passwordEncoder.encode(form.getNewPassword()));
            accountDAO.save(admin);
        }
    }

    public Account getAdmin(){
        List<Account> adminList = accountDAO.findByUserRole(Account.Role.ADMIN);
        return adminList.size() > 0 ? adminList.get(0) : null;
    }

    public Account findOne(String userName){
        List<Account> accounts = accountDAO.findByUserName(userName);
        return accounts.size() > 0 ? accounts.get(0) : null;
    }

    public List<Account> getAllUserRoleAccounts(){
        return accountDAO.findByUserRole(Account.Role.MEMBER);
    }

    public void delete(long id){
        accountDAO.delete(id);
    }
}
