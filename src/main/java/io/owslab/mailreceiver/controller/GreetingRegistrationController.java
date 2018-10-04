package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.EngineerListItemDTO;
import io.owslab.mailreceiver.dto.PartnerDTO;
import io.owslab.mailreceiver.form.EngineerFilterForm;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by khanhlvb on 6/19/18.
 */
@Controller
@RequestMapping("/user/")
public class GreetingRegistrationController {
	private static final Logger logger = LoggerFactory.getLogger(GreetingRegistrationController.class);
	
	@Autowired
    private MailAccountsService mailAccountsService;

	@RequestMapping(value = "/greetingRegistration", method = RequestMethod.GET)
	public String getFuzzyWord(Model model) {
		return "user/greetingRegistration";
	}
	
    @RequestMapping(value = { "/greetingRegistration/getEmailAccounts" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAccounts() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
        	List<EmailAccount> emailAccountList = mailAccountsService.list();
            result.setList(emailAccountList);
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
