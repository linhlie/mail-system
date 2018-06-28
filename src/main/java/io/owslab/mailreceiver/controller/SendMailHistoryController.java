package io.owslab.mailreceiver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by khanhlvb on 6/28/18.
 */

@Controller
@RequestMapping("/user/")
public class SendMailHistoryController {

    @RequestMapping(value = { "/sendMailHistory" }, method = RequestMethod.GET)
    public String getHelp(Model model, HttpServletRequest request) {
        return "user/sendMailHistory";
    }
}