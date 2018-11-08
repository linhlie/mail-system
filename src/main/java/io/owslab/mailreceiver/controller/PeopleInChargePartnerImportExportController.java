package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.service.expansion.EngineerService;
import io.owslab.mailreceiver.service.expansion.PeopleInChargePartnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by khanhlvb on 8/30/18.
 */
@Controller
@RequestMapping("/expansion/")
public class PeopleInChargePartnerImportExportController {
    private static final Logger logger = LoggerFactory.getLogger(PeopleInChargePartnerImportExportController.class);
    @Autowired
    private PeopleInChargePartnerService peopleInChargePartnerService;

    @RequestMapping(value = { "/peopleInChargePartnerImportExport" }, method = RequestMethod.GET)
    public String peopleInChargePartnerImportExport(Model model, HttpServletRequest request) {
        return "expansion/peopleInChargePartnerImportExport";
    }
}
