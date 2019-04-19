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
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.service.settings.MailReceiveRuleService;
import io.owslab.mailreceiver.service.statistics.ClickHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class StatisticController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticController.class);

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

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @Autowired
    private MailReceiveRuleService mrrs;

    private DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @RequestMapping(value = "user/statistic", method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        long numberOfMessage = mailBoxService.count();
        model.addAttribute("numberOfMessage", numberOfMessage);
        int matchingCount = matchingConditionService.getMatchingCount();
        model.addAttribute("matchingCount", matchingCount);
        return "user/statistic/statistic";
    }

    @RequestMapping(value="/user/statistic/mailStatistics", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getMailStatistics (@RequestParam(value = "accountId", required = false) String accountId){
        DashboardResponseBody responseBody = new DashboardResponseBody();
        try {
            Date now = new Date();
            String latestReceive = mailBoxService.getLatestReceive(accountId);
            List<String> receiveMailNumber = mailBoxService.getReceiveMailNumberStats(now, accountId);
            List<EmailAccount> emailAccounts = mailAccountsService.list();
            int checkMailInterval = enviromentSettingService.getCheckMailTimeInterval();
            responseBody.setHasSystemError(ReportErrorService.hasSystemError());
            responseBody.setLatestReceive(latestReceive);
            responseBody.setReceiveMailNumber(receiveMailNumber);
            responseBody.setEmailAccounts(emailAccounts);
            responseBody.setCheckMailInterval(checkMailInterval);
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

    @RequestMapping(value="/user/statistic/userStatistics", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getUserStatistics (@RequestParam(value = "accountId", required = false) String accountId){
        DashboardResponseBody responseBody = new DashboardResponseBody();
        try {
            Date now = new Date();
            List<String> clickCount = clickHistoryService.getClickCount(now, accountId);
            List<String> sendPerClick = clickHistoryService.getTotalSentStats(now, accountId);
            List<String> clickEmailMatchingEngineerCount = clickHistoryService.getClickEmailMatchingEngineerCount(now, accountId);
            List<String> sendMailEmailMatchingEngineerClick = clickHistoryService.getSendMailEmailMatchingEngineerClick(now, accountId);
            List<String> replyEmailsInboxCount = clickHistoryService.getReplyEmailsInboxCount(accountId);
            List<String> clickReplyEmailViaInboxCount = clickHistoryService.getClickReplyEmailsViaInboxCount(now, accountId);
            List<String> createSchedulerCount = clickHistoryService.getCreateSchedulerCount(now, accountId);
            List<String> sendMailSchedulerCount = clickHistoryService.getSendMailSchedulerCount(accountId);
            List<Account> accounts = accountService.getAllUserRoleAccounts();
            List<AccountDTO> accountDTOList = new ArrayList<>();
            for(Account account : accounts) {
                accountDTOList.add(new AccountDTO(account));
            }
            responseBody.setUsers(accountDTOList);
            responseBody.setClickCount(clickCount);
            responseBody.setSendPerClick(sendPerClick);
            responseBody.setClickEmailMatchingEngineerCount(clickEmailMatchingEngineerCount);
            responseBody.setSendMailEmailMatchingEngineerClick(sendMailEmailMatchingEngineerClick);
            responseBody.setReplyEmailsViaInboxCount(replyEmailsInboxCount);
            responseBody.setClickReplyEmailsViaInboxCount(clickReplyEmailViaInboxCount);
            responseBody.setCreateSchedulerCount(createSchedulerCount);
            responseBody.setSendMailSchedulerCount(sendMailSchedulerCount);
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

    @RequestMapping(value="/user/statistic/topUserSentMail", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getTopUserSentMail (){
        DashboardResponseBody responseBody = new DashboardResponseBody();
        try {
            Date now = new Date();
            List<String> topUserSentMail = clickHistoryService.getTopSentMail();
            responseBody.setList(topUserSentMail);
            responseBody.setMsg("done");
            responseBody.setStatus(true);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            logger.error("getTopUserSentMail: " + e.getMessage());
            responseBody.setMsg(e.getMessage());
            responseBody.setStatus(false);
            return ResponseEntity.ok(responseBody);
        }
    }
}
