package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.AccountDTO;
import io.owslab.mailreceiver.dto.BulletinBoardDTO;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.BulletinBoard;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.response.DashboardResponseBody;
import io.owslab.mailreceiver.service.bulletin.BulletinBoardService;
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
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

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
    private BulletinBoardService bulletinBoardService;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @Autowired
    private MailReceiveRuleService mrrs;

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

    @RequestMapping(value="/user/dashboard/mailStatistics", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getMailStatistics (@RequestParam(value = "accountId", required = false) String accountId){
        DashboardResponseBody responseBody = new DashboardResponseBody();
        try {
            Date now = new Date();
            String latestReceive = mailBoxService.getLatestReceive(accountId);
            List<EmailAccount> emailAccounts = mailAccountsService.list();
            int checkMailInterval = enviromentSettingService.getCheckMailTimeInterval();
            responseBody.setHasSystemError(ReportErrorService.hasSystemError());
            responseBody.setLatestReceive(latestReceive);
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

    @RequestMapping(value="/user/dashboard/getBulletinBoard", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getBulletinBoard (){
        DashboardResponseBody responseBody = new DashboardResponseBody();
        try {
            List<BulletinBoardDTO> listBulletinBoardDTO = bulletinBoardService.getBulletinBoard();
            responseBody.setListBulletinBoardDTO(listBulletinBoardDTO);
            responseBody.setMsg("done");
            responseBody.setStatus(true);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            logger.error("getBulletinBoard: " + e.getMessage());
            responseBody.setMsg(e.getMessage());
            responseBody.setStatus(false);
            return ResponseEntity.ok(responseBody);
        }
    }



    @RequestMapping(value = "/user/dashboard/saveBulletin", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveBulletin(@Valid @RequestBody BulletinBoardDTO bulletin, BindingResult bindingResult) {
        DashboardResponseBody responseBody = new DashboardResponseBody();
        if (bindingResult.hasErrors()) {
            responseBody.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(responseBody);
        }
        try {
            bulletinBoardService.saveBulletinBoard(bulletin);
            responseBody.setMsg("done");
            responseBody.setStatus(true);
        } catch (Exception e) {
            logger.error("saveBulletin: " + e.getMessage());
            responseBody.setMsg(e.getMessage());
            responseBody.setStatus(false);
        }
        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(value = "/user/dashboard/updateBulletinPosition", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateBulletinPosition(@Valid @RequestBody String startEndPosition, BindingResult bindingResult) {
        DashboardResponseBody responseBody = new DashboardResponseBody();
        if (bindingResult.hasErrors()) {
            responseBody.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(responseBody);
        }
        try {
            System.out.println(startEndPosition);
            bulletinBoardService.updateBulletinPosition(startEndPosition);
            responseBody.setMsg("done");
            responseBody.setStatus(true);
        } catch (Exception e) {
            logger.error("updateBulletinPosition: " + e.getMessage());
            responseBody.setMsg(e.getMessage());
            responseBody.setStatus(false);
        }
        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(value = "/user/dashboard/deleteBulletin/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteTabNumber(@PathVariable("id") long id) {
        try {
            bulletinBoardService.deleteTabNumber(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @RequestMapping(value="/user/dashboard/forceFetchMail", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> forceFetchMail (){
        AjaxResponseBody responseBody = new AjaxResponseBody();
        try {
            fetchMailsService.start();
            mrrs.checkMailStatus();
            responseBody.setMsg("done");
            responseBody.setStatus(true);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            logger.error("forceFetchMail: " + e.getMessage());
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
