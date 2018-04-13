package io.owslab.mailreceiver.form;

/**
 * Created by khanhlvb on 3/14/18.
 */
public class AdministratorSettingForm {
    private String userName;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;

    public AdministratorSettingForm() {
    }

    public AdministratorSettingForm(String userName, String currentPassword, String newPassword, String confirmNewPassword) {
        this.userName = userName;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
