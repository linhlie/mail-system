package io.owslab.mailreceiver.service.statistics;

import io.owslab.mailreceiver.dao.ClickHistoryDAO;
import io.owslab.mailreceiver.dao.ClickSentHistoryDAO;
import io.owslab.mailreceiver.model.ClickHistory;
import io.owslab.mailreceiver.model.ClickSentHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
