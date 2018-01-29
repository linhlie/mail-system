package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.EnviromentSettingForm;
import io.owslab.mailreceiver.form.ReceiveAccountForm;
import io.owslab.mailreceiver.model.EnviromentSetting;
import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.settings.ReceiveMailAccountsSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SettingsController {

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
    public String receiveSettings(Model model) {
        model.addAttribute("list", accountsSettingsService.list());
        ReceiveAccountForm receiveAccountForm = new ReceiveAccountForm();
        model.addAttribute("receiveAccountForm", receiveAccountForm);
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
