package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.SentMailFiles;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SentMailFilesDAO extends PagingAndSortingRepository<SentMailFiles, Long> {
}
