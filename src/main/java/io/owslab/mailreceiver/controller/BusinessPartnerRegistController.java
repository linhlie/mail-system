package io.owslab.mailreceiver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by khanhlvb on 7/25/18.
 */
@Controller
@RequestMapping("/expansion/")
public class BusinessPartnerRegistController {
    private static final Logger logger = LoggerFactory.getLogger(BusinessPartnerRegistController.class);

    @RequestMapping(value = { "/businessPartnerRegist" }, method = RequestMethod.GET)
    public String getBusinessPartnerRegist(Model model, HttpServletRequest request) {
        return "expansion/businessPartnerRegist";
    }
}
