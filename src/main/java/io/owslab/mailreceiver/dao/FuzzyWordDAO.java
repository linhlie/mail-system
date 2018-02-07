package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.FuzzyWord;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Transactional
public interface FuzzyWordDAO extends CrudRepository<FuzzyWord, Long> {

}
