package io.owslab.mailreceiver.validator;

import io.owslab.mailreceiver.dao.AccountDAO;
import io.owslab.mailreceiver.form.AdministratorSettingForm;
import io.owslab.mailreceiver.form.RegisterAccountForm;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * Created by khanhlvb on 3/14/18.
 */
@Component
public class AdministratorSettingValidator implements Validator {
    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == AdministratorSettingForm.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        AdministratorSettingForm administratorSettingForm = (AdministratorSettingForm) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "NotEmpty.administratorSettingForm.userName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentPassword", "NotEmpty.administratorSettingForm.currentPassword");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "NotEmpty.administratorSettingForm.newPassword");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmNewPassword", "NotEmpty.administratorSettingForm.confirmNewPassword");

        List<Account> adminList = accountDAO.findByUserRole(Account.Role.ADMIN);
        if (!errors.hasErrors()) {
            if (!administratorSettingForm.getNewPassword().equals(administratorSettingForm.getConfirmNewPassword())) {
                errors.rejectValue("confirmNewPassword", "Match.registerAccountForm.confirmNewPassword");
            }
        }
        if (!errors.hasErrors()) {
            if(adminList.size() == 0) {
                errors.rejectValue("userName", "Account.administratorSettingForm.notFoundAdmin");
            } else {
                Account admin = adminList.get(0);
                if(!admin.getUserName().equals(administratorSettingForm.getUserName())){
                    Account dbAccount = accountService.findOne(administratorSettingForm.getUserName());
                    if (dbAccount != null) {
                        errors.rejectValue("userName", "Duplicate.administratorSettingForm.userName");
                    }
                }
            }
        }
        if (!errors.hasErrors()) {
            if(adminList.size() == 0){
                errors.rejectValue("currentPassword", "Incorrect.administratorSettingForm.currentPassword");
            } else {
                Account admin = adminList.get(0);
                if(!passwordEncoder.matches(administratorSettingForm.getCurrentPassword(), admin.getEncryptedPassword())) {
                    errors.rejectValue("currentPassword", "Incorrect.administratorSettingForm.currentPassword");
                }
            }
        }
    }
}
