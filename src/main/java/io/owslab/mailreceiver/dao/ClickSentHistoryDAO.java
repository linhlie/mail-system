package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.ClickSentHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by khanhlvb on 7/9/18.
 */
public interface ClickSentHistoryDAO extends PagingAndSortingRepository<ClickSentHistory, Long> {
}