package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.MatchingCondition;
import io.owslab.mailreceiver.model.MatchingConditionSaved;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by khanhlvb on 3/6/18.
 */
@Transactional
public interface MatchingConditionDAO extends CrudRepository<MatchingCondition, Long> {
    List<MatchingCondition> findByType(int type);

    void save(MatchingConditionSaved form);
}