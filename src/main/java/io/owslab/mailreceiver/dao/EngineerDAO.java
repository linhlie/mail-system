package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Engineer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by khanhlvb on 8/17/18.
 */

@Transactional
public interface EngineerDAO extends PagingAndSortingRepository<Engineer, Long> {
    List<Engineer> findByAutoExtend(boolean autoExtend);
    List<Engineer> findByProjectPeriodEndBetween(long fromTime, long toTime);
}
