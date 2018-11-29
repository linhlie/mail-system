package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.SentMailFiles;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SentMailFilesDAO extends PagingAndSortingRepository<SentMailFiles, Long> {
    List<SentMailFiles> findByUploadFilesId(long upLoadFileId);
    List<SentMailFiles> findBySentMailHistoriesId(long sentMailHistoriesId);
}
