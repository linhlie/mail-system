package io.owslab.mailreceiver.service.bulletin;

import io.owslab.mailreceiver.dao.BulletinPermissionDAO;
import io.owslab.mailreceiver.dto.BulletinPermissionDTO;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.BulletinBoard;
import io.owslab.mailreceiver.model.BulletinPermission;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BulletinPermissionService {

    @Autowired
    BulletinPermissionDAO bulletinPermissionDAO;

    @Autowired
    AccountService accountService;

    public void saveBulletinPermission(BulletinPermission bulletinPermission){
        bulletinPermissionDAO.save(bulletinPermission);
    }

    public void saveListBulletinPermission(List<BulletinPermission> bulletinPermissions){
        bulletinPermissionDAO.save(bulletinPermissions);
    }

    public void createBulletinPermissions(BulletinBoard bulletinBoard){
        List<Account> accounts = accountService.getAllUserRoleAccounts();
        List<BulletinPermission> bulletinPermissions = new ArrayList<>();
        for(Account acc : accounts){
            bulletinPermissions.add(new BulletinPermission(acc.getId(), bulletinBoard.getId()));
        }
        saveListBulletinPermission(bulletinPermissions);
    }

    public List<BulletinPermissionDTO> getBulletinPermissions(long bulletinBoardId){
        List<BulletinPermission> bulletinPermissionList =  bulletinPermissionDAO.findByBulletinBoardId(bulletinBoardId);
        List<BulletinPermissionDTO> result = new ArrayList<>();
        for(BulletinPermission permission : bulletinPermissionList){
            Account account = accountService.findById(permission.getAccountId());
            result.add(new BulletinPermissionDTO(permission, account.getAccountName()));
        }
        return result;
    }
}
