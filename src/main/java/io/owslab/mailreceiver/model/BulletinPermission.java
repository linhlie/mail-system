package io.owslab.mailreceiver.model;

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

    public BulletinPermission(){

    }

    public BulletinPermission(long accountId, long bulletinBoardId){
        this.accountId = accountId;
        this.bulletinBoardId = bulletinBoardId;
        this.canView = true;
        this.canEdit = true;
        this.canDelete = true;
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
}
