package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.utils.SelectOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by khanhlvb on 2/28/18.
 */
@Controller
@RequestMapping("/user/")
public class MailAnalysisController {

    @Autowired
    private List<SelectOption> combineOptions;

    @Autowired
    private List<SelectOption> conditionOptions;

    @Autowired
    private List<SelectOption> mailItemOptions;

    @Autowired
    private List<SelectOption> matchingItemOptions;

    @RequestMapping(value = "/mailAnalysis", method = RequestMethod.GET)
    public String getMailAnalysisPage(Model model) {
        model.addAttribute("combineOptions", combineOptions);
        model.addAttribute("conditionOptions", conditionOptions);
        model.addAttribute("mailItemOptions", mailItemOptions);
        model.addAttribute("matchingItemOptions", matchingItemOptions);
        return "user/mailAnalysis/form";
    }
}
