package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.response.DashboardResponseBody;
import io.owslab.mailreceiver.service.mail.FetchMailsService;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.matching.MatchingConditionService;
import io.owslab.mailreceiver.service.statistics.ClickHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private FetchMailsService fetchMailsService;

    @Autowired
    private MatchingConditionService matchingConditionService;

    @Autowired
    private ClickHistoryService clickHistoryService;

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

    @RequestMapping(value="/user/dashboard/statistics", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getStatistics (){
        DashboardResponseBody responseBody = new DashboardResponseBody();
        try {
            String latestReceive = mailBoxService.getLatestReceive();
            List<String> clickCount = clickHistoryService.getClickCount();
            List<String> receiveMailNumber = new ArrayList<>();
            List<String> sendPerClick = new ArrayList<>();
            responseBody.setLatestReceive(latestReceive);
            responseBody.setReceiveMailNumber(receiveMailNumber);
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
