package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.BulletinBoard;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BulletinBoardDAO extends PagingAndSortingRepository<BulletinBoard, Long> {

    @Query(
            value = "SELECT * FROM bulletin_board where id = (SELECT max(id) from bulletin_board)",
            nativeQuery = true
    )
    BulletinBoard getBulletinBoardTop();
}