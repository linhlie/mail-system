package io.owslab.mailreceiver.dao;

import java.util.List;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import io.owslab.mailreceiver.model.DomainUnregister;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface DomainUnregisterDAO extends JpaRepository<DomainUnregister, Long>{
	public boolean existsByDomain(String domain);
	
	 @Transactional
	public Long deleteByDomain(String domain);
	 
	 @Transactional
	public void deleteByDomainIn(List<String> domains);
	 
	 public List<DomainUnregister> findByStatus(int status);
}
