package io.owslab.mailreceiver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;

import javax.sql.DataSource;

/**
 * Created by khanhlvb on 2/28/18.
 */
@Controller
public class AdministratorSettingController {

    @Autowired
    private DataSource datasource;

    //TODO: change admin login id and password, validate with current password
    //TODO: only one id can login to admin system
}
