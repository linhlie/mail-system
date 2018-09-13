package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Transactional
public interface EmailDAO extends PagingAndSortingRepository<Email, String> {
    List<Email> findByAccountIdOrderBySentAtDesc(long accountId);
    List<Email> findByAccountIdOrderByReceivedAt(long accountId);
    List<Email> findByAccountIdOrderByMessageNumberDesc(long accountId);
    List<Email> findByMessageId(String messageId);
    List<Email> findByMessageIdIn(List<String> messageIds);
    List<Email> findByMessageIdAndDeleted(String messageId, boolean deleted);
    List<Email> findBySentAtBeforeOrderBySentAtAsc(Date sentAt);
    Page<Email> findByOptimizedBodyIgnoreCaseContainingAndStatus(String content, int status, Pageable pageable);
    Page<Email> findBySubjectIgnoreCaseContainingAndErrorLogNotNull(String subject, Pageable pageable);
    Page<Email> findByOriginalBodyIgnoreCaseContainingAndDeleted(String content, boolean deleted, Pageable pageable);
    Page<Email> findByDeleted(boolean deleted, Pageable pageable);
    Page<Email> findByStatus(int status, Pageable pageable);
    List<Email> findByStatus(int status);
    Page<Email> findByErrorLogNotNull(Pageable pageable);
    List<Email> findByDeleted(boolean deleted);
    List<Email> findFirst1ByStatusOrderByReceivedAtDesc(int status);
    List<Email> findFirst1ByAccountIdAndStatusOrderByReceivedAtDesc(long accountId, int status);
    List<Email> findByErrorLogIsNullOrderByReceivedAtDesc();
    List<Email> findByStatusOrderByReceivedAtDesc(int status);
    long countByDeleted(boolean deleted);
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
}
