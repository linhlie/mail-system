package io.owslab.mailreceiver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by khanhlvb on 3/2/18.
 */
@Controller
@RequestMapping("/admin/")
public class HelpController {
    @RequestMapping(value = { "/help" }, method = RequestMethod.GET)
    public String index(Model model) {
        return "admin/help";
    }
}
