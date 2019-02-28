package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.EmailAddressGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailAddressGroupDAO extends JpaRepository<EmailAddressGroup, Long> {

    List<EmailAddressGroup> findByGroupNameAndAccountCreateId(String groupName, long accountCreateId);

    List<EmailAddressGroup> findByAccountCreateIdOrderByGroupNameAsc(long accountCreateId);
}
