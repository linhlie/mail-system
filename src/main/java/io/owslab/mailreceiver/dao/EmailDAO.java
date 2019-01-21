package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Transactional
public interface EmailDAO extends PagingAndSortingRepository<Email, String> {
    List<Email> findByAccountIdOrderBySentAtDesc(long accountId);
    List<Email> findByMessageId(String messageId);
    List<Email> findByMessageIdIn(List<String> messageIds);
    List<Email> findBySentAtBeforeOrderBySentAtAsc(Date sentAt);
    Page<Email> findBySubjectIgnoreCaseContainingAndErrorLogNotNull(String subject, Pageable pageable);
    Page<Email> findByStatus(int status, Pageable pageable);
    List<Email> findByStatus(int status);
    Page<Email> findByErrorLogNotNull(Pageable pageable);
    List<Email> findFirst1ByStatusOrderByReceivedAtDesc(int status);
    List<Email> findFirst1ByAccountIdAndStatusOrderByReceivedAtDesc(long accountId, int status);
    List<Email> findByStatusOrderByReceivedAtDesc(int status);
    List<Email> findByStatusOrStatusOrderByReceivedAtDesc(int status, int orStatus);
    long countByStatus(int status);
    long countByAccountIdAndReceivedAtBetweenAndStatus(long accountId, Date fromDate, Date toDate, int status);
    long countByReceivedAtBetweenAndStatus(Date fromDate, Date toDate, int status);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE emails e SET e.status = :newStatus WHERE e.status = :oldStatus",
            nativeQuery = true
    )
    int updateStatus(@Param("oldStatus") int oldStatus, @Param("newStatus") int newStatus);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE emails e SET e.status = :newStatus WHERE e.status = :oldStatus AND e.message_id in :msgIds",
            nativeQuery = true
    )
    int updateStatusByMessageIdIn(@Param("oldStatus") int oldStatus, @Param("newStatus") int newStatus, @Param("msgIds") Collection<String> msgIds);
    
    @Modifying(clearAutomatically = true)
    @Query(
            value = "SELECT e.from FROM emails e  WHERE e.status = :status GROUP BY e.from",
            nativeQuery = true
    )
    List<String> findByStatusGroupByFrom(@Param("status") int status);

    @Query(
            value = "SELECT * FROM emails e " +
                    "WHERE e.status = :status " +
                    "AND (lower(e.from) like :content " +
                    "OR lower(e.to) like :content " +
                    "OR lower(e.cc) like :content " +
                    "OR lower(e.subject) like :content " +
                    "OR lower(e.optimized_body) like :content ) " +
                    "ORDER BY e.sent_at DESC LIMIT :pageSize OFFSET :pageOffset",
            nativeQuery = true
    )
    List<Email> findByStatusAndFromOrToOrCcOrSubjectOrBody(@Param("status") int status, @Param("content") String content, @Param("pageOffset") int pageOffset, @Param("pageSize") int pageSize);

    @Query(
            value = "SELECT COUNT(e.status) FROM emails e " +
                    "WHERE e.status = :status " +
                    "AND (lower(e.from) like :content " +
                    "OR lower(e.to) like :content " +
                    "OR lower(e.cc) like :content " +
                    "OR lower(e.subject) like :content " +
                    "OR lower(e.optimized_body) like :content ) ",
            nativeQuery = true
    )
    int countFindByStatusAndFromOrToOrCcOrSubjectOrBody(@Param("status") int status, @Param("content") String content);
}
