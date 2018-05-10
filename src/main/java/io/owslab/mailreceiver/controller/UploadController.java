package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dao.UploadFileDAO;
import io.owslab.mailreceiver.dto.DetailMailDTO;
import io.owslab.mailreceiver.form.RemoveUploadedFileForm;
import io.owslab.mailreceiver.form.SendMailForm;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.UploadFile;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.file.UploadFileService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by khanhlvb on 5/10/18.
 */
@Controller
public class UploadController {
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private UploadFileService uploadFileService;

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<?> singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<UploadFile> uploadFiles = uploadFileService.upload(file);
            result.setMsg("done");
            result.setList(uploadFiles);
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("singleFileUpload: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value="/removeUploadedFile", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> removeUploadedFile (@RequestParam(value = "fileId", required = true) long fileId){
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            uploadFileService.delete(fileId);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("removeUploadedFile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/removeUploadedFiles")
    @ResponseBody
    public ResponseEntity<?> removeUploadedFiles (
            Model model,
            @Valid @RequestBody RemoveUploadedFileForm removeUploadedFileForm) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            uploadFileService.delete(removeUploadedFileForm.getUploadAttachment());
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("removeUploadedFiles: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
