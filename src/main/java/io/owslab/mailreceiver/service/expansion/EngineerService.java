package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.EngineerDAO;
import io.owslab.mailreceiver.dto.CSVEngineerDTO;
import io.owslab.mailreceiver.dto.CSVPartnerDTO;
import io.owslab.mailreceiver.dto.EngineerListItemDTO;
import io.owslab.mailreceiver.dto.ImportLogDTO;
import io.owslab.mailreceiver.exception.EngineerFieldValidationException;
import io.owslab.mailreceiver.exception.EngineerNotFoundException;
import io.owslab.mailreceiver.exception.PartnerNotFoundException;
import io.owslab.mailreceiver.form.EngineerFilterForm;
import io.owslab.mailreceiver.form.EngineerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Engineer;
import io.owslab.mailreceiver.service.file.UploadFileService;
import io.owslab.mailreceiver.utils.CSVBundle;
import io.owslab.mailreceiver.utils.Utils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by khanhlvb on 8/17/18.
 */
@Service
public class EngineerService {
    private static final Logger logger = LoggerFactory.getLogger(EngineerService.class);

    @Autowired
    private EngineerDAO engineerDAO;

    @Autowired
    private BusinessPartnerService partnerService;

    @Autowired
    private UploadFileService uploadFileService;

    public void delete(long id) {
        engineerDAO.delete(id);
    }

    public void add(EngineerForm form) throws PartnerNotFoundException, ParseException {
        BusinessPartner existPartner = partnerService.findOne(form.getPartnerId());
        if (existPartner == null) throw new PartnerNotFoundException("取引先が存在しません。");
        Engineer engineer = form.build();
        engineerDAO.save(engineer);
    }

    public void update(EngineerForm form, long id) throws EngineerNotFoundException, PartnerNotFoundException, ParseException {
        Engineer existEngineer = engineerDAO.findOne(id);
        if (existEngineer == null) throw new EngineerNotFoundException("技術者が存在しません");
        BusinessPartner existPartner = partnerService.findOne(form.getPartnerId());
        if (existPartner == null) throw new PartnerNotFoundException("取引先が存在しません。");
        Engineer engineer = form.build();
        engineer.setId(id);
        engineerDAO.save(engineer);
    }

    public List<EngineerListItemDTO> filter(EngineerFilterForm form, Timestamp now) {
        switch (form.getFilterType()) {
            case EngineerFilterForm.FilterType.ACTIVE:
                return getActive(now);
            case EngineerFilterForm.FilterType.INACTIVE:
                return getInactive(now);
            case EngineerFilterForm.FilterType.LAST_MONTH:
                return getByLastMonth(form, now);
            case EngineerFilterForm.FilterType.ALL:
            default:
                return getAll(now);
        }
    }

    public List<EngineerListItemDTO> getActive(Timestamp now) {
        List<EngineerListItemDTO> listItemDTOS = getAll(now);
        List<EngineerListItemDTO> result = new ArrayList<>();
        for (EngineerListItemDTO item : listItemDTOS) {
            if (item.isActive()) {
                result.add(item);
            }
        }
        return result;
    }

    public List<EngineerListItemDTO> getInactive(Timestamp now) {
        List<EngineerListItemDTO> listItemDTOS = getAll(now);
        List<EngineerListItemDTO> result = new ArrayList<>();
        for (EngineerListItemDTO item : listItemDTOS) {
            if (!item.isActive()) {
                result.add(item);
            }
        }
        return result;
    }

    public List<EngineerListItemDTO> getAll(Timestamp now) {
        List<Engineer> engineers = (List<Engineer>) engineerDAO.findAll();
        return build(engineers, now);
    }

    public List<Engineer> getList() {
        List<Engineer> engineers = (List<Engineer>) engineerDAO.findAll();
        return engineers;
    }

    public List<EngineerListItemDTO> getByLastMonth(EngineerFilterForm form, Timestamp now) {
        Date startDate = new Date(form.getFilterDate());
        startDate = Utils.atStartOfDay(startDate);
        Date endDate = Utils.addMonthsToDate(startDate, 1);
        endDate = Utils.addDayToDate(endDate, -1);
        endDate = Utils.atEndOfDay(endDate);
        List<Engineer> engineers = engineerDAO.findByProjectPeriodEndBetween(startDate.getTime(), endDate.getTime());
        return build(engineers, now);
    }

    private List<EngineerListItemDTO> build(List<Engineer> engineers, Timestamp now) {
        List<EngineerListItemDTO> engineerDTOs = new ArrayList<>();
        for(Engineer engineer : engineers){
            BusinessPartner partner = partnerService.findOne(engineer.getPartnerId());
            if(partner != null) {
                engineerDTOs.add(new EngineerListItemDTO(engineer, partner.getName(), now));
            }
        }
        return engineerDTOs;
    }

    public List<EngineerForm> getById(long id) {
        List<EngineerForm> result = new ArrayList<>();
        Engineer engineer = engineerDAO.findOne(id);
        if(engineer != null) {
            result.add(new EngineerForm(engineer));
        }
        return result;
    }

    public void autoExtend() {
        List<Engineer> autoExtends = engineerDAO.findByAutoExtend(true);
        long current = System.currentTimeMillis();
        for(Engineer engineer: autoExtends) {
            long projectPeriodEnd = engineer.getProjectPeriodEnd();
            if(projectPeriodEnd < current) {
                Date startDate = new Date(projectPeriodEnd);
                startDate = Utils.addDayToDate(startDate, 1);
                startDate = Utils.atStartOfDay(startDate);
                Date endDate = Utils.addMonthsToDate(startDate, engineer.getExtendMonth());
                endDate = Utils.addDayToDate(endDate, -1);
                endDate = Utils.atEndOfDay(endDate);
                engineer.setProjectPeriodEnd(endDate.getTime());
                engineerDAO.save(engineer);
            }
        }
    }

    public List<ImportLogDTO> importEngineer(MultipartFile multipartFile, boolean skipHeader, boolean deleteOld) throws Exception {

        File file = null;

        List<ImportLogDTO> importLogs = new ArrayList<ImportLogDTO>();
        List<CSVEngineerDTO> engineerDTOS = new ArrayList<>();

        try {
            file = uploadFileService.saveToUpload(multipartFile);
            String encoding = UniversalDetector.detectCharset(file);
            if(encoding == null) {
                encoding = "Shift-JIS";
            }
            try(ICsvBeanReader beanReader = new CsvBeanReader(new BufferedReader(
                    new InputStreamReader(new FileInputStream(file),
                            Charset.forName(encoding))), CsvPreference.STANDARD_PREFERENCE))
            {
                // the header elements are used to map the values to the bean
                final String[] headers = new String[]{ "name", "kanaName", "mailAddress", "employmentStatus", "partnerCode", "projectPeriodStart", "projectPeriodEnd", "autoExtend", "extendMonth", "matchingWord", "notGoodWord", "monetaryMoney", "stationLine", "stationNearest", "commutingTime" };

                CSVEngineerDTO engineerDTO;
                if(skipHeader) {
                    beanReader.getHeader(skipHeader);
                }
                while ((engineerDTO = beanReader.read(CSVEngineerDTO.class, headers)) != null) {
                    engineerDTOS.add(engineerDTO);
                }
                if(deleteOld) {
                    engineerDAO.deleteAll();
                }
                for(int line = 0; line < engineerDTOS.size(); line++) {
                    CSVEngineerDTO csvEngineerDTO = engineerDTOS.get(line);
                    String name = csvEngineerDTO.getName();
                    String kanaName = csvEngineerDTO.getKanaName();
                    String employmentStatus = csvEngineerDTO.getEmploymentStatus();
                    String partnerCode = csvEngineerDTO.getPartnerCode();
                    String projectPeriodStart = csvEngineerDTO.getProjectPeriodStart();
                    String projectPeriodEnd = csvEngineerDTO.getProjectPeriodEnd();
                    if(name == null || kanaName == null || employmentStatus == null
                            || partnerCode == null || projectPeriodStart == null || projectPeriodEnd == null) {
                        String type = "【技術者インポート】";
                        int lineIndex = skipHeader ? line + 2 : line + 1;
                        String info = "技術者名 " + Objects.toString(name, "");
                        List<String> missingList = new ArrayList<>();
                        if(name == null) {
                            missingList.add("技術者名がありません");
                        }
                        if(kanaName == null) {
                            missingList.add("カナ氏名がありません");
                        }
                        if(employmentStatus == null) {
                            missingList.add("雇用形態がありません");
                        }
                        if(partnerCode == null) {
                            missingList.add("所属識別IDがありません");
                        }
                        if(projectPeriodStart == null) {
                            missingList.add("案件期間「開始」がありません");
                        }
                        if(projectPeriodEnd == null) {
                            missingList.add("案件期間「終了」がありません");
                        }
                        String detail = String.join("、", missingList) + "。";
                        ImportLogDTO importLog = new ImportLogDTO(type, lineIndex, info, detail);
                        importLogs.add(importLog);
                        continue;
                    }
                    BusinessPartner existPartner = partnerService.findOneByPartnerCode(partnerCode);
                    if(existPartner != null) {
                        try {
                            Engineer engineer = csvEngineerDTO.build(existPartner);
                            engineerDAO.save(engineer);
                        } catch (EngineerFieldValidationException efve) {
                            String type = "【技術者インポート】";
                            int lineIndex = skipHeader ? line + 2 : line + 1;
                            String info = "技術者名 " + Objects.toString(name, "");
                            String detail = efve.getMessage();
                            ImportLogDTO importLog = new ImportLogDTO(type, lineIndex, info, detail);
                            importLogs.add(importLog);
                        }
                    } else {
                        String type = "【技術者インポート】";
                        int lineIndex = skipHeader ? line + 2 : line + 1;
                        String info = "技術者名 " + Objects.toString(name, "");
                        String detail = "所属識別ID「" + partnerCode + "」はは存在しないIDです。";
                        ImportLogDTO importLog = new ImportLogDTO(type, lineIndex, info, detail);
                        importLogs.add(importLog);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            if(file != null) {
                file.delete();
            }
            throw new Exception("技術者のインポートに失敗しました");
        }
        if(file != null) {
            file.delete();
        }

        return importLogs;
    }

    public CSVBundle<CSVEngineerDTO> export() {
        CSVBundle<CSVEngineerDTO> csvBundle = new CSVBundle<CSVEngineerDTO>();
        csvBundle.setFileName("技術者.csv");
        String[] csvHeader = { "技術者名", "カナ氏名", "メールアドレス", "雇用形態",
                "所属企業", "案件期間 開始", "案件期間 終了", "延長", "延長期間", "マッチングワード", "NGワード", "単金", "最寄り駅 線", "最寄り駅 駅", "通勤時間" };
        String[] keys = { "name", "kanaName", "mailAddress", "employmentStatus", "partnerCode", "projectPeriodStart", "projectPeriodEnd", "autoExtend", "extendMonth", "matchingWord", "notGoodWord", "monetaryMoney", "stationLine", "stationNearest", "commutingTime" };
        csvBundle.setHeaders(csvHeader);
        csvBundle.setKeys(keys);
        List<CSVEngineerDTO> data = getEngineerListToExport();
        csvBundle.setData(data);
        return csvBundle;
    }

    private List<CSVEngineerDTO> getEngineerListToExport() {
        List<CSVEngineerDTO> result = new ArrayList<>();
        List<Engineer> engineers = getList();
        for(Engineer engineer : engineers) {
            BusinessPartner partner = partnerService.findOne(engineer.getPartnerId());
            if(partner != null) {
                result.add(new CSVEngineerDTO(engineer, partner));
            }
        }
        return result;
    }
}
