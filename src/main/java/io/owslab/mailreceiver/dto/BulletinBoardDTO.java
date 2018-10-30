package io.owslab.mailreceiver.dto;

public class BulletinBoardDTO {
    private Long id;
    private String bulletin;
    private String timeEdit;
    private String username;
    private String tabName;
    private long tabNumber;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getBulletin() {
        return bulletin;
    }

    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }

    public String getTimeEdit() {
        return timeEdit;
    }

    public void setTimeEdit(String timeEdit) {
        this.timeEdit = timeEdit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTabName() { return tabName; }

    public void setTabName(String tabName) { this.tabName = tabName; }

    public long getTabNumber() { return tabNumber; }

    public void setTabNumber(long tabNumber) { this.tabNumber = tabNumber; }
}
