package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.AdministratorSettingForm;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.validator.AdministratorSettingValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by khanhlvb on 2/28/18.
 */
@Controller
@RequestMapping("/admin/")
public class AdministratorSettingController {

    private static final Logger logger = LoggerFactory.getLogger(AdministratorSettingController.class);
    @Autowired
    private AdministratorSettingValidator administratorSettingValidator;

    @Autowired
    private AccountService accountService;

    // Set a form validator
    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }

        if (target.getClass() == AdministratorSettingForm.class) {
            dataBinder.setValidator(administratorSettingValidator);
        }
    }

    @RequestMapping(value = { "/administratorSetting" }, method = RequestMethod.GET)
    public String index(Model model) {
        AdministratorSettingForm administratorSettingForm = new AdministratorSettingForm();
        model.addAttribute("administratorSettingForm", administratorSettingForm);
        model.addAttribute("saved", false);
        return "admin/setting";
    }

    @RequestMapping(value = "/administratorSetting", method = RequestMethod.POST)
    public String saveRegister(Model model, //
                               @ModelAttribute("administratorSettingForm") @Validated AdministratorSettingForm administratorSettingForm, //
                               BindingResult result) {

        // Validate result
        if (result.hasErrors()) {
            System.out.println("has Error");
            return "admin/setting";
        }
        try {
            accountService.updateAdmin(administratorSettingForm);
            System.out.println("Update admin");
            model.addAttribute("saved", true);
        }
        // Other error!!
        catch (Exception e) {
            logger.error(e.getMessage());
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "admin/setting";
        }

        return "admin/setting";
    }
}
