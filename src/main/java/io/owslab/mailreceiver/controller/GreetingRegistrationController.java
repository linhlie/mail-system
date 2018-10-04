package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.service.settings.MailAccountsService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by khanhlvb on 6/19/18.
 */
@Controller
@RequestMapping("/user/")
public class GreetingRegistrationController {
    @Autowired
    private MailAccountsService mailAccountsService;
	
    @RequestMapping(value = "/greetingRegistration", method = RequestMethod.GET)
    public String getFuzzyWord(Model model) {
        List<EmailAccount> emailAccountList = mailAccountsService.list();
        model.addAttribute("accounts", emailAccountList);
        return "user/greetingRegistration";
    }
}
