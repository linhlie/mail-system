package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.DomainUnregister;
import io.owslab.mailreceiver.model.PeopleInChargePartnerUnregister;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface PeopleInChargePartnerUnregisterDAO extends JpaRepository<PeopleInChargePartnerUnregister, Long> {
    List<PeopleInChargePartnerUnregister> findByStatus(int status);

    List<PeopleInChargePartnerUnregister> findByIdAndStatus(Long id, int status);

    @Transactional
    void deleteByEmailIn(List<String> enmail);

    @Transactional
    void deleteByEmail(String email);
}
