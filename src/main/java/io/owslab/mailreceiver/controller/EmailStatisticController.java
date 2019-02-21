package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.AccountDTO;
import io.owslab.mailreceiver.dto.ConditionNotificationDTO;
import io.owslab.mailreceiver.dto.EmailStatisticDTO;
import io.owslab.mailreceiver.dto.ExtractMailDTO;
import io.owslab.mailreceiver.enums.ClickType;
import io.owslab.mailreceiver.form.EmailStatisticDetailForm;
import io.owslab.mailreceiver.form.ExtractForm;
import io.owslab.mailreceiver.form.MatchingConditionForm;
import io.owslab.mailreceiver.form.StatisticConditionForm;
import io.owslab.mailreceiver.model.ConditionNotification;
import io.owslab.mailreceiver.response.ConditionNotificationResponseBody;
import io.owslab.mailreceiver.response.MatchingResponeBody;
import io.owslab.mailreceiver.service.condition.ConditionNotificationService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.service.statistics.EmailStatisticService;
import io.owslab.mailreceiver.utils.FinalMatchingResult;
import io.owslab.mailreceiver.utils.SelectOption;
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
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/")
public class EmailStatisticController {
    private static final Logger logger = LoggerFactory.getLogger(EmailStatisticController.class);

    @Autowired
    private NumberTreatmentService numberTreatmentService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private List<SelectOption> combineOptions;

    @Autowired
    private List<SelectOption> conditionOptions;

    @Autowired
    private List<SelectOption> mailItemOptions;

    @Autowired
    private List<SelectOption> matchingItemOptions;

    @Autowired
    private ConditionNotificationService conditionNotificationService;

    @Autowired
    private EmailStatisticService emailStatisticService;

    @RequestMapping(value = "emailStatistic/emailStatisticSetting", method = RequestMethod.GET)
    public String getMatchingSettings(Model model) {
        model.addAttribute("combineOptions", combineOptions);
        model.addAttribute("conditionOptions", conditionOptions);
        model.addAttribute("mailItemOptions", mailItemOptions);
        model.addAttribute("matchingItemOptions", matchingItemOptions);

        List<String> numberConditionSetting = numberTreatmentService.getNumberSetting();
        List<AccountDTO> accountDTOS = accountService.getAllUserRoleAccountDTOs();
        model.addAttribute("ruleNumber", numberConditionSetting.get(0));
        model.addAttribute("ruleNumberDownRate", numberConditionSetting.get(1));
        model.addAttribute("ruleNumberUpRate", numberConditionSetting.get(2));
        model.addAttribute("accounts", accountDTOS);
        return "user/emailStatistic/settings";
    }

    @RequestMapping(value = {"/emailStatistic/statisticConditionNotification"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getStatisticNotificationCondition() {
        ConditionNotificationResponseBody result = new ConditionNotificationResponseBody();
        try {
            long accountId = accountService.getLoggedInAccountId();
            long statisticNewnotification = conditionNotificationService.getNewConditionNotifications(accountId, ConditionNotification.Condition_Type.STATISTIC_CONDITION);
            List<ConditionNotificationDTO> statisticConditions = conditionNotificationService.getConditionNotifications(accountId, ConditionNotification.Condition_Type.STATISTIC_CONDITION, 1);

            result.setDestinationNotification(statisticNewnotification);
            result.setDestinationNotificationList(statisticConditions);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getStatisticNotificationCondition: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }


    @RequestMapping(value = "emailStatistic/emailStatisticResult", method = RequestMethod.GET)
    public String getMatchingResult(Model model) {
        return "user/emailStatistic/result";
    }

    @PostMapping("/emailStatistic/submitForm")
    @ResponseBody
    public ResponseEntity<?> submitForm(
            Model model,
            @Valid @RequestBody StatisticConditionForm statisticConditionForm, BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        MatchingResponeBody result = new MatchingResponeBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<EmailStatisticDTO> list = emailStatisticService.statisticEmail(statisticConditionForm);
            result.setMsg("done");
            result.setStatus(true);
            result.setList(list);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("emailStatisticSubmitForm " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "emailStatistic/showDetail", method = RequestMethod.GET)
    public String showDetail(Model model) {
        return "user/emailStatistic/showDetail";
    }

    @PostMapping("/emailStatistic/submitEmailDetailRequest")
    @ResponseBody
    public ResponseEntity<?> showEmailDetailRequest(Model model, @Valid @RequestBody EmailStatisticDetailForm form, BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        MatchingResponeBody result = new MatchingResponeBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<ExtractMailDTO> extractResult = emailStatisticService.getDetailEmails(form);
            result.setMsg("done");
            result.setStatus(true);
            result.setList(extractResult);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("showEmailDetailRequest " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }
}
