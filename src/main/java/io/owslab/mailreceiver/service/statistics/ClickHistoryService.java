package io.owslab.mailreceiver.service.statistics;

import io.owslab.mailreceiver.dao.ClickHistoryDAO;
import io.owslab.mailreceiver.dao.ClickSentHistoryDAO;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.ClickHistory;
import io.owslab.mailreceiver.model.ClickSentHistory;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by khanhlvb on 7/9/18.
 */
@Service
public class ClickHistoryService {

    @Autowired
    private ClickHistoryDAO clickHistoryDAO;

    @Autowired
    private ClickSentHistoryDAO clickSentHistoryDAO;

    @Autowired
    private AccountService accountService;

    private DecimalFormat df = new DecimalFormat("#,##0.00");
    private DecimalFormat df2 = new DecimalFormat("#,##0");

    public void save(String type) {
        long loggedInAccountId = accountService.getLoggedInAccountId();
        ClickHistory history = new ClickHistory(type, loggedInAccountId);
        clickHistoryDAO.save(history);
    }

    public void saveSent(String type) {
        long loggedInAccountId = accountService.getLoggedInAccountId();
        ClickSentHistory history = new ClickSentHistory(type, loggedInAccountId);
        clickSentHistoryDAO.save(history);
    }

    public String getTypeFromInt(int value){
        switch (value) {
            case 1:
                return ClickHistory.ClickType.EXTRACT_SOURCE;
            case 2:
                return ClickHistory.ClickType.EXTRACT_DESTINATION;
            case 3:
                return ClickHistory.ClickType.MATCHING;
            case 4:
                return ClickHistory.ClickType.MATCHING_SOURCE;
            case 5:
                return ClickHistory.ClickType.MATCHING_DESTINATION;
            case 6:
                return ClickHistory.ClickType.REPLY_SOURCE;
            case 7:
                return ClickHistory.ClickType.REPLY_DESTINATION;
            default:
                return "";
        }
    }

    public String getSentTypeFromInt(int value){
        switch (value) {
            case 1:
                return ClickSentHistory.ClickSentType.MATCHING_SOURCE;
            case 2:
                return ClickSentHistory.ClickSentType.MATCHING_DESTINATION;
            case 3:
                return ClickSentHistory.ClickSentType.REPLY_SOURCE;
            case 4:
                return ClickSentHistory.ClickSentType.REPLY_DESTINATION;
            default:
                return "";
        }
    }

    public List<String> getClickCount(Date now, String accountId) {
        List<String> clickCount = new ArrayList<>();
        List<String> clickCount1 = getClickCountByType(now, accountId, ClickHistory.ClickType.EXTRACT_SOURCE);
        List<String> clickCount2 = getClickCountByType(now, accountId, ClickHistory.ClickType.EXTRACT_DESTINATION);
        List<String> clickCount3 = getClickCountByType(now, accountId, ClickHistory.ClickType.MATCHING);
        clickCount.addAll(clickCount1);
        clickCount.addAll(clickCount2);
        clickCount.addAll(clickCount3);
        return clickCount;
    }

    private List<String> getClickCountByType(Date now, String accountId, String type) {
        List<String> clickCount = new ArrayList<>();
        Date fromDate = Utils.atStartOfDay(now);
        Date toDate = Utils.atEndOfDay(now);
        for(int i = 0; i < 8; i++){
            if(i > 0) {
                fromDate = Utils.addDayToDate(fromDate, -1);
                toDate = Utils.addDayToDate(toDate, -1);
            }
            long clicks = accountId == null ? clickHistoryDAO.countByTypeAndCreatedAtBetween(type, fromDate, toDate)
                    : clickHistoryDAO.countByAccountIdAndTypeAndCreatedAtBetween(Long.parseLong(accountId), type, fromDate, toDate);
            clickCount.add(df2.format(clicks));
        }
        return clickCount;
    }

    public List<String> getTotalSentStats(Date now, String accountId) {
        List<String> stats = new ArrayList<>();
        List<String> sent1 = getClickSentCountByType(accountId, ClickSentHistory.ClickSentType.MATCHING_SOURCE);
        List<String> click1 = getClickCountByType(now, accountId, ClickHistory.ClickType.MATCHING);
        List<String> sent2 = getClickSentCountByType(accountId, ClickSentHistory.ClickSentType.MATCHING_DESTINATION);
        List<String> click2 = click1;
        List<String> sent3 = getClickSentCountByType(accountId, ClickSentHistory.ClickSentType.REPLY_SOURCE);
        List<String> click3 = getClickCountByType(now, accountId, ClickHistory.ClickType.EXTRACT_SOURCE);
        List<String> sent4 = getClickSentCountByType(accountId, ClickSentHistory.ClickSentType.REPLY_DESTINATION);
        List<String> click4 = getClickCountByType(now, accountId, ClickHistory.ClickType.EXTRACT_DESTINATION);
        stats.addAll(sent1);
        stats.addAll(getSentRate(sent1, click1));
        stats.addAll(sent2);
        stats.addAll(getSentRate(sent2, click2));
        stats.addAll(sent3);
        stats.addAll(getSentRate(sent3, click3));
        stats.addAll(sent4);
        stats.addAll(getSentRate(sent4, click4));
        return stats;
    }

    private List<String> getClickSentCountByType(String accountId, String type) {
        List<String> clickSentCount = new ArrayList<>();
        Date now = new Date();
        Date fromDate = Utils.atStartOfDay(now);
        Date toDate = Utils.atEndOfDay(now);
        for(int i = 0; i < 8; i++){
            if(i > 0) {
                fromDate = Utils.addDayToDate(fromDate, -1);
                toDate = Utils.addDayToDate(toDate, -1);
            }
            long clicks = accountId == null ? clickSentHistoryDAO.countByTypeAndCreatedAtBetween(type, fromDate, toDate)
                    : clickSentHistoryDAO.countByAccountIdAndTypeAndCreatedAtBetween(Long.parseLong(accountId), type, fromDate, toDate);
            clickSentCount.add(df2.format(clicks));
        }
        return clickSentCount;
    }

    private List<String> getSentRate(List<String> sentCounts, List<String> clickCounts) {
        List<String> result = new ArrayList<>();
        for(int i = 0; i < 8; i++) {
            String sentCountStr = sentCounts.get(i);
            String clickCountStr = clickCounts.get(i);
            long clickCount = Long.parseLong(clickCountStr);
            if(clickCount == 0) {
                result.add(df.format(0));
            } else {
                long sentCount = Long.parseLong(sentCountStr);
                double rate = ((double) sentCount / (double) clickCount);
                result.add(df.format(rate));
            }
        }
        return result;
    }
}
