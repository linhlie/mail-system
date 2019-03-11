package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.SchedulerSendEmailFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchedulerSendEmailFileDAO extends JpaRepository<SchedulerSendEmailFile, Long> {
}
