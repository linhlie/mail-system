package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.service.mail.FetchMailsService;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class IndexController {

    @Autowired
    private MailBoxService mailBoxService;

    @Autowired
    private FetchMailsService fetchMailsService;

    private DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @RequestMapping("/default")

    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin/";
        }
        return "redirect:/";
    }

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin/";
        }
        return "index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        long numberOfMessage = mailBoxService.count();
        model.addAttribute("numberOfMessage", numberOfMessage);
        FetchMailsService.FetchMailProgress mailProgress = fetchMailsService.getTotalFetchMailProgress();
        model.addAttribute("mailProgressRemain", mailProgress.getTotal() - mailProgress.getDone());
        model.addAttribute("mailProgressLastUpdate", df.format(new Date()));
        return "admin";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }

}
