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
    List<Word> findByGroupWord(String group);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "SELECT * FROM words w WHERE w.group_word is not null order by w.group_word;",
            nativeQuery = true
    )
    List<Word> findWordsGroupNotNull();

    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE words SET group_word = NULL WHERE group_word = :groupName",
            nativeQuery = true
    )
    void deleteGroup(@Param("groupName") String groupName);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE words SET group_word = :newGroup WHERE group_word = :oldGroup",
            nativeQuery = true
    )
    void editGroup(@Param("oldGroup") String oldGroup, @Param("newGroup") String newGroup);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "SELECT count(w.group_word) FROM words w where w.group_word is  not null",
            nativeQuery = true
    )
    int countGroupWord();

    long countByGroupWordIsNotNull();
}
