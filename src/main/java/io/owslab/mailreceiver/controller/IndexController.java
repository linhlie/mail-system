package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.service.mail.MailBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private MailBoxService mailBoxService;

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
        return "admin";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }

}
