package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.BulletinBoard;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BulletinBoardDAO extends PagingAndSortingRepository<BulletinBoard, Long> {

    List<BulletinBoard> findByTabNumberGreaterThan(long tabNumber);
    List<BulletinBoard> findByIdIn(List<Long> Ids);
    BulletinBoard findTopByTabNumber(Long TagNumber);

    @Query(
            value = "SELECT * FROM bulletin_board where id = (SELECT max(id) from bulletin_board)",
            nativeQuery = true
    )
    BulletinBoard getBulletinBoardTop();

    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE bulletin_board e SET e.tab_number = e.tab_number-1 WHERE e.tab_number>:tabNumber",
            nativeQuery = true
    )
    void downTabNumber(@Param("tabNumber") long tabNumber);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE bulletin_board e SET e.tab_number = e.tab_number+1 WHERE e.tab_number>=:startPosition AND e.tab_number<:endPosition",
            nativeQuery = true
    )
    void upPosition(@Param("startPosition") long startPosition, @Param("endPosition") long endPosition);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE bulletin_board e SET e.tab_number = e.tab_number-1 WHERE e.tab_number>:startPosition AND e.tab_number<=:endPosition",
            nativeQuery = true
    )
    void downPosition(@Param("startPosition") long startPosition, @Param("endPosition") long endPosition);

}