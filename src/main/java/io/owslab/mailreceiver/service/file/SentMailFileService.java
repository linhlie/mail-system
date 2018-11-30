package io.owslab.mailreceiver.service.file;

import io.owslab.mailreceiver.dao.SentMailFilesDAO;
import io.owslab.mailreceiver.model.SentMailFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SentMailFileService {

    @Autowired
    SentMailFilesDAO sentMailFilesDAO;

    public void saveSentMailFile(SentMailFiles sentMailFile){
        sentMailFilesDAO.save(sentMailFile);
    }

    public void saveSentMailFiles(List<Long> uploadFileIds, long mailId){
        List<SentMailFiles> listSentMailFile = new ArrayList<>();
        for(long fileId : uploadFileIds){
            listSentMailFile.add(new SentMailFiles(mailId, fileId));
        }
        sentMailFilesDAO.save(listSentMailFile);
    }

    public List<SentMailFiles> getByMailId(long mailId){
        return sentMailFilesDAO.findBySentMailHistoriesId(mailId);
    }

    public List<SentMailFiles> getByFileId(long fileId){
        return sentMailFilesDAO.findByUploadFilesId(fileId);
    }
}
