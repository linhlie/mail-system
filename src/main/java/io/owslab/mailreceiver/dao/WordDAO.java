package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.Word;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Transactional
public interface WordDAO extends CrudRepository<Word, Long> {
    List<Word> findByWord(String word);
    List<Word> findById(long id);
    List<Word> findByGroup(String group);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "SELECT * FROM words w WHERE w.group is not null order by w.group;",
            nativeQuery = true
    )
    List<Word> findWordsGroupNotNull();
}
