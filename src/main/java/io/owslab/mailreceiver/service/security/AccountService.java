package io.owslab.mailreceiver.service.security;

import io.owslab.mailreceiver.dao.AccountDAO;
import io.owslab.mailreceiver.form.RegisterAccountForm;
import io.owslab.mailreceiver.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
