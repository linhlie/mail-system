package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.AttachmentFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface FileDAO extends CrudRepository<AttachmentFile, Long> {

}
