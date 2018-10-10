package io.owslab.mailreceiver.service.statistics;

import io.owslab.mailreceiver.dao.ClickHistoryDAO;
import io.owslab.mailreceiver.dao.ClickSentHistoryDAO;
import io.owslab.mailreceiver.model.ClickHistory;
import io.owslab.mailreceiver.model.ClickSentHistory;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.utils.UserStatisticSentMail;
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
            case 8:
                return ClickHistory.ClickType.EMAIL_MATCHING_ENGINEER;
            case 9:
                return ClickHistory.ClickType.REPLY_EMAIL_MATCHING_ENGINEER;
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
            case 5:
                return ClickSentHistory.ClickSentType.REPLY_EMAIL_MATCHING_ENGINEER;
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

    public List<String> getClickEmailMatchingEngineerCount(Date now, String accountId) {
        List<String> clickCount = new ArrayList<>();
        List<String> clickCount1 = getClickCountByType(now, accountId, ClickHistory.ClickType.EMAIL_MATCHING_ENGINEER);
        clickCount.addAll(clickCount1);
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
        List<String> sent2 = getClickSentCountByType(accountId, ClickSentHistory.ClickSentType.MATCHING_DESTINATION);
        List<String> sent3 = getClickSentCountByType(accountId, ClickSentHistory.ClickSentType.REPLY_SOURCE);
        List<String> sent4 = getClickSentCountByType(accountId, ClickSentHistory.ClickSentType.REPLY_DESTINATION);
        stats.addAll(sent1);
        stats.addAll(sent2);
        stats.addAll(sent3);
        stats.addAll(sent4);
        return stats;
    }

    public List<String> getSendMailEmailMatchingEngineerClick(Date now, String accountId) {
        List<String> stats = new ArrayList<>();
        List<String> sent1 = getClickSentCountByType(accountId, ClickSentHistory.ClickSentType.REPLY_EMAIL_MATCHING_ENGINEER);
        stats.addAll(sent1);
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

    public String getTopSentMail(){
        Date now = new Date();
        Date fromDate = Utils.atStartOfDay(now);
        Date toDate = Utils.atEndOfDay(now);
        List<Object[]>  listObject = clickSentHistoryDAO.findTopSentMailObject(fromDate, toDate);
        LinkedHashMap<Integer,List<String>> topUserSentMail = new LinkedHashMap<>();
        String result = "";
        int count = 0;
        if(listObject!=null && !listObject.isEmpty()){
            Object[] objectUser = listObject.get(0);
            String topQuantity = objectUser[1]+"";
            if( topQuantity == null || topQuantity.trim().equals("0")){
                return "該当なし";
            }
            for( Object[] object : listObject){
                String username = "";
                if(object[2] == null || (object[0]+"").trim().equals("")){
                    username = ""+object[0];
                }else {
                    username = "" + object[2];
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
                    rank = rank + usernames.get(0) + "さん" + key + "件、";
                }else{
                    rank = usernames.get(0) + "さん";
                    for(int i=1;i<usernames.size()-1;i++){
                        rank = rank + "と" +usernames.get(i) + "さん";
                    }
                    rank = rank + "と" + usernames.get(usernames.size()-1) + "さんが同じ" + key + "件、";
                }
                result = result + rank;
            }
            return result.substring(0, result.length()-1);
        }else{
            return "該当なし";
        }
    }
}
