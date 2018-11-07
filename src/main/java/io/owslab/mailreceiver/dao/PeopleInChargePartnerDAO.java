package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.PeopleInChargePartner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeopleInChargePartnerDAO extends JpaRepository<PeopleInChargePartner, Long> {

    List<PeopleInChargePartner> findByPartnerId(long partnerId);

    PeopleInChargePartner findByEmailInChargePartner(boolean emailInChargePartner);

    PeopleInChargePartner findByEmailAddress(String emailAddress);
}
