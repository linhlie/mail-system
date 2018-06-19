package io.owslab.mailreceiver.controller;

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
    @RequestMapping(value = "/greetingRegistration", method = RequestMethod.GET)
    public String getFuzzyWord(Model model) {
        return "user/greetingRegistration";
    }
}
