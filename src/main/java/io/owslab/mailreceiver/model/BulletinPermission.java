package io.owslab.mailreceiver.model;

import io.owslab.mailreceiver.dto.BulletinPermissionDTO;

import javax.persistence.*;

@Entity
@Table(name = "bulletin_permission")
public class BulletinPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long accountId;
    private long bulletinBoardId;
    private boolean canView;
    private boolean canEdit;
    private boolean canDelete;
    private boolean canChangePermission;

    public BulletinPermission(){

    }

    public BulletinPermission(long accountId, long bulletinBoardId, boolean canView, boolean canEdit, boolean canDelete, boolean canChangePermission){
        this.accountId = accountId;
        this.bulletinBoardId = bulletinBoardId;
        this.canView = canView;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.canChangePermission = canChangePermission;
    }

    public BulletinPermission(BulletinPermissionDTO permissionDTO) {
        this.id = permissionDTO.getId();
        this.accountId = permissionDTO.getAccountId();
        this.bulletinBoardId = permissionDTO.getBulletinBoardId();
        this.canView = permissionDTO.isCanView();
        this.canEdit = permissionDTO.isCanEdit();
        this.canDelete = permissionDTO.isCanDelete();
        this.canChangePermission = permissionDTO.isCanChangePermission();
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
