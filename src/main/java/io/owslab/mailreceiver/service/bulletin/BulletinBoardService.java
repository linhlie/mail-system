package io.owslab.mailreceiver.service.bulletin;

import io.owslab.mailreceiver.dao.BulletinBoardDAO;
import io.owslab.mailreceiver.dto.BulletinBoardDTO;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.BulletinBoard;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@Service
public class BulletinBoardService {

    @Autowired
    BulletinBoardDAO bulletinBoardDAO;

    @Autowired
    private AccountService accountService;

    public void saveBulletinBoard(String bulletin){
        long accountId = accountService.getLoggedInAccountId();
        if(accountId == 0){
            return;
        }
        BulletinBoard bulletinBoard = new BulletinBoard(bulletin, accountId);
        bulletinBoardDAO.save(bulletinBoard);
    }

    public BulletinBoard findTopBulletin(){
        return bulletinBoardDAO.getBulletinBoardTop();
    }

    public BulletinBoardDTO getBulletinBoard(){
        BulletinBoard bulletinBoard = findTopBulletin();
        BulletinBoardDTO bulletinBoardDTO = new BulletinBoardDTO();
        if(bulletinBoard.getBulletin()==null){
            bulletinBoardDTO.setBulletin("");
        }else{
            bulletinBoardDTO.setBulletin(bulletinBoard.getBulletin());
        }
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String date = df.format(bulletinBoard.getTimeEdit());
        bulletinBoardDTO.setTimeEdit(date);
        Account account = accountService.findById(bulletinBoard.getAccountId());
        bulletinBoardDTO.setUsername("");
        if(account!=null){
            bulletinBoardDTO.setUsername(account.getUserName());
        }
        if(account!=null && account.getName()!=null && !account.getName().equalsIgnoreCase("")){
            bulletinBoardDTO.setUsername(account.getName());
        }
        return bulletinBoardDTO;
    }

}