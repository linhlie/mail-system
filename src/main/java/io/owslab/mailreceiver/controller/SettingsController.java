package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.EmailAccountDTO;
import io.owslab.mailreceiver.form.*;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.mail.EmailAccountSettingService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.utils.FileAssert;
import io.owslab.mailreceiver.utils.FileAssertResult;
import io.owslab.mailreceiver.utils.PageWrapper;
import io.owslab.mailreceiver.validator.MailAccountSettingValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.*;

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
        redirectAttrs.addFlashAttribute("saved", true);
        return "redirect:/admin/enviromentSettings";
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

    @RequestMapping(value = { "/mailAccountSettings/add" }, method = RequestMethod.POST)
    public String saveReceiveAccount(
            Model model, @ModelAttribute("fullAccountForm") FullAccountForm fullAccountForm,
            BindingResult result, RedirectAttributes redirectAttrs) {
        mailAccountSettingValidator.validate(fullAccountForm, result);
        if (result.hasErrors()) {
            return "admin/settings/account/form";
        }
        MailAccountForm mailAccountForm = fullAccountForm.getMailAccountForm();
        ReceiveAccountForm receiveAccountForm = fullAccountForm.getReceiveAccountForm();
        SendAccountForm sendAccountForm = fullAccountForm.getSendAccountForm();
        EmailAccount emailAccount = mailAccountsService.save(new EmailAccount(mailAccountForm));
        EmailAccountSetting newReceiveAccountSetting = new EmailAccountSetting(receiveAccountForm, true);
        newReceiveAccountSetting.setAccountId(emailAccount.getId());
        emailAccountSettingService.save(newReceiveAccountSetting);
        EmailAccountSetting newSendAccountSetting = new EmailAccountSetting(sendAccountForm, true);
        newSendAccountSetting.setAccountId(emailAccount.getId());
        emailAccountSettingService.save(newSendAccountSetting);
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
        System.out.println("sendAccountForm: cc: " + sendAccountForm.getCc());
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
            emailAccount.setSignature(mailAccountForm.getSignature());
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
    public String receiveRuleSettings() {
        return "admin/settings/receive_mail_rule_settings";
    }
}
