package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.SentMailHistoryDAO;
import io.owslab.mailreceiver.form.SentMailHistoryForm;
import io.owslab.mailreceiver.model.SentMailHistory;
import io.owslab.mailreceiver.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by khanhlvb on 6/29/18.
 */
@Service
public class SendMailHistoryService {

    @Autowired
    private SentMailHistoryDAO sentMailHistoryDAO;

    public List<SentMailHistory> search(SentMailHistoryForm form) {
        String filterType = form.getFilterType();
        String fromDateStr = form.getFromDateStr();
        String toDateStr = form.getToDateStr();
        if(filterType.equals(SentMailHistoryForm.FilterType.TODAY)) {
            return findToday();
        }
        if((fromDateStr != null && fromDateStr.length() > 0) || (toDateStr != null && toDateStr.length() > 0)) {
            return findByDateRange(fromDateStr, toDateStr);
        }
        return findAll();
    }

    private List<SentMailHistory> findToday() {
        Date now = new Date();
        Date fromDate = Utils.atStartOfDay(now);
        Date toDate = Utils.atEndOfDay(now);
        return findByDateRange(fromDate, toDate);
    }

    private List<SentMailHistory> findByDateRange(String fromDateStr, String toDateStr) {
        try {
            Date fromDate = null;
            Date toDate = null;
            if(fromDateStr != null && fromDateStr.length() > 0) {
                fromDate = Utils.parseDateStr(fromDateStr);
                fromDate = Utils.atStartOfDay(fromDate);
            }
            if(toDateStr != null && toDateStr.length() > 0) {
                toDate = Utils.parseDateStr(toDateStr);
                toDate = Utils.atEndOfDay(toDate);
            }
            return findByDateRange(fromDate, toDate);
        } catch (ParseException e) {
           ;
        }
        return new ArrayList<>();
    }

    private List<SentMailHistory> findByDateRange(Date fromDate, Date toDate) {
        if(fromDate != null && toDate != null){
            return sentMailHistoryDAO.findBySentAtBetween(fromDate, toDate);
        } else if (fromDate != null) {
            return sentMailHistoryDAO.findBySentAtGreaterThanEqual(fromDate);
        } else if (toDate != null) {
            return sentMailHistoryDAO.findBySentAtLessThanEqual(toDate);
        }
        return findAll();
    }

    private List<SentMailHistory> findAll() {
        return (List<SentMailHistory>)sentMailHistoryDAO.findAll();
    }
}
