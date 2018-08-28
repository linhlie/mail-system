package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.BusinessPartnerGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by khanhlvb on 8/14/18.
 */

@Transactional
public interface BusinessPartnerGroupDAO extends PagingAndSortingRepository<BusinessPartnerGroup, Long> {
    List<BusinessPartnerGroup> findByPartnerId(long partnerId);

    @Query(
            value = "select * from business_partner_groups bpg where bpg.partner_id=:partnerId and bpg.with_partner_id=:withPartnerId",
            nativeQuery = true
    )
    List<BusinessPartnerGroup> findByPartnerIdAndWithPartnerId(@Param("partnerId") long partnerId, @Param("withPartnerId") long withPartnerId);
}
