package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.BusinessPartner;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by khanhlvb on 8/13/18.
 */
@Transactional
public interface BusinessPartnerDAO extends PagingAndSortingRepository<BusinessPartner, Long> {
    List<BusinessPartner> findByPartnerCode(String partnerCode);
}
