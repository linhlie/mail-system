package io.owslab.mailreceiver.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by khanhlvb on 8/29/18.
 */
public class MyUser extends User {
    private String profileName;
    private long userId;

    public MyUser(String username, String password, Collection<? extends GrantedAuthority> authorities, long userId) {
        this(username, password, authorities, null, userId);
    }

    public MyUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String profileName, long userId) {
        super(username, password, authorities);
        this.profileName = profileName;
        this.userId = userId;
    }

    public MyUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, long userId) {
        this(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, null, userId);
    }

    public MyUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String profileName, long userId) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.profileName = profileName;
        this.userId = userId;
    }

    public String getProfileName() {
        return profileName;
    }

    public long getUserId() {
        return userId;
    }
}

