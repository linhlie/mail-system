package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.EmailWord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface EmailWordDAO extends CrudRepository<EmailWord, Long> {
    List<EmailWord> findByStatus(int status);
}
