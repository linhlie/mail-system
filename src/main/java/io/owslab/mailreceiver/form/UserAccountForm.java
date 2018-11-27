package io.owslab.mailreceiver.form;

/**
 * Created by khanhlvb on 7/4/18.
 */
public class UserAccountForm {
    private String id;
    private String userName;
    private String lastName;
    private String firstName;
    private String newPassword;
    private String confirmNewPassword;
    private boolean expansion;

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
