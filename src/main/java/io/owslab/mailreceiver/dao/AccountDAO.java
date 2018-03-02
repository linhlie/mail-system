package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by khanhlvb on 3/1/18.
 */
@Transactional
public interface AccountDAO extends CrudRepository<Account, String> {
    List<Account> findByUserRole(String userRole);
}
