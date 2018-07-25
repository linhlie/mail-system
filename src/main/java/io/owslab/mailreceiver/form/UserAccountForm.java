package io.owslab.mailreceiver.form;

/**
 * Created by khanhlvb on 7/4/18.
 */
public class UserAccountForm {
    private String id;
    private String userName;
    private String name;
    private String newPassword;
    private String confirmNewPassword;
    private boolean expansion;

    public UserAccountForm() {
    }

    public UserAccountForm(String userName, String name, String newPassword, String confirmNewPassword, boolean expansion) {
        this.userName = userName;
        this.name = name;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
        this.expansion = expansion;
    }

    public UserAccountForm(String id, String userName, String name, String newPassword, String confirmNewPassword, boolean expansion) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
        this.expansion = expansion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isExpansion() {
        return expansion;
    }

    public void setExpansion(boolean expansion) {
        this.expansion = expansion;
    }
}
