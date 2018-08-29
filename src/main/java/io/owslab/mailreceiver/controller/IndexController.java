package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.AccountDTO;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.response.DashboardResponseBody;
import io.owslab.mailreceiver.service.errror.ReportErrorService;
import io.owslab.mailreceiver.service.mail.FetchMailsService;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.matching.MatchingConditionService;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.service.statistics.ClickHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private MailBoxService mailBoxService;

    @Autowired
    private MailAccountsService mailAccountsService;

    @Autowired
    private FetchMailsService fetchMailsService;

    @Autowired
    private MatchingConditionService matchingConditionService;

    @Autowired
    private ClickHistoryService clickHistoryService;

    @Autowired
    private AccountService accountService;

    private DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @RequestMapping("/default")

    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin/";
        }
        return "redirect:/";
    }

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin/";
        }
        long numberOfMessage = mailBoxService.count();
        model.addAttribute("numberOfMessage", numberOfMessage);
        int matchingCount = matchingConditionService.getMatchingCount();
        model.addAttribute("matchingCount", matchingCount);
        return "index";
    }

    @RequestMapping(value="/user/dashboard/userStatistics", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getUserStatistics (@RequestParam(value = "accountId", required = false) String accountId){
        DashboardResponseBody responseBody = new DashboardResponseBody();
        try {
            Date now = new Date();
            List<String> clickCount = clickHistoryService.getClickCount(now, accountId);
            List<String> sendPerClick = clickHistoryService.getTotalSentStats(now, accountId);
            List<Account> accounts = accountService.getAllUserRoleAccounts();
            List<AccountDTO> accountDTOList = new ArrayList<>();
            for(Account account : accounts) {
                accountDTOList.add(new AccountDTO(account));
            }
            responseBody.setUsers(accountDTOList);
            responseBody.setClickCount(clickCount);
            responseBody.setSendPerClick(sendPerClick);
            responseBody.setMsg("done");
            responseBody.setStatus(true);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            logger.error("getStatistics: " + e.getMessage());
            responseBody.setMsg(e.getMessage());
            responseBody.setStatus(false);
            return ResponseEntity.ok(responseBody);
        }
    }

    @RequestMapping(value="/user/dashboard/mailStatistics", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getMailStatistics (@RequestParam(value = "accountId", required = false) String accountId){
        DashboardResponseBody responseBody = new DashboardResponseBody();
        try {
            Date now = new Date();
            String latestReceive = mailBoxService.getLatestReceive(accountId);
            List<String> receiveMailNumber = mailBoxService.getReceiveMailNumberStats(now, accountId);
            List<EmailAccount> emailAccounts = mailAccountsService.list();
            responseBody.setHasSystemError(ReportErrorService.hasSystemError());
            responseBody.setLatestReceive(latestReceive);
            responseBody.setReceiveMailNumber(receiveMailNumber);
            responseBody.setEmailAccounts(emailAccounts);
            responseBody.setMsg("done");
            responseBody.setStatus(true);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            logger.error("getStatistics: " + e.getMessage());
            responseBody.setMsg(e.getMessage());
            responseBody.setStatus(false);
            return ResponseEntity.ok(responseBody);
        }
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        long numberOfMessage = mailBoxService.count();
        model.addAttribute("numberOfMessage", numberOfMessage);
        FetchMailsService.FetchMailProgress mailProgress = fetchMailsService.getTotalFetchMailProgress();
        model.addAttribute("mailProgressRemain", mailProgress.getTotal() - mailProgress.getDone());
        model.addAttribute("mailProgressLastUpdate", df.format(new Date()));
        return "admin";
    }

    @EventListener
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }

}
