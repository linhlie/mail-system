package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.ReceiveAccountForm;
import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;
import io.owslab.mailreceiver.service.settings.ReceiveMailAccountsSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SettingsController {

    @Autowired
    private ReceiveMailAccountsSettingsService accountsSettingsService;

    @RequestMapping("/enviromentSettings")
    public String enviromentSettings() {
        return "settings/enviroment_settings";
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
        return "settings/receive/form";
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
