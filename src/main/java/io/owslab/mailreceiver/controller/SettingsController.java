package io.owslab.mailreceiver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SettingsController {

    @RequestMapping("/enviromentSettings")
    public String enviromentSettings() {
        return "settings/enviroment_settings";
    }

    @RequestMapping("/receiveSettings")
    public String receiveSettings() {
        return "settings/receive_mail_accounts_settings";
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
