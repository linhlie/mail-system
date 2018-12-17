package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.BulletinPermission;

public class BulletinPermissionDTO {
    private long id;
    private long accountId;
    private String accountName;
    private long bulletinBoardId;
    private boolean canView;
    private boolean canEdit;
    private boolean canDelete;
    private boolean canChangePermission;

    public BulletinPermissionDTO(){

    }

    public BulletinPermissionDTO(BulletinPermission bulletinPermission, String accountName){
        this.id = bulletinPermission.getId();
        this.accountId = bulletinPermission.getAccountId();
        this.accountName = accountName;
        this.bulletinBoardId = bulletinPermission.getBulletinBoardId();
        this.canView = bulletinPermission.isCanView();
        this.canEdit = bulletinPermission.isCanEdit();
        this.canDelete = bulletinPermission.isCanDelete();
        this.canChangePermission = bulletinPermission.isCanChangePermission();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public long getBulletinBoardId() {
        return bulletinBoardId;
    }

    public void setBulletinBoardId(long bulletinBoardId) {
        this.bulletinBoardId = bulletinBoardId;
    }

    public boolean isCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isCanChangePermission() { return canChangePermission; }

    public void setCanChangePermission(boolean canChangePermission) { this.canChangePermission = canChangePermission; }
}
