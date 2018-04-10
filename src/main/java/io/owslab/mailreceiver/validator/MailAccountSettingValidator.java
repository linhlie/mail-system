package io.owslab.mailreceiver.validator;

import io.owslab.mailreceiver.form.FullAccountForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class MailAccountSettingValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == FullAccountForm.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        FullAccountForm fullAccountForm = (FullAccountForm) target;
        //TODO: check connect info account settings
        errors.rejectValue("rUserName", "Authentication.fullAccountForm.rUserName");
        errors.rejectValue("sUserName", "Authentication.fullAccountForm.sUserName");
    }
}
