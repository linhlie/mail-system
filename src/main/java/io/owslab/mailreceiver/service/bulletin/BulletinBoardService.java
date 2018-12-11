package io.owslab.mailreceiver.service.bulletin;

import io.owslab.mailreceiver.dao.BulletinBoardDAO;
import io.owslab.mailreceiver.dto.BulletinBoardDTO;
import io.owslab.mailreceiver.dto.BulletinPermissionDTO;
import io.owslab.mailreceiver.form.BulletinBoardForm;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.BulletinBoard;
import io.owslab.mailreceiver.model.BulletinPermission;
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
    BulletinPermissionService bulletinPermissionService;

    @Autowired
    private AccountService accountService;

    // need transaction
    public void saveBulletinBoard(BulletinBoardDTO bulletin){
        long accountId = accountService.getLoggedInAccountId();
        if(accountId <= 0){
            return;
        }
        BulletinBoard bulletinBoard = null;
        if(bulletin.getId() == null){
            List<BulletinBoard> bulletinBoards = findTabsBulletin(0);
            bulletinBoard = new BulletinBoard(bulletin, accountId, bulletinBoards);
        }else{
            bulletinBoard = bulletinBoardDAO.findOne(bulletin.getId());
            bulletinBoard.setTabName(bulletin.getTabName());
            bulletinBoard.setBulletin(bulletin.getBulletin());
            bulletinBoard.setTimeEdit(new Date());
            bulletinBoard.setAccountId(accountId);

        }
        if(bulletinBoard == null) return;
        BulletinBoard bulletinBoardSaved = bulletinBoardDAO.save(bulletinBoard);
        if(bulletin.getId() == null &&  bulletinBoardSaved != null){
            bulletinPermissionService.createBulletinPermissions(bulletinBoardSaved);
        }
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
        long accountLoggedId = accountService.getLoggedInAccountId();
        List<BulletinPermission> listPermission = bulletinPermissionService.getBulletinPermissionsByAccountIdAndCanView(accountLoggedId);
        List<Long> listId = new ArrayList<>();
        for(BulletinPermission permission :listPermission){
            listId.add(permission.getBulletinBoardId());
        }
        List<BulletinBoard> bulletinBoards = bulletinBoardDAO.findByIdIn(listId);
        sortBulletinBoard(bulletinBoards);
        List<BulletinBoardDTO> bulletinBoardDTOs = new ArrayList<>();
        if(bulletinBoards == null){
            return bulletinBoardDTOs;
        }
        for(BulletinBoard bulletin : bulletinBoards){
            Account accountEdit = accountService.findById(bulletin.getAccountId());
            Account accountCreate = accountService.findById(bulletin.getAccountCreateId());
            BulletinBoardDTO bulletinBoardDTO = new BulletinBoardDTO(bulletin, accountEdit, accountCreate);
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