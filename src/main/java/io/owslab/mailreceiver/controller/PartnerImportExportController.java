package io.owslab.mailreceiver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by khanhlvb on 8/24/18.
 */
@Controller
@RequestMapping("/expansion/")
public class PartnerImportExportController {
    private static final Logger logger = LoggerFactory.getLogger(PartnerImportExportController.class);

    @RequestMapping(value = { "/partnerImportExport" }, method = RequestMethod.GET)
    public String getPartnerImportExport(Model model, HttpServletRequest request) {
        return "expansion/partnerImportExport";
    }
}
