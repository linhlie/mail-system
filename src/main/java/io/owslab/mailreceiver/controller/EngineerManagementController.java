package io.owslab.mailreceiver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by khanhlvb on 8/15/18.
 */
@Controller
@RequestMapping("/expansion/")
public class EngineerManagementController {
    @RequestMapping(value = { "/engineerManagement" }, method = RequestMethod.GET)
    public String getEngineerManagement(Model model, HttpServletRequest request) {
        return "expansion/engineerManagement";
    }
}
