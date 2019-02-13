package io.owslab.mailreceiver.service.security;

import io.owslab.mailreceiver.dao.AccountDAO;
import io.owslab.mailreceiver.dto.AccountDTO;
import io.owslab.mailreceiver.form.AdministratorSettingForm;
import io.owslab.mailreceiver.form.RegisterAccountForm;
import io.owslab.mailreceiver.form.UserAccountForm;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.MyUser;
import io.owslab.mailreceiver.service.bulletin.BulletinPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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

    @Autowired
    private BulletinPermissionService bulletinPermissionService;

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

    public Account findById(Long id){
        return accountDAO.findOne(id);
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
        user.setLastName(form.getLastName());
        user.setFirstName(form.getFirstName());
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
        user.setLastName(form.getLastName());
        user.setFirstName(form.getFirstName());
        user.setEncryptedPassword(passwordEncoder.encode(form.getNewPassword()));
        user.setActive(true);
        if(form.isExpansion()) {
            user.setUserRole(Account.Role.MEMBER_EXPANSION);
        } else {
            user.setUserRole(Account.Role.MEMBER);
        }
        Account userSaved = accountDAO.save(user);
        if(form.getId() == null && userSaved != null) {
            bulletinPermissionService.createPermissionForNewAccount(userSaved);
        }
    }

    public long getLoggedInAccountId() {
        MyUser user = (MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUserId();
    }

    public List<AccountDTO> getAllUserRoleAccountDTOs(){
        List<AccountDTO> result = new ArrayList<>();
        List<Account> accounts=getAllUserRoleAccounts();
        long currentAccountId = getLoggedInAccountId();
        for(Account account : accounts){
            if (currentAccountId != account.getId()){
                AccountDTO accountDTO = new AccountDTO(account);
                result.add(accountDTO);
            }
        }
        return result;
    }

    public String getLastNameUserLogged(){
        Long userId = getLoggedInAccountId();
        Account account = findById(userId);
        return  account.getLastName();
    }
}
