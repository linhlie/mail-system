package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.*;
import io.owslab.mailreceiver.enums.ClickType;
import io.owslab.mailreceiver.form.*;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.response.ConditionNotificationResponseBody;
import io.owslab.mailreceiver.response.DetailMailResponseBody;
import io.owslab.mailreceiver.response.MatchingResponeBody;
import io.owslab.mailreceiver.service.condition.ConditionNotificationService;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.mail.SendMailService;
import io.owslab.mailreceiver.service.matching.MatchingConditionService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.service.statistics.ClickHistoryService;
import io.owslab.mailreceiver.utils.FinalMatchingResult;
import io.owslab.mailreceiver.utils.SelectOption;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by khanhlvb on 3/5/18.
 */
@Controller
@RequestMapping("/user/")
public class MatchingSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(MatchingSettingsController.class);
    @Autowired
    private List<SelectOption> combineOptions;

    @Autowired
    private List<SelectOption> conditionOptions;

    @Autowired
    private List<SelectOption> mailItemOptions;

    @Autowired
    private List<SelectOption> matchingItemOptions;

    @Autowired
    private MatchingConditionService matchingConditionService;

    @Autowired
    private MailBoxService mailBoxService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @Autowired
    private MailAccountsService mailAccountsService;

    @Autowired
    private ClickHistoryService clickHistoryService;

    @Autowired
    private NumberTreatmentService numberTreatmentService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ConditionNotificationService conditionNotificationService;

    @RequestMapping(value = "/matchingSettings", method = RequestMethod.GET)
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
        return "user/matching/settings";
    }

    @RequestMapping(value="/matchingSettings/source", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getSourceInJSON (){
        AjaxResponseBody result = new AjaxResponseBody();

        result.setMsg("done");
        result.setStatus(true);
        result.setList(matchingConditionService.getSourceConditionList());
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value="/matchingSettings/destination", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getDestinationInJSON (){
        AjaxResponseBody result = new AjaxResponseBody();

        result.setMsg("done");
        result.setStatus(true);
        result.setList(matchingConditionService.getDestinationConditionList());
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value="/matchingSettings/matching", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getMatchingInJSON (){
        AjaxResponseBody result = new AjaxResponseBody();

        result.setMsg("done");
        result.setStatus(true);
        result.setList(matchingConditionService.getMatchingConditionList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/matchingSettings/submitForm")
    @ResponseBody
    public ResponseEntity<?> submitForm(
            Model model,
            @Valid @RequestBody MatchingConditionForm matchingConditionForm, BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        MatchingResponeBody result = new MatchingResponeBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            clickHistoryService.save(ClickType.MATCHING.getValue());
            FinalMatchingResult finalMatchingResult = matchingConditionService.matching(matchingConditionForm);
            result.setMsg("done");
            result.setStatus(true);
            result.setList(finalMatchingResult.getMatchingResultList());
            result.setMailList(finalMatchingResult.getMailList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/matchingResult", method = RequestMethod.GET)
    public String getMatchingResult(Model model) {
        List<String> numberConditionSetting = numberTreatmentService.getNumberSetting();
        model.addAttribute("ruleNumber",numberConditionSetting.get(0));
        model.addAttribute("ruleNumberDownRate",numberConditionSetting.get(1));
        model.addAttribute("ruleNumberUpRate",numberConditionSetting.get(2));
        return "user/matching/result";
    }

    @RequestMapping(value="/matchingResult/email", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getEmailInJSON (
            @RequestParam(value = "messageId", required = true) String messageId,
            @RequestParam(value = "highlightWord", required = false) String highlightWord,
            @RequestParam(value = "matchRange", required = false) String matchRange,
            @RequestParam(value = "spaceEffective", required = false) boolean spaceEffective,
            @RequestParam(value = "distinguish", required = false) boolean distinguish
    ){
        AjaxResponseBody result = new AjaxResponseBody();
        List<DetailMailDTO> mailDetail = mailBoxService.getMailDetail(messageId, highlightWord, matchRange, spaceEffective, distinguish);
        result.setMsg("done");
        result.setStatus(true);
        result.setList(mailDetail);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value="/matchingResult/editEmail", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getEditEmailInJSON (@RequestParam(value = "messageId") String messageId,
                                          @RequestParam(value = "type") int type,
                                          @RequestParam(value = "replyId") String replyId,
                                          @RequestParam(value = "receiver") String receiver,
                                          @RequestParam(value = "range", required = false) String range,
                                          @RequestParam(value = "matchRange", required = false) String matchRange,
                                          @RequestParam(value = "replaceType", required = false) int replaceType,
                                          @RequestParam(value = "accountId", required = false) String accountId){
        DetailMailResponseBody result = new DetailMailResponseBody();
        try {
            clickHistoryService.save(type);
            DetailMailDTO mailDetail = mailBoxService.getMailDetailWithReplacedRange(messageId, replyId, range, matchRange, replaceType, accountId);
            List<EmailAccountToSendMailDTO> accountList = mailAccountsService.getListEmailAccount(type, receiver, -1);
            result.setList(accountList);
            result.setMsg("done");
            result.setStatus(true);
            result.setMail(mailDetail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("getEditEmailInJSON: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value="/matchingResult/replyEmail", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getReplyEmailInJSON (@RequestParam(value = "messageId") String messageId, @RequestParam(value = "type") int type,
                                           @RequestParam(value = "receiver") String receiver){
        DetailMailResponseBody result = new DetailMailResponseBody();
        try {
            clickHistoryService.save(type);
            DetailMailDTO mailDetail = mailBoxService.getContentRelyEmail(messageId);
            List<EmailAccountToSendMailDTO> accountList = mailAccountsService.getListEmailAccount(type, receiver, -1);
            result.setMsg("done");
            result.setStatus(true);
            result.setMail(mailDetail);
            result.setList(accountList);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("replyEmail: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/sendRecommendationMail")
    @ResponseBody
    public ResponseEntity<?> sendRecommendationMail(
            Model model,
            @Valid @RequestBody SendMailForm sendMailForm, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            sendMailService.sendMail(sendMailForm);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("sendRecommendationMail: " + e.getMessage());
            e.printStackTrace();
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = { "/sendReplyRecommendationMail/getMailAccounts" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getMailAccounts() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<EmailAccount> accounts = mailAccountsService.list();
            result.setList(accounts);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getMailAccounts: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sendReplyRecommendationMail")
    @ResponseBody
    public ResponseEntity<?> sendReplyRecommendationMail(
            Model model,
            @Valid @RequestBody SendMultilMailForm sendMailForm, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            mailBoxService.sendMultilMail(sendMailForm);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("sendReplyRecommendationMail: " + e.getMessage());
            e.printStackTrace();
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value="/matchingResult/envSettings", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getEnvSettingsInJSON () throws JSONException{
        AjaxResponseBody result = new AjaxResponseBody();
        JSONObject obj = new JSONObject();
        obj.put("debug_on", enviromentSettingService.getDebugOn());
        obj.put("debug_receive_mail_address", enviromentSettingService.getDebugReceiveMailAddress());
        result.setMsg(obj.toString());
        result.setStatus(true);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/extractSource", method = RequestMethod.GET)
    public String getExtractSource(Model model) {
        model.addAttribute("extractTitle", "比較メール元のみ抽出");
        model.addAttribute("extractResult", "絞り込み元");
        return "user/matching/extract";
    }

    @RequestMapping(value = "/extractDestination", method = RequestMethod.GET)
    public String getExtractDestination(Model model) {
        model.addAttribute("extractTitle", "比較メール先のみ抽出");
        model.addAttribute("extractResult", "絞り込み先");
        return "user/matching/extract";
    }

    @RequestMapping(value = "/extractEmailStatistic", method = RequestMethod.GET)
    public String getExtractEmailStatistic(Model model) {
        model.addAttribute("extractTitle", "集計対象メール一覧");
        model.addAttribute("extractResult", "絞り込み");
        return "user/matching/extract";
    }

    @PostMapping("/submitExtract")
    @ResponseBody
    public ResponseEntity<?> submitExtract(
            Model model,
            @Valid @RequestBody ExtractForm extractForm, BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        MatchingResponeBody result = new MatchingResponeBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            clickHistoryService.save(extractForm.getType());
            List<ExtractMailDTO> extractResult = matchingConditionService.extract(extractForm);
            result.setMsg("done");
            result.setStatus(true);
            result.setList(extractResult);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/sendTab", method = RequestMethod.GET)
    public String getSendTab() {
        return "user/matching/sendTab";
    }

    @RequestMapping(value = { "/matchingSettings/matchingConditionNotification" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getNotificationCondition() {
        ConditionNotificationResponseBody result = new ConditionNotificationResponseBody();
        try {
            long accountId = accountService.getLoggedInAccountId();
            long sourceNewnotification = conditionNotificationService.getNewConditionNotifications(accountId, ConditionNotification.Condition_Type.SOURCE_CONDITION);
            long destinationNewnotification = conditionNotificationService.getNewConditionNotifications(accountId, ConditionNotification.Condition_Type.DESTINATION_CONDITION);
            long matchingNewnotification = conditionNotificationService.getNewConditionNotifications(accountId, ConditionNotification.Condition_Type.MATCHING_CONDITION);
            List<ConditionNotificationDTO> sourceConditions = conditionNotificationService.getConditionNotifications(accountId, ConditionNotification.Condition_Type.SOURCE_CONDITION, 1);
            List<ConditionNotificationDTO> destinationConditions = conditionNotificationService.getConditionNotifications(accountId, ConditionNotification.Condition_Type.DESTINATION_CONDITION, 1);
            List<ConditionNotificationDTO> matchingConditions = conditionNotificationService.getConditionNotifications(accountId, ConditionNotification.Condition_Type.MATCHING_CONDITION, 1);

            result.setSourceNotification(sourceNewnotification);
            result.setDestinationNotification(destinationNewnotification);
            result.setMatchingNotification(matchingNewnotification);

            result.setSourceNotificationList(sourceConditions);
            result.setDestinationNotificationList(destinationConditions);
            result.setMatchingNotificationList(matchingConditions);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getNotificationCondition: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/conditionNotification/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addConditionNotification(
            @Valid @RequestBody ConditionNotification form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            conditionNotificationService.add(form);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("addConditionNotification: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/conditionNotification/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateConditionNotification(
            @Valid @RequestBody ConditionNotificationDTO form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            conditionNotificationService.update(form);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("updateConditionNotification: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/conditionNotification/showMore")
    @ResponseBody
    public ResponseEntity<?> showMoreConditionNotifications(@Valid @RequestBody ConditionNotificationDTO form, BindingResult bindingResult) {
        MatchingResponeBody result = new MatchingResponeBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<ConditionNotificationDTO> conditionNotificationDTOS = conditionNotificationService.showMoreConditionNotifications(form);
            result.setMsg("done");
            result.setStatus(true);
            result.setList(conditionNotificationDTOS);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("showMoreConditionNotifications: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);       
        }
    }

}
