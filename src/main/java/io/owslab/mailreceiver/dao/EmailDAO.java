package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Email;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Transactional
public interface EmailDAO extends CrudRepository<Email, String> {
    List<Email> findByAccountIdOrderBySentAtDesc(long accountId);
    List<Email> findByMessageId(String messageId);
}
