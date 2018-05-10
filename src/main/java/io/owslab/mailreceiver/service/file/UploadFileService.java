package io.owslab.mailreceiver.service.file;

import io.owslab.mailreceiver.dao.UploadFileDAO;
import io.owslab.mailreceiver.model.UploadFile;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by khanhlvb on 5/10/18.
 */
@Service
public class UploadFileService {

    @Autowired
    private EnviromentSettingService enviromentSettingService;
    @Autowired
    private UploadFileDAO uploadFileDAO;

    public List<UploadFile> upload(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("Please select a file to upload");
        }
        // Get the file and save it somewhere
        byte[] bytes = file.getBytes();
        String storagePath = enviromentSettingService.getStoragePath();
        String fileName = file.getOriginalFilename();
        Path path = Paths.get(storagePath + File.separator + fileName);
        Files.write(path, bytes);
        UploadFile uploadFile = new UploadFile(
                fileName,
                storagePath,
                new Date(),
                bytes.length
        );
        uploadFileDAO.save(uploadFile);
        List<UploadFile> uploadFiles = new ArrayList<>();
        uploadFiles.add(uploadFile);
        return uploadFiles;
    }

    public void delete(long fileId) {
        UploadFile uploadFile = uploadFileDAO.findOne(fileId);
        if (uploadFile != null) {
            File file = new File(uploadFile.getStoragePath() + File.separator + uploadFile.getFileName());
            file.delete();
            uploadFileDAO.delete(fileId);
        }
    }

    public void delete(List<Long> idList) {
        if(idList != null) {
            for(Long fileId : idList) {
                this.delete(fileId);
            }
        }
    }
}
