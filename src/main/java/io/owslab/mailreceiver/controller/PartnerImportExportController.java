package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import io.owslab.mailreceiver.utils.CSVBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
        } else {
            csvBundle = partnerService.export();
        }
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(csvBundle.getFileName(), "UTF-8"));
        response.setCharacterEncoding("UTF-8");
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
}
