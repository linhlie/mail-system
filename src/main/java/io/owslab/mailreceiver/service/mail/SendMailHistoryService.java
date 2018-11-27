package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.SentMailHistoryDAO;
import io.owslab.mailreceiver.dto.SentMailHistoryDTO;
import io.owslab.mailreceiver.form.SentMailHistoryForm;
import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.SentMailHistory;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by khanhlvb on 6/29/18.
 */
@Service
public class SendMailHistoryService {

    @Autowired
    private SentMailHistoryDAO sentMailHistoryDAO;

    @Autowired
    AccountService accountService;

    public SentMailHistory getOne(long id){
        return sentMailHistoryDAO.findOne(id);
    }

    public List<SentMailHistory> search(SentMailHistoryForm form) {
        String filterType = form.getFilterType();
        String fromDateStr = form.getFromDateStr();
        String toDateStr = form.getToDateStr();
        if(filterType.equals(SentMailHistoryForm.FilterType.TODAY)) {
            return findToday();
        } else if (filterType.equals(SentMailHistoryForm.FilterType.PERIOD)) {
            return findByDateRange(fromDateStr, toDateStr);
        }
        return findAll();
    }

    public List<SentMailHistoryDTO> getSentMailhistory(SentMailHistoryForm form){
        List<SentMailHistory> rawHistories = search(form);
        List<Account> listAccount = accountService.getAllUserRoleAccounts();
        HashMap<Long, String>  mapAccount = new HashMap<>();
        for(Account account : listAccount){
            mapAccount.put(account.getId(), account.getAccountName());
        }
        List<SentMailHistoryDTO> histories = new ArrayList<>();
        for(SentMailHistory history : rawHistories) {
            String username = mapAccount.get(history.getAccountSentMailId());
            histories.add(new SentMailHistoryDTO(history, username));
        }
        return histories;
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
