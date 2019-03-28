package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.DomainUnregister;
import io.owslab.mailreceiver.model.PeopleInChargePartnerUnregister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface PeopleInChargePartnerUnregisterDAO extends JpaRepository<PeopleInChargePartnerUnregister, Long> {
    List<PeopleInChargePartnerUnregister> findByStatus(int status);

    List<PeopleInChargePartnerUnregister> findByIdAndStatus(Long id, int status);

    @Transactional
    void deleteByEmailIn(List<String> enmail);

    @Transactional
    void deleteByEmail(String email);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
            value = "delete unpeo from people_in_charge_partner_unregister unpeo, people_in_charge_partner peo where peo.email_address = unpeo.email;",
            nativeQuery = true
    )
    void deletePeople();
}
