package io.owslab.mailreceiver.service.security;

import io.owslab.mailreceiver.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 3/1/18.
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountService.findOne(username);

        if (account == null) {
            throw new UsernameNotFoundException("User " //
                    + username + " was not found in the database");
        }

        // USER,ADMIN,..
        String role = account.getUserRole();

        List<GrantedAuthority> grantList = new ArrayList<>();

        // ROLE_USER, ROLE_ADMIN
        GrantedAuthority authority = new SimpleGrantedAuthority(role);

        grantList.add(authority);

        boolean enabled = account.isActive();
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        UserDetails userDetails = (UserDetails) new User(account.getUserName(), //
                account.getEncryptedPassword(), enabled, accountNonExpired, //
                credentialsNonExpired, accountNonLocked, grantList);

        return userDetails;
    }
}
