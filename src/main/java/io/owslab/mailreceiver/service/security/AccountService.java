package io.owslab.mailreceiver.service.security;

import io.owslab.mailreceiver.dao.AccountDAO;
import io.owslab.mailreceiver.form.AdministratorSettingForm;
import io.owslab.mailreceiver.form.RegisterAccountForm;
import io.owslab.mailreceiver.form.UserAccountForm;
import io.owslab.mailreceiver.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<String> roles = new ArrayList<>();
        roles.add(Account.Role.MEMBER);
        roles.add(Account.Role.MEMBER_EXPANSION);
        return accountDAO.findByUserRoleIn(roles);
    }

    public void delete(long id){
        accountDAO.delete(id);
    }

    public void saveUser(UserAccountForm form) {
        Account user = new Account();
        if(form.getId() != null) {
            long id = Long.parseLong(form.getId());
            Account existUser = accountDAO.findOne(id);
            if(existUser != null) {
                updateUser(existUser, form);
            } else {
                addUser(form);
            }
        } else {
            addUser(form);
        }

    }

    private void updateUser(Account user, UserAccountForm form) {
        user.setUserName(form.getUserName());
        user.setName(form.getName());
        user.setActive(true);
        String newPassword = form.getNewPassword();
        if(newPassword != null && newPassword.length() > 0) {
            user.setEncryptedPassword(passwordEncoder.encode(newPassword));
        }
        if(form.isExpansion()) {
            user.setUserRole(Account.Role.MEMBER_EXPANSION);
        } else {
            user.setUserRole(Account.Role.MEMBER);
        }
        accountDAO.save(user);
    }

    private void addUser(UserAccountForm form) {
        Account user = new Account();
        user.setUserName(form.getUserName());
        user.setName(form.getName());
        user.setEncryptedPassword(passwordEncoder.encode(form.getNewPassword()));
        user.setActive(true);
        if(form.isExpansion()) {
            user.setUserRole(Account.Role.MEMBER_EXPANSION);
        } else {
            user.setUserRole(Account.Role.MEMBER);
        }
        accountDAO.save(user);
    }
}
