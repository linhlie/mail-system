package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.DetailMailDTO;
import io.owslab.mailreceiver.dto.ExtractMailDTO;
import io.owslab.mailreceiver.form.ExtractForm;
import io.owslab.mailreceiver.form.MatchingConditionForm;
import io.owslab.mailreceiver.form.SendMailForm;
import io.owslab.mailreceiver.model.ClickHistory;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.response.DetailMailResponseBody;
import io.owslab.mailreceiver.response.MatchingResponeBody;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.mail.SendMailService;
import io.owslab.mailreceiver.service.matching.MatchingConditionService;
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
public class EmailMatchingEngineerController {

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

    @RequestMapping(value = "/emailMatchingEngineerSetting", method = RequestMethod.GET)
    public String getMatchingSettings(Model model) {
        model.addAttribute("combineOptions", combineOptions);
        model.addAttribute("conditionOptions", conditionOptions);
        model.addAttribute("mailItemOptions", mailItemOptions);
        model.addAttribute("matchingItemOptions", matchingItemOptions);
        return "user/emailMatchingEngineer/emailMatchingEngineerSetting";
    }
}
