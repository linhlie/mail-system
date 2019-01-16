package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.NumberTreatment;
import io.owslab.mailreceiver.model.ReplaceLetter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by khanhlvb on 2/23/18.
 */
@Transactional
public interface NumberTreatmentDAO extends CrudRepository<NumberTreatment, Long> {
}
