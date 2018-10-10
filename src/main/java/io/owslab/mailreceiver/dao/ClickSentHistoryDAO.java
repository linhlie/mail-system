package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.ClickSentHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by khanhlvb on 7/9/18.
 */
public interface ClickSentHistoryDAO extends PagingAndSortingRepository<ClickSentHistory, Long> {
    long countByTypeAndCreatedAtBetween(String type, Date fromDate, Date toDate);
    long countByAccountIdAndTypeAndCreatedAtBetween(long accountId, String type, Date fromDate, Date toDate);

    @Query(value = "SELECT acc.user_name, COUNT(his.account_id) " +
            "FROM click_sent_histories his, accounts acc " +
            "WHERE acc.id = his.account_id " +
            "AND his.created_at BETWEEN :fromDate AND :toDate " +
            "GROUP BY his.account_id " +
            "ORDER BY COUNT(his.account_id) DESC",
            nativeQuery = true
    )
    List<Object[]> findTopSentMailObject (@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
}