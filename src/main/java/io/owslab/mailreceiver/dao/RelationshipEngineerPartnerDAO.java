package io.owslab.mailreceiver.dao;

import java.util.Collection;
import java.util.List;

import io.owslab.mailreceiver.model.RelationshipEngineerPartner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import io.owslab.mailreceiver.model.Engineer;

public interface RelationshipEngineerPartnerDAO extends JpaRepository<RelationshipEngineerPartner, Long>{
	
	 List<RelationshipEngineerPartner> findByEngineerId(Long id);
	
    @Modifying(clearAutomatically = true)
    @Query(
            value = "DELETE e FROM relationship_engineer_partner e  WHERE e.engineer_id = :engineerId AND e.partner_id = :partnerId",
            nativeQuery = true
    )
    void deleteByEngineerIdAndPartnerId(@Param("engineerId") long engineerId, @Param("partnerId") long partnerId);
    
    void deleteByIdIn(List<Long> listId);
    
    void deleteByEngineerId(Long engineerId);
}
