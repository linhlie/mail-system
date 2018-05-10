package io.owslab.mailreceiver.dao;

import io.owslab.mailreceiver.model.AttachmentFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Transactional
public interface FileDAO extends CrudRepository<AttachmentFile, Long> {
    List<AttachmentFile> findByMessageIdAndDeleted(String messageId, boolean deleted);
    List<AttachmentFile> findByIdInAndDeleted(List<Long> idList, boolean deleted);
}
