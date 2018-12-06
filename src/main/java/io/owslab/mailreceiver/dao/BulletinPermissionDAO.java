package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.BulletinPermission;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BulletinPermissionDAO extends PagingAndSortingRepository<BulletinPermission, Long>  {
    List<BulletinPermission> findByBulletinBoardId(long bulletinBoardId);
    List<BulletinPermission> findByAccountIdAndCanView(long accountId, boolean canView);
}
