package io.owslab.mailreceiver.dao;


import io.owslab.mailreceiver.model.ReplaceUnit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ReplaceUnitDAO extends CrudRepository<ReplaceUnit, Long> {
    List<ReplaceUnit> findByUnit(String unit);
}
