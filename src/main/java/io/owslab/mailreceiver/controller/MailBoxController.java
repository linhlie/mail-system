package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.ReceiveAccountForm;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.RelativeSentAtEmail;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.utils.PageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public static final int PAGE_SIZE = 5;

    @Autowired
    private MailBoxService mailBoxService;

    @RequestMapping(value = "/mailbox", method = RequestMethod.GET)
    public String receiveSettings(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            Model model) {
        page = page - 1;
        List<RelativeSentAtEmail> relativeSentAtEmailList = new ArrayList<RelativeSentAtEmail>();
        PageRequest pageRequest = new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "sentAt");
        Page<Email> pages = search == null ? mailBoxService.list(pageRequest) : mailBoxService.searchContent(search, pageRequest);
        List<Email> list = pages.getContent();
        PageWrapper<Email> pageWrapper = new PageWrapper<Email>(pages, "/mailbox");
        for(int i = 0, n = list.size(); i < n; i++){
            Email email = list.get(i);
            relativeSentAtEmailList.add(new RelativeSentAtEmail(email));
        }
        if(search != null){
            model.addAttribute("search", search);
        }
        model.addAttribute("list", relativeSentAtEmailList);
        model.addAttribute("page", pageWrapper);
        return "mailbox/list";
    }
}
