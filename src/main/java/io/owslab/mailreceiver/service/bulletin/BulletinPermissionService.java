package io.owslab.mailreceiver.service.bulletin;

import io.owslab.mailreceiver.dao.BulletinPermissionDAO;
import io.owslab.mailreceiver.dto.BulletinPermissionDTO;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.BulletinBoard;
import io.owslab.mailreceiver.model.BulletinPermission;
import io.owslab.mailreceiver.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Permission;
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

    public List<BulletinPermission> getBulletinPermissionsByAccountIdAndCanView(long accountId){
        return bulletinPermissionDAO.findByAccountIdAndCanView(accountId, true);
    }

    public List<BulletinPermission> getBulletinPermissionsByBulletinBoardId(long bulletinId){
        return bulletinPermissionDAO.findByBulletinBoardId(bulletinId);
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
        Long accountLoggedId = accountService.getLoggedInAccountId();
        for(BulletinPermission permission : bulletinPermissionList){
            if(accountLoggedId != permission.getAccountId()){
                Account account = accountService.findById(permission.getAccountId());
                result.add(new BulletinPermissionDTO(permission, account.getAccountName()));
            }
        }
        return result;
    }

    public void changeBulletinPermission(List<BulletinPermissionDTO> bulletinPermissionDTOs) {
        for(BulletinPermissionDTO permissionDTO : bulletinPermissionDTOs){
            BulletinPermission permission = new BulletinPermission(permissionDTO);
            saveBulletinPermission(permission);
        }
    }

    public void createPermissionForNewAccount(Account userSaved) {
        List<Long> listId = bulletinPermissionDAO.getBulletinBoardId();
        for(int i=0; i<listId.size(); i++){
            String idBulletin = listId.get(i)+"";
            long id = Long.parseLong(idBulletin);
            List<BulletinPermission> listPermission = new ArrayList<>();
            try {
                listPermission = getBulletinPermissionsByBulletinBoardId(id);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(listPermission.size() > 0){
                boolean isSave = true;
                for(BulletinPermission permission : listPermission){
                    if(!permission.isCanView() || !permission.isCanEdit() || !permission.isCanDelete()){
                        isSave = false;
                        break;
                    }
                }
                if(isSave){
                    BulletinPermission newPermission = new BulletinPermission(userSaved.getId(), id);
                    saveBulletinPermission(newPermission);
                }else{
                    BulletinPermission newPermission = new BulletinPermission(userSaved.getId(), id, false);
                    saveBulletinPermission(newPermission);
                }
            }
        }
    }

    public boolean checkPermissionEdit(long bulletinBoardId) {
        long accountId = accountService.getLoggedInAccountId();
        if(accountId<=0){
            return false;
        }
        BulletinPermission bulletinPermission = bulletinPermissionDAO.findByAccountIdAndBulletinBoardId(accountId, bulletinBoardId);
        if(bulletinPermission == null){
            return false;
        }
        return bulletinPermission.isCanEdit();
    }

    public boolean checkPermissionDelete(long bulletinBoardId) {
        long accountId = accountService.getLoggedInAccountId();
        if(accountId<=0){
            return false;
        }
        BulletinPermission bulletinPermission = bulletinPermissionDAO.findByAccountIdAndBulletinBoardId(accountId, bulletinBoardId);
        if(bulletinPermission == null){
            return false;
        }
        return bulletinPermission.isCanDelete();
    }
}
