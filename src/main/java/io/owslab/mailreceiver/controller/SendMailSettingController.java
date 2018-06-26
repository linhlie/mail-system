package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by khanhlvb on 6/8/18.
 */

@Controller
@RequestMapping("/user/")
public class SendMailSettingController {

    @Autowired
    private MailAccountsService mailAccountsService;
    @RequestMapping(value = "/sendMailSettings", method = RequestMethod.GET)
    public String getSendMailSettings(Model model) {
        List<EmailAccount> emailAccountList = mailAccountsService.list();
        model.addAttribute("accounts", emailAccountList);
        return "user/sendMailSettings/form";
    }
}