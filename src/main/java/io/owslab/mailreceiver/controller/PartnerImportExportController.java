package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.ImportLogDTO;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.BusinessPartnerGroup;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import io.owslab.mailreceiver.service.expansion.EngineerService;
import io.owslab.mailreceiver.service.expansion.PeopleInChargePartnerService;
import io.owslab.mailreceiver.utils.CSVBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by khanhlvb on 8/24/18.
 */
@Controller
@RequestMapping("/expansion/")
public class PartnerImportExportController {
    private static final Logger logger = LoggerFactory.getLogger(PartnerImportExportController.class);

    @Autowired
    private BusinessPartnerService partnerService;

    @Autowired
    private EngineerService engineerService;

    @Autowired
    private PeopleInChargePartnerService peopleInChargePartnerService;

    @RequestMapping(value = { "/partnerImportExport" }, method = RequestMethod.GET)
    public String getPartnerImportExport(Model model, HttpServletRequest request) {
        return "expansion/partnerImportExport";
    }

    @RequestMapping(value = "/exportCSV", method = RequestMethod.GET)
    public void exportPartnerCSV(HttpServletResponse response, @RequestParam(value = "header") boolean header, @RequestParam(value = "type") String type) throws IOException {
        response.setContentType("text/csv");
        CSVBundle csvBundle;
        if(type.equals("groupPartner")) {
            csvBundle = partnerService.exportGroups();
        } else if (type.equals("engineer")) {
            csvBundle = engineerService.export();
        }else {
            csvBundle = partnerService.export();
        }
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(csvBundle.getFileName(), "UTF-8"));
        response.setCharacterEncoding("SHIFT_JIS");
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        if(header) {
            String[] headers = csvBundle.getHeaders();
            csvWriter.writeHeader(headers);
        }
        String[] keys = csvBundle.getKeys();
        List data = csvBundle.getData();
        for(int i = 0; i < data.size(); i++) {
            csvWriter.write(data.get(i), keys);
        }
        csvWriter.close();
    }

    @PostMapping("/importPartner")
    @ResponseBody
    public ResponseEntity<?> importPartner(@RequestParam("file") MultipartFile file, @RequestParam(value = "header") boolean header, @RequestParam(value = "deleteOld") boolean deleteOld) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<ImportLogDTO> importLogs = partnerService.importPartner(file, header, deleteOld);
            result.setMsg("done");
            result.setList(importLogs);
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("importPartner: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/importPartnerGroup")
    @ResponseBody
    public ResponseEntity<?> importPartnerGroup(@RequestParam("file") MultipartFile file, @RequestParam(value = "header") boolean header, @RequestParam(value = "deleteOld") boolean deleteOld) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<ImportLogDTO> importLogs = partnerService.importPartnerGroup(file, header, deleteOld);
            result.setMsg("done");
            result.setList(importLogs);
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("importPartnerGroup: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/importEngineer")
    @ResponseBody
    public ResponseEntity<?> importEngineer(@RequestParam("file") MultipartFile file, @RequestParam(value = "header") boolean header, @RequestParam(value = "deleteOld") boolean deleteOld) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<ImportLogDTO> importLogs = engineerService.importEngineer(file, header, deleteOld);
            result.setMsg("done");
            result.setList(importLogs);
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("importEngineer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/importPeopleInChargePartners")
    @ResponseBody
    public ResponseEntity<?> importPeopleInChargePartner(@RequestParam("file") MultipartFile file, @RequestParam(value = "header") boolean header, @RequestParam(value = "deleteOld") boolean deleteOld) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<ImportLogDTO> importLogs = peopleInChargePartnerService.importPeopleinChargePartner(file, header, deleteOld);
            result.setMsg("done");
            result.setList(importLogs);
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("importPeopleInChargePartner: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
