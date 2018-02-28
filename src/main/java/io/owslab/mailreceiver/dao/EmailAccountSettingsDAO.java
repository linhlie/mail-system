package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Transactional
public interface EmailAccountSettingsDAO extends PagingAndSortingRepository<EmailAccountSetting, Long> {

    List<EmailAccountSetting> findByDisabled(boolean disabled);
    List<EmailAccountSetting> findByTypeAndDisabled(int type, boolean disabled);
    List<EmailAccountSetting> findById(long id);

}
