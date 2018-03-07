package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.FuzzyWord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Transactional
public interface FuzzyWordDAO extends CrudRepository<FuzzyWord, Long> {
    @Query(
            value = "select * from Fuzzy_Words fw where fw.word_id=:wordId and fw.with_word_id=:withWordId",
            nativeQuery = true
    )
    List<FuzzyWord> findByWordIdAndWithWordId(@Param("wordId") long wordId, @Param("withWordId") long withWordId);

    @Query(
            value = "select * from Fuzzy_Words fw where fw.word_id=:wordId and fw.fuzzy_type=:fuzzyType",
            nativeQuery = true
    )
    List<FuzzyWord> findByWordIdAndFuzzyType(@Param("wordId") long wordId, @Param("fuzzyType") int fuzzyType);

    @Query(
            value = "select * from Fuzzy_Words fw where fw.with_word_id=:withWordId and fw.fuzzy_type=:fuzzyType",
            nativeQuery = true
    )
    List<FuzzyWord> findByWithWordIdAndFuzzyType(@Param("withWordId") long withWordId, @Param("fuzzyType") int fuzzyType);
}
