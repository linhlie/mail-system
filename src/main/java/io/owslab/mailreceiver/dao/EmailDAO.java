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
    List<Email> findByCreatedAtBeforeOrderByCreatedAtAsc(Date createdAt);
    Page<Email> findByOptimizedBodyIgnoreCaseContaining(String content, Pageable pageable);
}
