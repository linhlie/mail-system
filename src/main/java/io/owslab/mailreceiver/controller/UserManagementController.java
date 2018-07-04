package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.AccountDTO;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.security.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 7/3/18.
 */
@Controller
@RequestMapping("/admin/")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = { "/userManagement" }, method = RequestMethod.GET)
    public String index(Model model) {
        return "admin/userManagement";
    }

    @GetMapping("/userManagementData")
    @ResponseBody
    public ResponseEntity<?> getUserManagementData(Model model) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<Account> accounts = accountService.getAllUserRoleAccounts();
            List<AccountDTO> accountDTOList = new ArrayList<>();
            for(Account account : accounts) {
                accountDTOList.add(new AccountDTO(account));
            }
            result.setMsg("done");
            result.setStatus(true);
            result.setList(accountDTOList);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("getUserManagementData: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = { "/deleteUser" }, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> deleteUser(
            Model model,
            @RequestParam(value = "id") long id) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            accountService.delete(id);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("deleteUser: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }
}
