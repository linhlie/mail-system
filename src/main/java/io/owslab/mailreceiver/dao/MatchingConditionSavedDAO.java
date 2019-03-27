package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.MatchingConditionSaved;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingConditionSavedDAO extends JpaRepository<MatchingConditionSaved, Long> {
    List<MatchingConditionSaved> findByAccountCreatedId(long accountId);

    List<MatchingConditionSaved> findByAccountCreatedIdAndConditionNameAndConditionType(long accountId, String conditionName, int conditionType);

    List<MatchingConditionSaved> findByConditionTypeAndAccountCreatedId(int conditionType, long accountCreatedId);

}
