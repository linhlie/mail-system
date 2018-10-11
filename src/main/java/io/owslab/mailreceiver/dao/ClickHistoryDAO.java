package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.ClickHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;


/**
 * Created by khanhlvb on 7/9/18.
 */
public interface ClickHistoryDAO extends PagingAndSortingRepository<ClickHistory, Long> {
    long countByTypeAndCreatedAtBetween(int type, Date fromDate, Date toDate);
    long countByAccountIdAndTypeAndCreatedAtBetween(long accountId, int type, Date fromDate, Date toDate);
}