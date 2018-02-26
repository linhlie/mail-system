package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.ReplaceLetter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by khanhlvb on 2/22/18.
 */
@Transactional
public interface ReplaceLetterDAO extends CrudRepository<ReplaceLetter, Long> {
    List<ReplaceLetter> findByLetterAndPosition(String letter, int position);
    List<ReplaceLetter> findByHidden(boolean hidden);
    List<ReplaceLetter> findByReplaceNotAndPosition(int replace, int position);
}
