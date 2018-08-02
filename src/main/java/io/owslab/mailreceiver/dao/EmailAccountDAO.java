package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.EmailAccount;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface EmailAccountDAO extends PagingAndSortingRepository<EmailAccount, Long> {
    List<EmailAccount> findByDisabled(boolean disabled);
    List<EmailAccount> findByAlertSend(boolean alertSend);
    List<EmailAccount> findById(long id);
}
