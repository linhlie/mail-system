package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.EmailAddressGroup;
import io.owslab.mailreceiver.model.EmailsAddressInGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailsAddressInGroupDAO extends JpaRepository<EmailsAddressInGroup, Long> {

    List<EmailsAddressInGroup> findByGroupId(long groupId);

    List<EmailsAddressInGroup> findByGroupIdAndPeopleInChargeId(long groupId, long peopleInChargeId);

}
