package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.SchedulerSendEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchedulerSendEmailDAO extends JpaRepository<SchedulerSendEmail, Long> {
}
