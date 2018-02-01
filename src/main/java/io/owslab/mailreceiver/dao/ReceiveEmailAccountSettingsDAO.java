package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Transactional
public interface ReceiveEmailAccountSettingsDAO extends PagingAndSortingRepository<ReceiveEmailAccountSetting, Long> {

    List<ReceiveEmailAccountSetting> findByDisabled(boolean disabled);
    List<ReceiveEmailAccountSetting> findById(long id);

}
