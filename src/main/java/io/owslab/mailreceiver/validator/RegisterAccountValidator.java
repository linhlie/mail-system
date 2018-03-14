package io.owslab.mailreceiver.validator;

import io.owslab.mailreceiver.dao.AccountDAO;
import io.owslab.mailreceiver.form.RegisterAccountForm;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by khanhlvb on 3/1/18.
 */
@Component
public class RegisterAccountValidator implements Validator {
    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private AccountService accountService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == RegisterAccountForm.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterAccountForm registerAccountForm = (RegisterAccountForm) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "NotEmpty.registerAccountForm.userName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.registerAccountForm.password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotEmpty.registerAccountForm.confirmPassword");

        if (!errors.hasFieldErrors("userName")) {
            Account dbAccount = accountService.findOne(registerAccountForm.getUserName());
            if (dbAccount != null) {
                errors.rejectValue("userName", "Duplicate.registerAccountForm.userName");
            }
        }

        if (!errors.hasErrors()) {
            if (!registerAccountForm.getConfirmPassword().equals(registerAccountForm.getPassword())) {
                errors.rejectValue("confirmPassword", "Match.registerAccountForm.confirmPassword");
            }
        }
    }
}
