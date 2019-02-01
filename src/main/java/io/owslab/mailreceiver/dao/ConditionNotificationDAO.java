package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.ConditionNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionNotificationDAO extends JpaRepository<ConditionNotification, Long> {

}
