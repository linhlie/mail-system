package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.NumberRange;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by khanhlvb on 2/23/18.
 */
@Transactional
public interface NumberRangeDAO extends CrudRepository<NumberRange, Long> {

}
