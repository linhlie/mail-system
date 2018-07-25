package io.owslab.mailreceiver.validator;

import io.owslab.mailreceiver.dao.AccountDAO;
import io.owslab.mailreceiver.form.AdministratorSettingForm;
import io.owslab.mailreceiver.form.UserAccountForm;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * Created by khanhlvb on 7/4/18.
 */
@Component
@PropertySource(value = "classpath:validation.properties", encoding = "UTF-8")
public class UserAccountValidator implements Validator {
    @Autowired
    private AccountDAO accountDAO;

    @Value("${NotEmpty.userAccountForm.userName}")
    private String userAccountRequired;

    @Value("${NotEmpty.userAccountForm.name}")
    private String userNameRequired;

    @Value("${NotEmpty.userAccountForm.newPassword}")
    private String newPasswordRequired;

    @Value("${NotEmpty.userAccountForm.confirmNewPassword}")
    private String confirmNewPasswordRequired;

    @Value("${Match.userAccountForm.confirmNewPassword}")
    private String confirmNewPasswordNotMatch;

    @Value("${Account.userAccountForm.notFound}")
    private String accountNotFound;

    @Value("${Duplicate.userAccountForm.userName}")
    private String duplicateAccount;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == UserAccountForm.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserAccountForm userAccountForm = (UserAccountForm) target;
        boolean isUpdate = isUpdate(userAccountForm);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "NotEmpty.userAccountForm.userName", userAccountRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty.userAccountForm.name", userNameRequired);
        if(!isUpdate) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "NotEmpty.userAccountForm.newPassword", newPasswordRequired);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmNewPassword", "NotEmpty.userAccountForm.confirmNewPassword", confirmNewPasswordRequired);
        }

        if (!errors.hasErrors()) {
            if (!userAccountForm.getNewPassword().equals(userAccountForm.getConfirmNewPassword())) {
                errors.rejectValue("confirmNewPassword", "Match.userAccountForm.confirmNewPassword", null, confirmNewPasswordNotMatch);
            }
        }
        if (!errors.hasErrors()) {
            List<Account> userByName = accountDAO.findByUserName(userAccountForm.getUserName());
            if(isUpdate){
                long userAccountId = Long.parseLong(userAccountForm.getId());
                Account currentUserAccount = accountDAO.findOne(userAccountId);
                if(currentUserAccount == null) {
                    errors.rejectValue("userName", "Account.userAccountForm.notFound", null, accountNotFound);
                } else {
                    if(userByName.size() > 0) {
                        if(userAccountId != (userByName.get(0).getId())) {
                            errors.rejectValue("userName", "Duplicate.userAccountForm.userName", null, duplicateAccount);
                        }
                    }
                }
            } else {
                if(userByName.size() > 0) {
                    errors.rejectValue("userName", "Duplicate.userAccountForm.userName", null, duplicateAccount);
                }
            }
        }
    }

    private boolean isUpdate(UserAccountForm form) {
        return form != null && form.getId() != null;
    }
}
