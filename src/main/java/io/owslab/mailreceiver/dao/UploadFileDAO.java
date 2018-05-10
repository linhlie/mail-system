package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.UploadFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Transactional
public interface UploadFileDAO extends CrudRepository<UploadFile, Long> {
    List<UploadFile> findByIdIn(List<Long> idList);
}
