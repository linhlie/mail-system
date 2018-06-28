package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.SentMailHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by khanhlvb on 6/28/18.
 */
public interface SentMailHistoryDAO extends PagingAndSortingRepository<SentMailHistory, Long> {
}
