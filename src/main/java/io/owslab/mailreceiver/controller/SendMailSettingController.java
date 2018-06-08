package io.owslab.mailreceiver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by khanhlvb on 6/8/18.
 */

@Controller
@RequestMapping("/user/")
public class SendMailSettingController {

    @RequestMapping(value = "/sendMailSettings", method = RequestMethod.GET)
    public String getSendMailSettings(Model model) {
        return "user/sendMailSettings/form";
    }
}