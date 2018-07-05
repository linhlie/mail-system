package io.owslab.mailreceiver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by khanhlvb on 7/5/18.
 */
@Controller
@RequestMapping("/user/")
public class ExtractConditionManagementController {
    @RequestMapping(value = { "/extractConditionManagement" }, method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        return "user/extractConditionManagement";
    }
}
