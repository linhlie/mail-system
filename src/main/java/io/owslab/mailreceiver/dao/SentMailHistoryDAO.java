package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.SentMailHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by khanhlvb on 6/28/18.
 */
public interface SentMailHistoryDAO extends PagingAndSortingRepository<SentMailHistory, Long> {
    List<SentMailHistory> findBySentAtBetween(Date startDate, Date endDate);
    List<SentMailHistory> findBySentAtGreaterThanEqual(Date startDate);
    List<SentMailHistory> findBySentAtLessThanEqual(Date endDate);
}
