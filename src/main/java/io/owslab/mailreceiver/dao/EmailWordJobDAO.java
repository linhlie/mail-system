package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.EmailWordJob;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface EmailWordJobDAO extends CrudRepository<EmailWordJob, Long> {

}
