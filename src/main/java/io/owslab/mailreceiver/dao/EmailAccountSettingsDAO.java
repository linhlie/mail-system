package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Transactional
public interface EmailAccountSettingsDAO extends PagingAndSortingRepository<EmailAccountSetting, Long> {

    List<EmailAccountSetting> findByAccountIdAndType(long accountId, int type);
    List<EmailAccountSetting> findById(long id);

    List<EmailAccountSetting> findByUserNameAndType(String username, int type);

}
