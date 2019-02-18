package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.AccountDTO;
import io.owslab.mailreceiver.dto.ConditionNotificationDTO;
import io.owslab.mailreceiver.model.ConditionNotification;
import io.owslab.mailreceiver.response.ConditionNotificationResponseBody;
import io.owslab.mailreceiver.service.condition.ConditionNotificationService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.utils.SelectOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
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

    @RequestMapping(value = "user/emailStatisticSetting", method = RequestMethod.GET)
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
        return "user/emailStatistic/settings";
    }

    @RequestMapping(value = { "user/statistic/matchingConditionNotification" }, method = RequestMethod.GET)
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
}
