package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dao.UploadFileDAO;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.UploadFile;
import io.owslab.mailreceiver.utils.MediaTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * Created by khanhlvb on 6/1/18.
 */
@Controller
public class DownloadController {

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private UploadFileDAO uploadFileDAO;

    //    @RequestMapping(value = "/download/{path}/{fileName}", method = RequestMethod.GET)
//    public void downloadFile3(HttpServletResponse response, @PathVariable("path") String path, @PathVariable("fileName") String fileName) throws IOException {
//
//        System.out.println("path: " + path);
//        System.out.println("fileName: " + fileName);
////        String realPath = new String(Base64.getDecoder().decode(path));
////        String realFileName = new String(Base64.getDecoder().decode(fileName));
//        String realPath = path;
//        String realFileName = fileName;
//        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, realFileName);
//
//        File file = new File(realPath + "/" + realFileName);
//
//        // Content-Type
//        // application/pdf
//        response.setContentType(mediaType.getType());
//
//        // Content-Disposition
//        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
//
//        // Content-Length
//        response.setContentLength((int) file.length());
//
//        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
//        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());
//
//        byte[] buffer = new byte[1024];
//        int bytesRead = 0;
//        while ((bytesRead = inStream.read(buffer)) != -1) {
//            outStream.write(buffer, 0, bytesRead);
//        }
//        outStream.flush();
//        inStream.close();
//    }

    @RequestMapping(value = "/download/{digest}/{fileName}", method = RequestMethod.GET)
    public void downloadFile3(
            HttpServletResponse response,
            @PathVariable("digest") String digest,
            @PathVariable("fileName") String fileName
    ) throws IOException {

        String decodedDigest = new String(DatatypeConverter.parseHexBinary(digest));
        String[] digestParts = decodedDigest.split(File.separator, 2);
        if(digestParts.length == 0) return;

        long id = Long.parseLong(digestParts[0]);
        AttachmentFile attachmentFile = fileDAO.findOne(id);
        if(attachmentFile == null) {
            //TODO: response error cannot download;
            return;
        }
        String realPath = attachmentFile.getStoragePath();
        String realFileName = attachmentFile.getFileName();
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, realFileName);

        File file = new File(realPath);

        // Content-Type
        // application/pdf
        response.setContentType(mediaType.getType());

        // Content-Disposition
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(realFileName, "UTF-8"));

        // Content-Length
        response.setContentLength((int) file.length());
        response.setCharacterEncoding("UTF-8");

        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        outStream.flush();
        inStream.close();
    }

    @RequestMapping(value = "/download/fileUpload/{digest}/{fileName}", method = RequestMethod.GET)
    public void downloadFile4(
            HttpServletResponse response,
            @PathVariable("digest") String digest,
            @PathVariable("fileName") String fileName
    ) throws IOException {

        String decodedDigest = new String(DatatypeConverter.parseHexBinary(digest));
        String[] digestParts = decodedDigest.split(File.separator, 2);
        if(digestParts.length == 0) return;

        long id = Long.parseLong(digestParts[0]);
        UploadFile uploadFile = uploadFileDAO.findOne(id);
        if(uploadFile == null) {
            //TODO: response error cannot download;
            return;
        }
        String realPath = uploadFile.getStoragePath();
        String realFileName = uploadFile.getFileName();
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, realFileName);

        File file = new File(realPath+"/"+realFileName);

        // Content-Type
        // application/pdf
        response.setContentType(mediaType.getType());

        // Content-Disposition
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(realFileName, "UTF-8"));

        // Content-Length
        response.setContentLength((int) file.length());
        response.setCharacterEncoding("UTF-8");

        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        outStream.flush();
        inStream.close();
    }

}
