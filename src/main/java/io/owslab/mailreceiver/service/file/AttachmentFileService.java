package io.owslab.mailreceiver.service.file;

import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.model.AttachmentFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttachmentFileService {
    @Autowired
    FileDAO fileDAO;

    public void save(List<AttachmentFile> attachmentFiles){
        fileDAO.save(attachmentFiles);
    }
}
