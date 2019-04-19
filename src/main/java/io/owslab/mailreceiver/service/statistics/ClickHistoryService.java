package io.owslab.mailreceiver.service.statistics;

import io.owslab.mailreceiver.dao.ClickHistoryDAO;
import io.owslab.mailreceiver.dao.ClickSentHistoryDAO;
import io.owslab.mailreceiver.enums.ClickType;
import io.owslab.mailreceiver.enums.SentMailType;
import io.owslab.mailreceiver.model.ClickHistory;
import io.owslab.mailreceiver.model.ClickSentHistory;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

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

    public void save(int type) {
        long loggedInAccountId = accountService.getLoggedInAccountId();
        ClickHistory history = new ClickHistory(type, loggedInAccountId);
        clickHistoryDAO.save(history);
    }

    public void saveSent(int type, long userId) {
        ClickSentHistory history = new ClickSentHistory(type, userId);
        clickSentHistoryDAO.save(history);
    }

    public List<String> getClickCount(Date now, String accountId) {
        List<String> clickCount = new ArrayList<>();
        List<String> clickCount1 = getClickCountByType(now, accountId, ClickType.EXTRACT_SOURCE.getValue());
        List<String> clickCount2 = getClickCountByType(now, accountId, ClickType.EXTRACT_DESTINATION.getValue());
        List<String> clickCount3 = getClickCountByType(now, accountId, ClickType.MATCHING.getValue());
        clickCount.addAll(clickCount1);
        clickCount.addAll(clickCount2);
        clickCount.addAll(clickCount3);
        return clickCount;
    }

    public List<String> getClickEmailMatchingEngineerCount(Date now, String accountId) {
        List<String> clickCount = new ArrayList<>();
        List<String> clickCount1 = getClickCountByType(now, accountId, ClickType.EMAIL_MATCHING_ENGINEER.getValue());
        clickCount.addAll(clickCount1);
        return clickCount;
    }

    public List<String> getClickReplyEmailsViaInboxCount(Date now, String accountId){
        List<String> clickCount = new ArrayList<>();
        List<String> clickCount1 = getClickCountByType(now, accountId, ClickType.REPLY_EMAIL_VIA_INBOX.getValue());
        clickCount.addAll(clickCount1);
        return clickCount;
    }

    public List<String> getCreateSchedulerCount(Date now, String accountId){
        List<String> clickCount = new ArrayList<>();
        List<String> clickCount1 = getClickCountByType(now, accountId, ClickType.SEND_EMAIL_SCHEDULE.getValue());
        clickCount.addAll(clickCount1);
        return clickCount;
    }

    private List<String> getClickCountByType(Date now, String accountId, int type) {
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
        List<String> sent1 = getClickSentCountByType(accountId, SentMailType.MATCHING_SOURCE.getValue());
        List<String> sent2 = getClickSentCountByType(accountId, SentMailType.MATCHING_DESTINATION.getValue());
        List<String> sent3 = getClickSentCountByType(accountId, SentMailType.REPLY_SOURCE.getValue());
        List<String> sent4 = getClickSentCountByType(accountId, SentMailType.REPLY_DESTINATION.getValue());
        stats.addAll(sent1);
        stats.addAll(sent2);
        stats.addAll(sent3);
        stats.addAll(sent4);
        return stats;
    }

    public List<String> getSendMailEmailMatchingEngineerClick(Date now, String accountId) {
        List<String> stats = new ArrayList<>();
        List<String> sent1 = getClickSentCountByType(accountId, SentMailType.REPLY_EMAIL_MATCHING_ENGINEER.getValue());
        stats.addAll(sent1);
        return stats;
    }

    public List<String> getReplyEmailsInboxCount(String accountId){
        List<String> clickCount = new ArrayList<>();
        List<String> clickCount1 = getClickSentCountByType(accountId, SentMailType.SEND_VIA_INBOX.getValue());
        clickCount.addAll(clickCount1);
        return clickCount;
    }

    public List<String> getSendMailSchedulerCount(String accountId){
        List<String> clickCount = new ArrayList<>();
        List<String> clickCount1 = getClickSentCountByType(accountId, SentMailType.SEND_MAIL_SCHEDULER.getValue());
        clickCount.addAll(clickCount1);
        return clickCount;
    }

    private List<String> getClickSentCountByType(String accountId, int type) {
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

    public List<String> getTopSentMail(){
        Date now = new Date();
        Date fromDate = Utils.atStartOfDay(now);
        Date toDate = Utils.atEndOfDay(now);
        List<Object[]>  listObject = clickSentHistoryDAO.findTopSentMailObject(fromDate, toDate);
        LinkedHashMap<Integer,List<String>> topUserSentMail = new LinkedHashMap<>();
        List<String> result = new ArrayList<>();
        int count = 0;
        if(listObject!=null && !listObject.isEmpty()){
            Object[] objectUser = listObject.get(0);
            String topQuantity = objectUser[1]+"";
            if( topQuantity == null){
                result.add("該当なし");
                return result;
            }
            for( Object[] object : listObject){
                String username = "";
                if(object[2] == null || object[3] == null){
                    username = ""+object[0];
                }else {
                    username = object[2] + "　" + object[3];
                }
                int quantity = Integer.parseInt((object[1]+"").trim());
                if(topUserSentMail.containsKey(quantity)){
                    topUserSentMail.get(quantity).add(username);
                }else{
                    if(count==3){
                        break;
                    }
                    List<String> usernames = new ArrayList<>();
                    usernames.add(username);
                    topUserSentMail.put(quantity,usernames);
                    count++;
                }
            }
            Iterator<Integer> linkedHashMapIterator = topUserSentMail.keySet().iterator();
            while (linkedHashMapIterator.hasNext()) {
                String rank = "";
                Integer key = linkedHashMapIterator.next();
                List<String> usernames = topUserSentMail.get(key);
                usernames.sort(String.CASE_INSENSITIVE_ORDER);
                if(usernames.size()==1){
                    rank = rank + usernames.get(0) + "(" + key + "件)";
                }else{
                    rank = usernames.get(0) ;
                    for(int i=1;i<usernames.size()-1;i++){
                        rank = rank + "、" +usernames.get(i);
                    }
                    rank = rank + "、" + usernames.get(usernames.size()-1) + "(" + key + "件)";
                }
                result.add(rank +"　");
            }
            return result;
        }else{
            result.add("該当なし");
            return result;
        }
    }
}
