package io.owslab.mailreceiver.model;

import javax.persistence.*;

@Entity
@Table(name = "bulletin_permission")
public class BulletinPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "bulletin_board_id", referencedColumnName = "id", nullable = false)
    private BulletinBoard bulletinBoard;

    private int permission;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BulletinBoard getBulletinBoard() {
        return bulletinBoard;
    }

    public void setBulletinBoard(BulletinBoard bulletinBoard) {
        this.bulletinBoard = bulletinBoard;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public static class Permission {
        public static final int BLOCK = 1;
        public static final int CAN_VIEW = 2;
        public static final int CAN_EDIT = 3;
    }
}
