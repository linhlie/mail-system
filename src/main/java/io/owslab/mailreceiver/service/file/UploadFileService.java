package io.owslab.mailreceiver.service.file;

import io.owslab.mailreceiver.dao.UploadFileDAO;
import io.owslab.mailreceiver.dto.FileDTO;
import io.owslab.mailreceiver.model.SentMailFiles;
import io.owslab.mailreceiver.model.UploadFile;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by khanhlvb on 5/10/18.
 */
@Service
public class UploadFileService {

    @Autowired
    private EnviromentSettingService enviromentSettingService;
    @Autowired
    private UploadFileDAO uploadFileDAO;

    @Autowired
    private SentMailFileService sentMailFileService;

    public File saveToUpload(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("Please select a file to upload");
        }
        byte[] bytes = file.getBytes();
        String storagePath = enviromentSettingService.getStoragePath();
        storagePath = normalizeDirectoryPath(storagePath) + File.separator + "upload";
        File saveDirectory = new File(storagePath);
        if (!saveDirectory.exists()){
            saveDirectory.mkdir();
        }
        storagePath = normalizeDirectoryPath(storagePath) + File.separator + getRandomFolderName();
        saveDirectory = new File(storagePath);
        if (!saveDirectory.exists()){
            saveDirectory.mkdir();
        }
        String fileName = getUniqueFileName();
        Path path = Paths.get(storagePath + File.separator + fileName);
        Path savePath = Files.write(path, bytes);
        return savePath.toFile();
    }

    public List<UploadFile> upload(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("Please select a file to upload");
        }
        // Get the file and save it somewhere
        byte[] bytes = file.getBytes();
        String storagePath = enviromentSettingService.getStoragePath();
        storagePath = normalizeDirectoryPath(storagePath) + File.separator + "upload";
        File saveDirectory = new File(storagePath);
        if (!saveDirectory.exists()){
            saveDirectory.mkdir();
        }
        storagePath = normalizeDirectoryPath(storagePath) + File.separator + getRandomFolderName();
        saveDirectory = new File(storagePath);
        if (!saveDirectory.exists()){
            saveDirectory.mkdir();
        }
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
    
    private String normalizeDirectoryPath(String path){
        if (path != null && path.length() > 0 && path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public void delete(long fileId) {
        UploadFile uploadFile = uploadFileDAO.findOne(fileId);
        if (uploadFile != null) {
            String fileFullName = uploadFile.getStoragePath() + File.separator + uploadFile.getFileName();
            Path path = Paths.get(fileFullName);
            try {
                Files.delete(path);
                Path parent = path.getParent();
                if(Files.isDirectory(parent)) {
                    if(Files.list(parent).count() == 0) {
                        Files.delete(parent);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public static String getRandomFolderName(){
        return Long.toString(System.currentTimeMillis()) + "" + randomWithRange(1, 100);
    }

    public static int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public static String getUniqueFileName() {
        return System.currentTimeMillis() + "" + UUID.randomUUID().toString();
    }

    public List<FileDTO> getFileUpload(long mailId){
        List<SentMailFiles> listFile = sentMailFileService.getByMailId(mailId);
        List<FileDTO> listFileUpload = new ArrayList<>();
        for(SentMailFiles sentFile : listFile){
            UploadFile uploadFile = uploadFileDAO.findOne(sentFile.getUploadFilesId());
            if(uploadFile != null){
                listFileUpload.add(new FileDTO(uploadFile));
            }
        }
        return listFileUpload;
    }
}
