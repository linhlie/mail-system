package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.EmailAccountSettingDTO;
import io.owslab.mailreceiver.form.EnviromentSettingForm;
import io.owslab.mailreceiver.form.ReceiveAccountForm;
import io.owslab.mailreceiver.form.SendAccountForm;
import io.owslab.mailreceiver.model.EnviromentSetting;
import io.owslab.mailreceiver.model.FuzzyWord;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.settings.ReceiveMailAccountsSettingsService;
import io.owslab.mailreceiver.service.word.FuzzyWordService;
import io.owslab.mailreceiver.service.word.WordService;
import io.owslab.mailreceiver.utils.FileAssert;
import io.owslab.mailreceiver.utils.FileAssertResult;
import io.owslab.mailreceiver.utils.PageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private ReceiveMailAccountsSettingsService accountsSettingsService;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

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
        Page<EmailAccountSetting> pages = accountsSettingsService.list(pageRequest);
        PageWrapper<EmailAccountSetting> pageWrapper = new PageWrapper<EmailAccountSetting>(pages, "/mailAccountSettings");
        List<EmailAccountSetting> list = pages.getContent();
        List<EmailAccountSettingDTO> dtoList = new ArrayList<>();
        for(EmailAccountSetting emailAccountSetting : list){
            dtoList.add(new EmailAccountSettingDTO(emailAccountSetting));
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
    public String getAddReceiveAccount(Model model) {
        ReceiveAccountForm receiveAccountForm = new ReceiveAccountForm();
        receiveAccountForm.setMailServerPort(993);
        model.addAttribute("receiveAccountForm", receiveAccountForm);
        model.addAttribute("api", "/admin/addReceiveAccount");
        return "admin/settings/account/form";
    }

    @RequestMapping(value = { "/addReceiveAccount" }, method = RequestMethod.POST)
    public String saveReceiveAccount(
            Model model,
            @ModelAttribute("receiveAccountForm") ReceiveAccountForm receiveAccountForm) {
        EmailAccountSetting newAccount = new EmailAccountSetting(receiveAccountForm, false);
        accountsSettingsService.save(newAccount);
        return "redirect:/admin/mailAccountSettings";
    }

    @RequestMapping(value = "/mailAccountSettings/update", method = RequestMethod.GET)
    public String getReceiveAccount(@RequestParam(value = "id", required = true) long id, Model model) {
        List<EmailAccountSetting> listAccount = accountsSettingsService.findById(id);
        if(listAccount.isEmpty()){
            //TODO: account not found error
        }
        EmailAccountSetting account = listAccount.get(0);
        ReceiveAccountForm receiveAccountForm = new ReceiveAccountForm(account);
        model.addAttribute("receiveAccountForm", receiveAccountForm);
        model.addAttribute("api", "/admin/updateReceiveAccount/" + id);
        return "admin/settings/account/form";
    }

    @RequestMapping(value = "/updateReceiveAccount/{id}", method = RequestMethod.POST)
    public String updateReceiveAccount(@PathVariable("id") long id, Model model, @ModelAttribute("receiveAccountForm") ReceiveAccountForm receiveAccountForm) {
        List<EmailAccountSetting> listAccount = accountsSettingsService.findById(id);
        if(listAccount.isEmpty()){
            //TODO: account not found error
        }
        EmailAccountSetting newAccount = new EmailAccountSetting(receiveAccountForm, true);
        newAccount.setId(id);
        accountsSettingsService.save(newAccount);
        return "redirect:/admin/mailAccountSettings";
    }

    @RequestMapping(value = "/mailAccountSettings/addSend", method = RequestMethod.GET)
    public String getAddSendAccount(Model model) {
        SendAccountForm sendAccountForm = new SendAccountForm();
        sendAccountForm.setMailServerPort(25);
        model.addAttribute("sendAccountForm", sendAccountForm);
        model.addAttribute("api", "/admin/addSendAccount");
        return "admin/settings/account/sendForm";
    }

    @RequestMapping(value = { "/addSendAccount" }, method = RequestMethod.POST)
    public String saveSendAccount(
            Model model,
            @ModelAttribute("sendAccountForm") SendAccountForm sendAccountForm) {
        EmailAccountSetting newAccount = new EmailAccountSetting(sendAccountForm, false);
        accountsSettingsService.save(newAccount);
        return "redirect:/admin/mailAccountSettings";
    }

    @RequestMapping(value = "/mailAccountSettings/updateSend", method = RequestMethod.GET)
    public String getSendAccount(@RequestParam(value = "id", required = true) long id, Model model) {
        List<EmailAccountSetting> listAccount = accountsSettingsService.findById(id);
        if(listAccount.isEmpty()){
            //TODO: account not found error
        }
        EmailAccountSetting account = listAccount.get(0);
        SendAccountForm sendAccountForm = new SendAccountForm(account);
        model.addAttribute("sendAccountForm", sendAccountForm);
        model.addAttribute("api", "/admin/updateSendAccount/" + id);
        return "admin/settings/account/sendForm";
    }

    @RequestMapping(value = "/updateSendAccount/{id}", method = RequestMethod.POST)
    public String updateSendAccount(@PathVariable("id") long id, Model model, @ModelAttribute("sendAccountForm") SendAccountForm sendAccountForm) {
        List<EmailAccountSetting> listAccount = accountsSettingsService.findById(id);
        if(listAccount.isEmpty()){
            //TODO: account not found error
        }
        EmailAccountSetting newAccount = new EmailAccountSetting(sendAccountForm, true);
        newAccount.setId(id);
        accountsSettingsService.save(newAccount);
        return "redirect:/admin/mailAccountSettings";
    }

    @RequestMapping("/receiveRuleSettings")
    public String receiveRuleSettings() {
        return "admin/settings/receive_mail_rule_settings";
    }
}
