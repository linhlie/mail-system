package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.ConditionNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ConditionNotificationDAO extends JpaRepository<ConditionNotification, Long> {

    @Modifying(clearAutomatically = true)
    @Query(
            value = "SELECT * FROM condition_notification c where c.to_account_id = :toAccountId and c.condition_type = :conditionType order by c.sent_at desc limit :listSize",
            nativeQuery = true
    )
    List<ConditionNotification> findByToAccountIdAndConditionTypeLimit(@Param("toAccountId") long toAccountId, @Param("conditionType") int conditionType, @Param("listSize") int listSize);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "SELECT * FROM condition_notification c where c.to_account_id = :toAccountId and c.condition_type = :conditionType and c.sent_at < :sentAt order by c.sent_at desc limit :listSize",
            nativeQuery = true
    )
    List<ConditionNotification> getMoreConditionNotifications(@Param("toAccountId") long toAccountId, @Param("conditionType") int conditionType, @Param("sentAt") String sentAt, @Param("listSize") int listSize);

    long countConditionNotificationByToAccountIdAndConditionTypeAndStatus(long toAccountId, int conditionType, int status);
}
