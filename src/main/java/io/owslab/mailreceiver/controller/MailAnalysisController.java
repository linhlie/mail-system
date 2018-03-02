package io.owslab.mailreceiver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by khanhlvb on 2/28/18.
 */
@Controller
@RequestMapping("/user/")
public class MailAnalysisController {
    @RequestMapping(value = "/mailAnalysis", method = RequestMethod.GET)
    public String getMailAnalysisPage(Model model) {
        return "user/mailAnalysis/form";
    }
}
