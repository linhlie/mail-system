package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.dto.BulletinBoardDTO;

public class BulletinBoardForm {
     BulletinBoardDTO bulletionBoard;
     int position;

    public BulletinBoardDTO getBulletionBoard() {
        return bulletionBoard;
    }

    public void setBulletionBoard(BulletinBoardDTO bulletionBoard) {
        this.bulletionBoard = bulletionBoard;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
