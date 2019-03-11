package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.SchedulerSendEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulerSendEmailDAO extends JpaRepository<SchedulerSendEmail, Long> {
    List<SchedulerSendEmail> findByAccountIdOrderBySentAt(long accountId);
    List<SchedulerSendEmail> findById(long id);
}
