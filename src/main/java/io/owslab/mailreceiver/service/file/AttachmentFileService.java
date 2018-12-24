package io.owslab.mailreceiver.service.file;

import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dto.FileDTO;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.SentMailFiles;
import io.owslab.mailreceiver.model.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttachmentFileService {
    @Autowired
    FileDAO fileDAO;

    public void save(List<AttachmentFile> attachmentFiles){
        fileDAO.save(attachmentFiles);
    }

    public List<FileDTO> getFileByMessageId(String messageId){
        List<AttachmentFile> listFile = fileDAO.findByMessageIdAndDeleted(messageId, false);
        List<FileDTO> listFileAttachment = new ArrayList<>();
        for(AttachmentFile file : listFile){
            listFileAttachment.add(new FileDTO(file));
        }
        return listFileAttachment;
    }
}
