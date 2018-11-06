package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.PartnerForPeopleInChargeDTO;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/expansion/")
public class PeopleInChargePartnerController {
    private static final Logger logger = LoggerFactory.getLogger(PeopleInChargePartnerController.class);

    @Autowired
    BusinessPartnerService partnerService;

    @RequestMapping(value = { "/peopleInChargePartner" }, method = RequestMethod.GET)
    public String getBusinessPartnerRegist(Model model, HttpServletRequest request) {
        return "expansion/peopleInChargePartner";
    }

    @RequestMapping(value = { "/peopleInChargePartner/getPartners" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getPartners() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<PartnerForPeopleInChargeDTO> partners = partnerService.getPartnerForPeopleInCharge();
            result.setList(partners);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getPartners: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }
}
