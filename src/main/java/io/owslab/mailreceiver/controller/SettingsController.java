package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.EmailAccountDTO;
import io.owslab.mailreceiver.dto.ReceiveRuleDTO;
import io.owslab.mailreceiver.exception.EmailAccountException;
import io.owslab.mailreceiver.form.*;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import io.owslab.mailreceiver.model.ReceiveRule;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.response.JsonStringResponseBody;
import io.owslab.mailreceiver.service.errror.ReportErrorService;
import io.owslab.mailreceiver.service.mail.EmailAccountSettingService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.service.settings.MailReceiveRuleService;
import io.owslab.mailreceiver.service.transaction.EmailAccountTransaction;
import io.owslab.mailreceiver.utils.FileAssert;
import io.owslab.mailreceiver.utils.FileAssertResult;
import io.owslab.mailreceiver.utils.PageWrapper;
import io.owslab.mailreceiver.validator.MailAccountSettingValidator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/")
public class SettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    public static final int PAGE_SIZE = 5;

    @Autowired
    private MailAccountsService mailAccountsService;

    @Autowired
    private EmailAccountSettingService emailAccountSettingService;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @Autowired
    private MailAccountSettingValidator mailAccountSettingValidator;

    @Autowired
    private MailReceiveRuleService mailReceiveRuleService;

    @Autowired
    private EmailAccountTransaction emailAccountTransaction;

    @Autowired
    private NumberTreatmentService numberTreatmentService;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }

        if (target.getClass() == FullAccountForm.class) {
            dataBinder.setValidator(mailAccountSettingValidator);
        }
    }

    @RequestMapping(value = "/enviromentSettings", method = RequestMethod.GET)
    public String enviromentSettings(Model model, RedirectAttributes redirectAttrs) {
        HashMap<String, String> map = enviromentSettingService.getAll();
        EnviromentSettingForm enviromentSettingForm = new EnviromentSettingForm();
        enviromentSettingForm.setMap(map);
        model.addAttribute("enviromentSettingForm", enviromentSettingForm);
        return "admin/settings/enviroment_settings";
    }

    @RequestMapping(value = "/enviromentSettings", method = RequestMethod.POST)
    public String updateEnviromentSettings(Model model, @ModelAttribute("enviromentSettingForm") EnviromentSettingForm enviromentSettingForm, RedirectAttributes redirectAttrs) {
        Map<String, String> map = enviromentSettingForm.getMap();
        for (String key : map.keySet()) {
            enviromentSettingService.set(key, map.get(key));
        }
        ReportErrorService.updateAdministratorMailAddress();
        redirectAttrs.addFlashAttribute("saved", true);
        return "redirect:/admin/enviromentSettings";
    }

    @RequestMapping(value="/enviromentSettings/getFullPath", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getFullPath (@RequestParam(value = "folderName", required = true) String folderName){
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<FileAssertResult> assertResults = new ArrayList<>();
            String fullpath = FileAssert.findFullPath(folderName);
            FileAssertResult assertResult = FileAssert.getRootPath(fullpath);
            assertResults.add(assertResult);
            result.setList(assertResults);
            result.setMsg(fullpath);
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value="/enviromentSettings/storagePath", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getDirectoryTree (@RequestParam(value = "path", required = false) String path, @RequestParam(value = "subFolders", required = false) boolean subFolders){
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            path = path == null ? "/" : path;
            FileAssertResult assertResult = FileAssert.getDirectoryTree(new File(path), subFolders);
            List<FileAssertResult> assertResults = new ArrayList<>();
            assertResults.add(assertResult);
            result.setMsg("done");
            result.setStatus(true);
            result.setList(assertResults);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value="/enviromentSettings/createSubFolder", method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity<?> createSubFolder (@RequestParam(value = "path", required = true) String path){
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            File directory = new File(path);
            if (!directory.exists()){
                directory.mkdirs();
            }
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("createSubFolder: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/mailAccountSettings", method = RequestMethod.GET)
    public String getMailAccountSettings(@RequestParam(value = "page", required = false, defaultValue = "1") int page, Model model) {
        page = page - 1;
        PageRequest pageRequest = new PageRequest(page, PAGE_SIZE);
        Page<EmailAccount> pages = mailAccountsService.list(pageRequest);
        PageWrapper<EmailAccount> pageWrapper = new PageWrapper<EmailAccount>(pages, "/admin/mailAccountSettings");
        List<EmailAccount> list = pages.getContent();
        List<EmailAccountDTO> dtoList = new ArrayList<>();
        for(EmailAccount emailAccount : list){
            dtoList.add(new EmailAccountDTO(emailAccount));
        }
        int rowsInPage = list.size();
        int fromEntry = rowsInPage == 0 ? 0 : page * PAGE_SIZE + 1;
        int toEntry = rowsInPage == 0 ? 0 : fromEntry + rowsInPage - 1;
        model.addAttribute("list", dtoList);
        model.addAttribute("page", pageWrapper);
        model.addAttribute("fromEntry", fromEntry);
        model.addAttribute("toEntry", toEntry);
        return "admin/settings/mail_accounts_settings";
    }
    @RequestMapping(value = "/mailAccountSettings/add", method = RequestMethod.GET)
    public String getAddMailAccount(Model model) {
        FullAccountForm fullAccountForm = new FullAccountForm();
        fullAccountForm.setrMailServerPort(993);
        fullAccountForm.setsMailServerPort(25);
        model.addAttribute("fullAccountForm", fullAccountForm);
        model.addAttribute("api", "/admin/mailAccountSettings/add");
        return "admin/settings/account/form";
    }

    //need transaction
    @RequestMapping(value = { "/mailAccountSettings/add" }, method = RequestMethod.POST)
    public String saveReceiveAccount(
            Model model, @ModelAttribute("fullAccountForm") FullAccountForm fullAccountForm,
            BindingResult result, RedirectAttributes redirectAttrs) {
        mailAccountSettingValidator.validate(fullAccountForm, result);
        if (result.hasErrors()) {
            return "admin/settings/account/form";
        }
        try {
            emailAccountTransaction.saveEmailAccountTransaction(fullAccountForm);
        } catch (EmailAccountException e) {
            logger.error("Save account email fail "+e.toString());
            model.addAttribute("errorMessage", "メールアカウントが登録できませんでした。");
            return "admin/settings/account/form";
        }
        return "redirect:/admin/mailAccountSettings";
    }

    @RequestMapping(value = "/mailAccountSettings/update", method = RequestMethod.GET)
    public String getAccount(@RequestParam(value = "id", required = true) long id, Model model) {
        List<EmailAccount> listAccount = mailAccountsService.findById(id);
        if(listAccount.isEmpty()){
            //TODO: account not found error
        }
        EmailAccount account = listAccount.get(0);
        MailAccountForm mailAccountForm = new MailAccountForm(account);
        ReceiveAccountForm receiveAccountForm = emailAccountSettingService.getReceiveAccountForm(account.getId());
        SendAccountForm sendAccountForm = emailAccountSettingService.getSendAccountForm(account.getId());
        FullAccountForm fullAccountForm = new FullAccountForm(mailAccountForm, receiveAccountForm, sendAccountForm);
        fullAccountForm.setAccountId(id);
        model.addAttribute("fullAccountForm", fullAccountForm);
        model.addAttribute("api", "/admin/mailAccountSettings/update?id=" + id);
        return "admin/settings/account/form";
    }

    @RequestMapping(value = "/mailAccountSettings/update", method = RequestMethod.POST)
    public String updateReceiveAccount(@RequestParam(value = "id") long id, Model model, @ModelAttribute("fullAccountForm") FullAccountForm fullAccountForm,
                                       BindingResult result, RedirectAttributes redirectAttrs) {
        List<EmailAccount> listAccount = mailAccountsService.findById(id);
        if(listAccount.isEmpty()){
            //TODO: account not found error
        }
        else {
            fullAccountForm.setAccountId(id);
            mailAccountSettingValidator.validate(fullAccountForm, result);
            if (result.hasErrors()) {
                return "admin/settings/account/form";
            }
            MailAccountForm mailAccountForm = fullAccountForm.getMailAccountForm();
            ReceiveAccountForm receiveAccountForm = fullAccountForm.getReceiveAccountForm();
            SendAccountForm sendAccountForm = fullAccountForm.getSendAccountForm();
            EmailAccount emailAccount = listAccount.get(0);
            emailAccount.setDisabled(mailAccountForm.isDisabled());
            emailAccount.setAlertSend(mailAccountForm.isAlertSend());
            emailAccount.setSignature(mailAccountForm.getSignature());
            emailAccount.setInChargeCompany(mailAccountForm.getInChargeCompany());
            mailAccountsService.save(emailAccount);
            EmailAccountSetting existReceiveAccountSetting = emailAccountSettingService.findOneReceive(id);
            EmailAccountSetting newReceiveAccountSetting = new EmailAccountSetting(receiveAccountForm, true);
            newReceiveAccountSetting.setAccountId(emailAccount.getId());
            if(existReceiveAccountSetting != null) newReceiveAccountSetting.setId(existReceiveAccountSetting.getId());
            emailAccountSettingService.save(newReceiveAccountSetting);
            EmailAccountSetting existSendAccountSetting = emailAccountSettingService.findOneSend(id);
            EmailAccountSetting newSendAccountSetting = new EmailAccountSetting(sendAccountForm, true);
            newSendAccountSetting.setAccountId(emailAccount.getId());
            if(existSendAccountSetting != null) newSendAccountSetting.setId(existSendAccountSetting.getId());
            emailAccountSettingService.save(newSendAccountSetting);
            ReportErrorService.updateSendAccountInfo();
        }
        return "redirect:/admin/mailAccountSettings";
    }

    @RequestMapping(value = { "/deleteAccount" }, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> deleteAccount(
            Model model,
            @RequestParam(value = "id") long id, @RequestParam(value = "deleteMail") boolean deleteMail) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            mailAccountsService.delete(id, deleteMail);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("deleteAccount: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping("/receiveRuleSettings")
    public String receiveRuleSettings(Model model) {
        List<String> numberConditionSetting = numberTreatmentService.getNumberSetting();
        model.addAttribute("ruleNumber",numberConditionSetting.get(0));
        model.addAttribute("ruleNumberDownRate",numberConditionSetting.get(1));
        model.addAttribute("ruleNumberUpRate",numberConditionSetting.get(2));
        return "admin/settings/receive_mail_rule_settings";
    }

    @RequestMapping(value = "/receiveRuleSettings/load", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> loadReceiveRuleSettings() {
        JsonStringResponseBody result = new JsonStringResponseBody();
        try {
            JSONObject json = mailReceiveRuleService.getReceiveRuleSettings();
            List<ReceiveRuleDTO> list = mailReceiveRuleService.getRuleNameList();
            result.setJson(json.toString());
            result.setList(list);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("loadReceiveRuleSettings: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/receiveRuleSettings/saveReceiveReceiveRuleBundle", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveReceiveReceiveRuleBundle(
            @Valid @RequestBody ReceiveRuleBundleForm form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            mailReceiveRuleService.saveReceiveRuleBundle(form);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("saveReceiveReceiveRuleBundle: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/receiveRuleSettings/saveMarkReflectionScopeBundle", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveMarkReflectionScopeBundle(
            @Valid @RequestBody MarkReflectionScopeBundleForm form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            mailReceiveRuleService.saveMarkReflectionScopeBundle(form);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("saveMarkReflectionScopeBundle: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/receiveRuleSettings/saveRule", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveRule(
            @Valid @RequestBody ReceiveRuleForm form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            mailReceiveRuleService.saveRule(form);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("saveRule: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/receiveRuleSettings/getRule", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getRule(
            @Valid @RequestBody ReceiveRuleForm form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<ReceiveRule> rules = mailReceiveRuleService.getRulesByForm(form);
            result.setList(rules);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("saveRule: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }
}
