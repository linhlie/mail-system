package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.EngineerListItemDTO;
import io.owslab.mailreceiver.dto.PartnerDTO;
import io.owslab.mailreceiver.form.EngineerFilterForm;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.greeting.GreetingService;
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
import org.springframework.web.bind.annotation.*;

/**
 * Created by khanhlvb on 6/19/18.
 */
@Controller
@RequestMapping("/user/")
public class GreetingRegistrationController {
	private static final Logger logger = LoggerFactory.getLogger(GreetingRegistrationController.class);
	
	@Autowired
    private MailAccountsService mailAccountsService;

    @Autowired
    private GreetingService greetingService;

	@RequestMapping(value = "/greetingRegistration", method = RequestMethod.GET)
	public String greetingRegistration(Model model) {
        List<EmailAccount> emailAccountList = mailAccountsService.list();
        model.addAttribute("accounts", emailAccountList);
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

    @RequestMapping(value = "/greetingRegistration/{emailId}" , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> greetingRegistrationByEmailId(@PathVariable("emailId") long emailId) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<Greeting> greetings = greetingService.getByEmailAccount(emailId);
            result.setList(greetings);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("greetingRegistrationByEmailId: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/greetingRegistration/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addGreeting( @Valid @RequestBody Greeting greeting, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            greetingService.addGreeting(greeting);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("addGreeting: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/greetingRegistration/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateGreeting( @Valid @RequestBody Greeting greeting, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            greetingService.updateGreeting(greeting);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("updateGreeting: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/greetingRegistration/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        try {
            greetingService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
