package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.ReplaceNumber;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by khanhlvb on 2/13/18.
 */
@Transactional
public interface ReplaceNumberDAO extends CrudRepository<ReplaceNumber, Long> {
    List<ReplaceNumber> findByCharacter(String character);
}
