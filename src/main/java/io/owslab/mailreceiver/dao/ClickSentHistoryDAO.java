package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.ClickSentHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

/**
 * Created by khanhlvb on 7/9/18.
 */
public interface ClickSentHistoryDAO extends PagingAndSortingRepository<ClickSentHistory, Long> {
    long countByTypeAndCreatedAtBetween(String type, Date fromDate, Date toDate);
}