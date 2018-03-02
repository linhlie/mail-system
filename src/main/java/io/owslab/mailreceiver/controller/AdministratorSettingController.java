package io.owslab.mailreceiver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;

/**
 * Created by khanhlvb on 2/28/18.
 */
@Controller
@RequestMapping("/admin/")
public class AdministratorSettingController {

    //TODO: change admin login id and password, validate with current password
    //TODO: only one id can login to admin system
    @RequestMapping(value = { "/administratorSetting" }, method = RequestMethod.GET)
    public String index(Model model) {
        return "admin/setting";
    }
}
