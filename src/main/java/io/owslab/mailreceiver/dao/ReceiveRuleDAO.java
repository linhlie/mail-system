package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.ReceiveRule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by khanhlvb on 7/17/18.
 */
@Transactional
public interface ReceiveRuleDAO extends CrudRepository<ReceiveRule, Long> {
    List<ReceiveRule> findByNameAndTypeOrderByLastUpdateDesc(String name, int type);
}
