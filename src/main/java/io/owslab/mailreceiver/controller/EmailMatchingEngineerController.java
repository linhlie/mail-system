package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.AccountDTO;
import io.owslab.mailreceiver.dto.ConditionNotificationDTO;
import io.owslab.mailreceiver.dto.EngineerMatchingDTO;
import io.owslab.mailreceiver.enums.ClickType;
import io.owslab.mailreceiver.form.EmailMatchingEngineerForm;
import io.owslab.mailreceiver.form.EngineerFilterForm;
import io.owslab.mailreceiver.model.ConditionNotification;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.response.ConditionNotificationResponseBody;
import io.owslab.mailreceiver.response.MatchingResponeBody;
import io.owslab.mailreceiver.service.condition.ConditionNotificationService;
import io.owslab.mailreceiver.service.expansion.EngineerService;
import io.owslab.mailreceiver.service.matching.EmailMatchingEngineerService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.service.statistics.ClickHistoryService;
import io.owslab.mailreceiver.utils.FinalEmailMatchingEngineerResult;
import io.owslab.mailreceiver.utils.SelectOption;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by khanhlvb on 3/5/18.
 */
@Controller
@RequestMapping("/user/")
public class EmailMatchingEngineerController {

    private static final Logger logger = LoggerFactory.getLogger(EmailMatchingEngineerController.class);
    @Autowired
    private List<SelectOption> combineOptions;

    @Autowired
    private List<SelectOption> conditionOptions;

    @Autowired
    private List<SelectOption> mailItemOptions;

    @Autowired
    private List<SelectOption> matchingItemOptions;

    @Autowired
    private EmailMatchingEngineerService emailMatchingEngineerService;

    @Autowired
    private ClickHistoryService clickHistoryService;
    
    @Autowired
    private EngineerService engineerService;

    @Autowired
    private NumberTreatmentService numberTreatmentService;

    @Autowired
    private AccountService accountService;

    @Autowired
    ConditionNotificationService conditionNotificationService;

    @RequestMapping(value = "/emailMatchingEngineerSetting", method = RequestMethod.GET)
    public String getMatchingSettings(Model model) {
        model.addAttribute("combineOptions", combineOptions);
        model.addAttribute("conditionOptions", conditionOptions);
        model.addAttribute("mailItemOptions", mailItemOptions);
        model.addAttribute("matchingItemOptions", matchingItemOptions);

        List<String> numberConditionSetting = numberTreatmentService.getNumberSetting();
        List<AccountDTO> accountDTOS = accountService.getAllUserRoleAccountDTOs();
        model.addAttribute("ruleNumber",numberConditionSetting.get(0));
        model.addAttribute("ruleNumberDownRate",numberConditionSetting.get(1));
        model.addAttribute("ruleNumberUpRate",numberConditionSetting.get(2));
        model.addAttribute("accounts",accountDTOS);
        return "user/emailMatchingEngineer/setting";
    }
    
    @RequestMapping(value = { "/engineerMatching/list" }, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getEngineers( @Valid @RequestBody EngineerFilterForm form, BindingResult bindingResult) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<EngineerMatchingDTO> engineers = engineerService.filterEngineerMatching(form, now);
            result.setList(engineers);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getPartners: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }
    
    @RequestMapping(value = "/emailMatchingEngineerResult", method = RequestMethod.GET)
    public String getMatchingResult(Model model) {
        return "user/emailMatchingEngineer/result";
    }
    
    @PostMapping("/emailMatchingEngineer/submitForm")
    @ResponseBody
    public ResponseEntity<?> submitForm(Model model, @Valid @RequestBody EmailMatchingEngineerForm form, BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        MatchingResponeBody result = new MatchingResponeBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            clickHistoryService.save(ClickType.EMAIL_MATCHING_ENGINEER.getValue());
        	FinalEmailMatchingEngineerResult finalResult = emailMatchingEngineerService.matchingEmailsWithEngineerCondition(form);
            result.setMsg("done");
            result.setStatus(true);
            result.setList(finalResult.getListEngineerMatching());
            result.setMailList(finalResult.getMailList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            String error = ExceptionUtils.getStackTrace(e);
            logger.error("emailMatchingEngineer/submitForm: " + error);
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = { "/engineerMatching/matchingConditionNotification" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getNotificationCondition() {
        ConditionNotificationResponseBody result = new ConditionNotificationResponseBody();
        try {
            long accountId = accountService.getLoggedInAccountId();
            long destinationNewnotification = conditionNotificationService.getNewConditionNotifications(accountId, ConditionNotification.Condition_Type.ENGINEER_MATCHING_CONDITION);
            List<ConditionNotificationDTO> destinationConditions = conditionNotificationService.getConditionNotifications(accountId, ConditionNotification.Condition_Type.ENGINEER_MATCHING_CONDITION, 1);

            result.setDestinationNotification(destinationNewnotification);
            result.setDestinationNotificationList(destinationConditions);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getNotificationCondition: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }
}
