package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.SchedulerSendEmailFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulerSendEmailFileDAO extends JpaRepository<SchedulerSendEmailFile, Long> {
    List<SchedulerSendEmailFile> findBySchedulerSendEmailId(long id);
}
