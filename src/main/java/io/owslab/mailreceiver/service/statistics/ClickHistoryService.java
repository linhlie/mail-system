package io.owslab.mailreceiver.service.statistics;

import io.owslab.mailreceiver.dao.ClickHistoryDAO;
import io.owslab.mailreceiver.dao.ClickSentHistoryDAO;
import io.owslab.mailreceiver.model.ClickHistory;
import io.owslab.mailreceiver.model.ClickSentHistory;
import io.owslab.mailreceiver.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void save(String type) {
        ClickHistory history = new ClickHistory(type);
        clickHistoryDAO.save(history);
    }

    public void saveSent(String type) {
        ClickSentHistory history = new ClickSentHistory(type);
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

    public List<String> getClickCount() {
        List<String> clickCount = new ArrayList<>();
        List<String> clickCount1 = getClickCountByType(ClickHistory.ClickType.EXTRACT_SOURCE);
        List<String> clickCount2 = getClickCountByType(ClickHistory.ClickType.EXTRACT_DESTINATION);
        List<String> clickCount3 = getClickCountByType(ClickHistory.ClickType.MATCHING);
        clickCount.addAll(clickCount1);
        clickCount.addAll(clickCount2);
        clickCount.addAll(clickCount3);
        return clickCount;
    }

    public List<String> getClickCountByType(String type) {
        List<String> clickCount = new ArrayList<>();
        Date now = new Date();
        Date fromDate = Utils.atStartOfDay(now);
        Date toDate = Utils.atEndOfDay(now);
        for(int i = 0; i < 8; i++){
            if(i > 0) {
                fromDate = Utils.addDayToDate(fromDate, -1);
                toDate = Utils.addDayToDate(toDate, -1);
            }
            long clicks = clickHistoryDAO.countByTypeAndCreatedAtBetween(type, fromDate, toDate);
            clickCount.add(Long.toString(clicks));
        }
        return clickCount;
    }
}
