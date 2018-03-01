package io.owslab.mailreceiver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by khanhlvb on 3/1/18.
 */
@Controller
public class RegisterController {

    @RequestMapping(value = { "/register" }, method = RequestMethod.GET)
    public String index(Model model) {
        return "register";
    }
}
