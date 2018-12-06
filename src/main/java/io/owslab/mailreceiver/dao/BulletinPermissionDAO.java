package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.BulletinBoard;
import io.owslab.mailreceiver.model.BulletinPermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BulletinPermissionDAO extends PagingAndSortingRepository<BulletinPermission, Long>  {
    List<BulletinPermission> findByBulletinBoardId(long bulletinBoardId);
    List<BulletinPermission> findByAccountIdAndCanView(long accountId, boolean canView);
    BulletinPermission findByAccountIdAndBulletinBoardId(long accountId, long bulletinBoardId);

    @Query(
            value = "SELECT bulletin_board_id FROM bulletin_permission group by bulletin_board_id",
            nativeQuery = true
    )
    List<Long> getBulletinBoardId();
}
