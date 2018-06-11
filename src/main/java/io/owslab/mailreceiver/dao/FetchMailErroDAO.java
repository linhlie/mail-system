package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.FetchMailError;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by khanhlvb on 6/11/18.
 */
@Transactional
public interface FetchMailErroDAO extends CrudRepository<FetchMailError, Long> {
}
