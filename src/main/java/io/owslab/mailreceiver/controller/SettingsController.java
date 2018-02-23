package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.EnviromentSettingForm;
import io.owslab.mailreceiver.form.ReceiveAccountForm;
import io.owslab.mailreceiver.model.EnviromentSetting;
import io.owslab.mailreceiver.model.FuzzyWord;
import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.settings.ReceiveMailAccountsSettingsService;
import io.owslab.mailreceiver.service.word.FuzzyWordService;
import io.owslab.mailreceiver.service.word.WordService;
import io.owslab.mailreceiver.utils.PageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class SettingsController {

    public static final int PAGE_SIZE = 5;

    @Autowired
    private ReceiveMailAccountsSettingsService accountsSettingsService;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @RequestMapping(value = "/enviromentSettings", method = RequestMethod.GET)
    public String enviromentSettings(Model model) {
        HashMap<String, String> map = enviromentSettingService.getAll();
        EnviromentSettingForm enviromentSettingForm = new EnviromentSettingForm();
        enviromentSettingForm.setMap(map);
        model.addAttribute("enviromentSettingForm", enviromentSettingForm);
        return "settings/enviroment_settings";
    }

    @RequestMapping(value = "/enviromentSettings", method = RequestMethod.POST)
    public String updateEnviromentSettings(Model model, @ModelAttribute("enviromentSettingForm") EnviromentSettingForm enviromentSettingForm) {
        Map<String, String> map = enviromentSettingForm.getMap();
        for (String key : map.keySet()) {
            enviromentSettingService.set(key, map.get(key));
        }
        return "redirect:/enviromentSettings";
    }

    @RequestMapping(value = "/receiveSettings", method = RequestMethod.GET)
    public String receiveSettings(@RequestParam(value = "page", required = false, defaultValue = "1") int page, Model model) {
        page = page - 1;
        PageRequest pageRequest = new PageRequest(page, PAGE_SIZE);
        Page<ReceiveEmailAccountSetting> pages = accountsSettingsService.list(pageRequest);
        PageWrapper<ReceiveEmailAccountSetting> pageWrapper = new PageWrapper<ReceiveEmailAccountSetting>(pages, "/receiveSettings");
        List<ReceiveEmailAccountSetting> list = pages.getContent();
        int rowsInPage = list.size();
        int fromEntry = rowsInPage == 0 ? 0 : page * PAGE_SIZE + 1;
        int toEntry = rowsInPage == 0 ? 0 : fromEntry + rowsInPage - 1;
        model.addAttribute("list", list);
        model.addAttribute("page", pageWrapper);
        model.addAttribute("fromEntry", fromEntry);
        model.addAttribute("toEntry", toEntry);
        return "settings/receive_mail_accounts_settings";
    }
    @RequestMapping(value = "/receiveSettings/add", method = RequestMethod.GET)
    public String getAddReceiveAccount(Model model) {
        ReceiveAccountForm receiveAccountForm = new ReceiveAccountForm();
        model.addAttribute("receiveAccountForm", receiveAccountForm);
        model.addAttribute("api", "/addReceiveAccount");
        return "settings/receive/form";
    }

    @RequestMapping(value = { "/addReceiveAccount" }, method = RequestMethod.POST)
    public String saveReceiveAccount(
            Model model,
            @ModelAttribute("receiveAccountForm") ReceiveAccountForm receiveAccountForm) {
        ReceiveEmailAccountSetting newAccount = new ReceiveEmailAccountSetting(receiveAccountForm, false);
        accountsSettingsService.save(newAccount);
        return "redirect:/receiveSettings";
    }

    @RequestMapping(value = "/receiveSettings/update", method = RequestMethod.GET)
    public String getReceiveAccount(@RequestParam(value = "id", required = true) long id, Model model) {
        List<ReceiveEmailAccountSetting> listAccount = accountsSettingsService.findById(id);
        if(listAccount.isEmpty()){
            //TODO: account not found error
        }
        ReceiveEmailAccountSetting account = listAccount.get(0);
        ReceiveAccountForm receiveAccountForm = new ReceiveAccountForm(account);
        model.addAttribute("receiveAccountForm", receiveAccountForm);
        model.addAttribute("api", "/updateReceiveAccount/" + id);
        return "settings/receive/form";
    }

    @RequestMapping(value = "/updateReceiveAccount/{id}", method = RequestMethod.POST)
    public String updateReceiveAccount(@PathVariable("id") long id, Model model, @ModelAttribute("receiveAccountForm") ReceiveAccountForm receiveAccountForm) {
        List<ReceiveEmailAccountSetting> listAccount = accountsSettingsService.findById(id);
        if(listAccount.isEmpty()){
            //TODO: account not found error
        }
        ReceiveEmailAccountSetting newAccount = new ReceiveEmailAccountSetting(receiveAccountForm, true);
        newAccount.setId(id);
        accountsSettingsService.save(newAccount);
        return "redirect:/receiveSettings";
    }

    @RequestMapping("/sendSettings")
    public String sendSettings() {
        return "settings/send_mail_accounts_settings";
    }

    @RequestMapping("/receiveRuleSettings")
    public String receiveRuleSettings() {
        return "settings/receive_mail_rule_settings";
    }
}
