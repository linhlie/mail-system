package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.EmailWord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by khanhlvb on 2/9/18.
 */
@Transactional
public interface EmailWordDAO extends CrudRepository<EmailWord, Long> {

}
