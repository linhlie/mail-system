package io.owslab.mailreceiver.form;

/**
 * Created by khanhlvb on 3/1/18.
 */
public class RegisterAccountForm {

    private String userName;
    private String password;
    private String confirmPassword;

    public RegisterAccountForm() {
    }

    public RegisterAccountForm(String userName, String password, String confirmPassword) {
        this.userName = userName;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
