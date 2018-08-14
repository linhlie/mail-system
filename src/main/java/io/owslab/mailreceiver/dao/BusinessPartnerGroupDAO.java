package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.BusinessPartnerGroup;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by khanhlvb on 8/14/18.
 */

@Transactional
public interface BusinessPartnerGroupDAO extends PagingAndSortingRepository<BusinessPartnerGroup, Long> {
    List<BusinessPartnerGroup> findByPartnerId(long partnerId);
}
