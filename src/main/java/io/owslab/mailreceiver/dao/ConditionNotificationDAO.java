package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.ConditionNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConditionNotificationDAO extends JpaRepository<ConditionNotification, Long> {

    @Modifying(clearAutomatically = true)
    @Query(
            value = "SELECT * FROM condition_notification c where c.from_account_id = :fromAccountId and c.condition_type = :conditionType order by send_at desc limit :listSize;",
            nativeQuery = true
    )
    List<ConditionNotification> findByToAccountIdAndConditionTypeLimit(@Param("fromAccountId") long fromAccountId, @Param("conditionType") int conditionType, @Param("listSize") int listSize);
}
