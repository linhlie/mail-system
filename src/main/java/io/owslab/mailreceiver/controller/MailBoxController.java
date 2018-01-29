package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.ReceiveAccountForm;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.RelativeSentAtEmail;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * Created by khanhlvb on 1/26/18.
 */
@Controller
public class MailBoxController {
    @Autowired
    private MailBoxService mailBoxService;

    @RequestMapping(value = "/mailbox", method = RequestMethod.GET)
    public String receiveSettings(@RequestParam(value = "search", required = false) String search, Model model) {
        List<RelativeSentAtEmail> relativeSentAtEmailList = new ArrayList<RelativeSentAtEmail>();
        List<Email> list = search == null ? mailBoxService.list() : mailBoxService.searchContent(search);
        for(int i = 0, n = list.size(); i < n; i++){
            Email email = list.get(i);
            relativeSentAtEmailList.add(new RelativeSentAtEmail(email));
        }
        if(search != null){
            model.addAttribute("search", search);
        }
        model.addAttribute("list", relativeSentAtEmailList);
        return "mailbox/list";
    }
}
