package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.RegisterAccountForm;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.validator.RegisterAccountValidator;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by khanhlvb on 3/1/18.
 */
@Controller
public class RegisterController {

    @Autowired
    private RegisterAccountValidator registerAccountValidator;

    @Autowired
    private AccountService accountService;

    // Set a form validator
    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }

        if (target.getClass() == RegisterAccountForm.class) {
            dataBinder.setValidator(registerAccountValidator);
        }
    }

    @RequestMapping(value = { "/register" }, method = RequestMethod.GET)
    public String index(Model model) {
        RegisterAccountForm registerAccountForm = new RegisterAccountForm();

        model.addAttribute("registerAccountForm", registerAccountForm);
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String saveRegister(Model model, //
                               @ModelAttribute("registerAccountForm") @Validated RegisterAccountForm registerAccountForm, //
                               BindingResult result, //
                               final RedirectAttributes redirectAttributes) {

        // Validate result
        if (result.hasErrors()) {
            return "register";
        }
        Account newAccount = null;
        try {
            newAccount = accountService.createNewAccount(registerAccountForm);
        }
        // Other error!!
        catch (Exception e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "register";
        }

        redirectAttributes.addFlashAttribute("flashUser", newAccount);

        return "redirect:/";
    }
}
