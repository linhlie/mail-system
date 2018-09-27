package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.BusinessPartner;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by khanhlvb on 8/13/18.
 */
@Transactional
public interface BusinessPartnerDAO extends PagingAndSortingRepository<BusinessPartner, Long> {
    List<BusinessPartner> findByPartnerCode(String partnerCode);
    
    @Query(
            value = "select * from business_partners bp where bp.domain1=:domain or bp.domain2=:domain or bp.domain3=:domain",
            nativeQuery = true
    )
    List<BusinessPartner> findByDomain(@Param("domain") String domain );
}
