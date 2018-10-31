package io.owslab.mailreceiver.service.bulletin;

import io.owslab.mailreceiver.dao.BulletinBoardDAO;
import io.owslab.mailreceiver.dto.BulletinBoardDTO;
import io.owslab.mailreceiver.form.BulletinBoardForm;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.BulletinBoard;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BulletinBoardService {

    @Autowired
    BulletinBoardDAO bulletinBoardDAO;

    @Autowired
    private AccountService accountService;

    public void saveBulletinBoard(BulletinBoardDTO bulletin){
        long accountId = accountService.getLoggedInAccountId();
        if(accountId == 0){
            return;
        }
        BulletinBoard bulletinBoard = new BulletinBoard();
        if(bulletin.getId() != null && bulletin.getId()>0){
            bulletinBoard.setId(bulletin.getId());
        }
        if(bulletin.getBulletin()!=null){
            bulletinBoard.setBulletin(bulletin.getBulletin());
        }else{
            bulletinBoard.setBulletin("");
        }
        bulletinBoard.setAccountId(accountId);
        bulletinBoard.setTimeEdit(new Date());
        bulletinBoard.setTabName(bulletin.getTabName());
        if(bulletin.getTabNumber() == 0){
            List<BulletinBoard> bulletinBoards = findTabsBulletin(0);
            bulletinBoard.setTabNumber(bulletinBoards.size() + 1);
        }else{
            bulletinBoard.setTabNumber(bulletin.getTabNumber());
        }
        bulletinBoardDAO.save(bulletinBoard);
    }

    //need one query
    public void updateBulletinPosition(BulletinBoardForm form){
        BulletinBoardDTO bulletinBoardDTO = form.getBulletionBoard();
        BulletinBoard bulletinBoard = bulletinBoardDAO.findOne(bulletinBoardDTO.getId());
        if(bulletinBoard ==null || bulletinBoard.getTabNumber() != bulletinBoardDTO.getTabNumber()){
            return;
        }else{
            long start = bulletinBoard.getTabNumber();
            long end = form.getPosition() + 1;
            if(start>end){
                bulletinBoard.setTabNumber(end);
                bulletinBoardDAO.upPosition(end,start);
                bulletinBoardDAO.save(bulletinBoard);
            }else{
                bulletinBoard.setTabNumber(end);
                bulletinBoardDAO.downPosition(start,end);
                bulletinBoardDAO.save(bulletinBoard);
            }
        }
    }

    public BulletinBoard findTopBulletin(){
        return bulletinBoardDAO.getBulletinBoardTop();
    }

    public List<BulletinBoard> findTabsBulletin(long number){
        return bulletinBoardDAO.findByTabNumberGreaterThan(number);
    }

    public List<BulletinBoardDTO> getBulletinBoard(){
        List<BulletinBoard> bulletinBoards = findTabsBulletin(0);
        sortBulletinBoard(bulletinBoards);
        List<BulletinBoardDTO> bulletinBoardDTOs = new ArrayList<>();
        if(bulletinBoards == null){
            return bulletinBoardDTOs;
        }
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        for(BulletinBoard bulletin : bulletinBoards){
            BulletinBoardDTO bulletinBoardDTO = new BulletinBoardDTO();
            bulletinBoardDTO.setId(bulletin.getId());
            bulletinBoardDTO.setBulletin(bulletin.getBulletin());
            String date = df.format(bulletin.getTimeEdit());
            bulletinBoardDTO.setTimeEdit(date);
            Account account = accountService.findById(bulletin.getAccountId());
            bulletinBoardDTO.setUsername("");
            if(account!=null){
                bulletinBoardDTO.setUsername(account.getUserName());
            }
            if(account!=null && account.getName()!=null && !account.getName().equalsIgnoreCase("")){
                bulletinBoardDTO.setUsername(account.getName());
            }
            bulletinBoardDTO.setTabName(bulletin.getTabName());
            bulletinBoardDTO.setTabNumber(bulletin.getTabNumber());
            bulletinBoardDTOs.add(bulletinBoardDTO);
        }
        return bulletinBoardDTOs;
    }

    //need transaction
    public void deleteTabNumber(long id) {
        BulletinBoard bulletin = bulletinBoardDAO.findOne(id);
        if(bulletin!=null){
            long oldTabNumber = bulletin.getTabNumber();
            bulletinBoardDAO.delete(bulletin);
            if(oldTabNumber>0){
                bulletinBoardDAO.downTabNumber(oldTabNumber);
            }
        }
    }

    public void sortBulletinBoard(List<BulletinBoard> list){
        Collections.sort(list, new Comparator<BulletinBoard>() {
            @Override
            public int compare(BulletinBoard o1, BulletinBoard o2) {
                return (int) (o1.getTabNumber()-o2.getTabNumber());
            }
        });
    }
}