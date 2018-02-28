package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

@Transactional
public interface EmailDAO extends PagingAndSortingRepository<Email, String> {
    List<Email> findByAccountIdOrderBySentAtDesc(long accountId);
    List<Email> findByMessageId(String messageId);
    List<Email> findByMessageIdAndDeleted(String messageId, boolean deleted);
    List<Email> findBySentAtBeforeAndDeletedOrderBySentAtAsc(Date sentAt, boolean deleted);
    Page<Email> findByOptimizedBodyIgnoreCaseContainingAndDeleted(String content, boolean deleted, Pageable pageable);
    Page<Email> findByDeleted(boolean deleted, Pageable pageable);
    long countByDeleted(boolean deleted);
}
