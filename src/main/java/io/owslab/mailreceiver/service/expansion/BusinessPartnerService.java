package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.controller.PartnerImportExportController;
import io.owslab.mailreceiver.dao.BusinessPartnerDAO;
import io.owslab.mailreceiver.dao.BusinessPartnerGroupDAO;
import io.owslab.mailreceiver.dto.CSVPartnerDTO;
import io.owslab.mailreceiver.dto.CSVPartnerGroupDTO;
import io.owslab.mailreceiver.dto.ImportLogDTO;
import io.owslab.mailreceiver.exception.PartnerCodeException;
import io.owslab.mailreceiver.form.PartnerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.BusinessPartnerGroup;
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
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by khanhlvb on 8/13/18.
 */
@Service
public class BusinessPartnerService {
    private static final Logger logger = LoggerFactory.getLogger(BusinessPartnerService.class);

    @Autowired
    private BusinessPartnerDAO partnerDAO;

    @Autowired
    private BusinessPartnerGroupDAO partnerGroupDAO;

    @Autowired
    private UploadFileService uploadFileService;

    public List<BusinessPartner> getAll() {
        return (List<BusinessPartner>) partnerDAO.findAll();
    }

    public List<BusinessPartnerGroup> getAllGroup() {
        return (List<BusinessPartnerGroup>) partnerGroupDAO.findAll();
    }

    //TODO need to be transaction
    public void add(PartnerForm form) throws PartnerCodeException {
        BusinessPartner.Builder builder = form.getBuilder();
        String partnerCode = builder.getPartnerCode();
        BusinessPartner existPartner = findOneByPartnerCode(partnerCode);
        if(existPartner != null) throw new PartnerCodeException("識別IDは既に存在します。");
        BusinessPartner addedPartner = partnerDAO.save(builder.build());
        List<Long> groupAddIds = form.getGroupAddIds();
        saveGroupList(addedPartner, groupAddIds);
    }

    //TODO need to be transaction
    public void update(PartnerForm form, long id) throws PartnerCodeException {
        BusinessPartner.Builder builder = form.getBuilder();
        builder.setId(id);
        String partnerCode = builder.getPartnerCode();
        BusinessPartner partner = findOne(id);
        if(partner == null) throw new PartnerCodeException("取引先は存在しません");
        BusinessPartner existPartner = findOneByPartnerCode(partnerCode);
        if(existPartner != null) {
            long existPartnerId = existPartner.getId();
            if(existPartnerId != id) throw new PartnerCodeException("識別IDは既に存在します。");
        }
        BusinessPartner updatedPartner = partnerDAO.save(builder.build());
        List<Long> groupRemoveIds = form.getGroupRemoveIds();
        List<Long> groupAddIds = form.getGroupAddIds();
        deleteGroupList(updatedPartner, groupRemoveIds);
        saveGroupList(updatedPartner, groupAddIds);
    }

    public BusinessPartner findOneByPartnerCode(String partnerCode){
        List<BusinessPartner> partners = partnerDAO.findByPartnerCode(partnerCode);
        return partners.size() > 0 ? partners.get(0) : null;
    }

    public BusinessPartner findOne(long id){
        return partnerDAO.findOne(id);
    }

    public void delete(long id){
        partnerDAO.delete(id);
    }

    public List<BusinessPartnerGroup> findByPartner(long partnerId){
        return partnerGroupDAO.findByPartnerId(partnerId);
    }

    private void deleteGroupList(BusinessPartner partner, List<Long> groupWithPartnerIds) {
        List<BusinessPartnerGroup> partnerGroups = new ArrayList<>();
        for(Long groupWithPartnerId : groupWithPartnerIds) {
            List<BusinessPartnerGroup> groupWithPartners = partnerGroupDAO.findByPartnerIdAndWithPartnerId(partner.getId(), groupWithPartnerId);
            if(groupWithPartners.size() > 0) {
                partnerGroups.add(groupWithPartners.get(0));
            }
            groupWithPartners = partnerGroupDAO.findByPartnerIdAndWithPartnerId(groupWithPartnerId, partner.getId());
            if(groupWithPartners.size() > 0) {
                partnerGroups.add(groupWithPartners.get(0));
            }
        }
        partnerGroupDAO.delete(partnerGroups);
    }

    private void saveGroupList(BusinessPartner partner, List<Long> groupWithPartnerIds) {
        List<BusinessPartnerGroup> partnerGroups = new ArrayList<>();
        for(Long groupWithPartnerId : groupWithPartnerIds) {
            BusinessPartner groupWithPartner = findOne(groupWithPartnerId);
            if(groupWithPartner != null) {
                partnerGroups.add(new BusinessPartnerGroup(partner, groupWithPartner));
                partnerGroups.add(new BusinessPartnerGroup(groupWithPartner, partner));
            }
        }
        partnerGroupDAO.save(partnerGroups);
    }

    public CSVBundle<CSVPartnerDTO> export() {
        CSVBundle<CSVPartnerDTO> csvBundle = new CSVBundle<CSVPartnerDTO>();
        csvBundle.setFileName("取引先.csv");
        String[] csvHeader = { "取引先名称", "カナ名称", "会社形態", "前株後株",
                "識別ID", "ドメイン", "ドメイン", "ドメイン", "自社" };
        String[] keys = {"name", "kanaName", "companyType", "stockShare", "partnerCode", "domain1", "domain2", "domain3", "ourCompany"};
        csvBundle.setHeaders(csvHeader);
        csvBundle.setKeys(keys);
        List<CSVPartnerDTO> data = getPartnerListToExport();
        csvBundle.setData(data);
        return csvBundle;
    }

    public CSVBundle<CSVPartnerGroupDTO> exportGroups() {
        CSVBundle<CSVPartnerGroupDTO> csvBundle = new CSVBundle<CSVPartnerGroupDTO>();
        csvBundle.setFileName("取引先グループ.csv");
        String[] csvHeader = { "取引先名", "識別ID", "取引グループ 取引先名", "取引グループ 識別ID" };
        String[] keys = {"partnerName", "partnerCode", "withPartnerName", "withPartnerCode" };
        csvBundle.setHeaders(csvHeader);
        csvBundle.setKeys(keys);
        List<CSVPartnerGroupDTO> data = getPartnerGroupListToExport();
        csvBundle.setData(data);
        return csvBundle;
    }

    private List<CSVPartnerDTO> getPartnerListToExport() {
        List<CSVPartnerDTO> result = new ArrayList<>();
        List<BusinessPartner> partners = getAll();
        for(BusinessPartner partner : partners) {
            result.add(new CSVPartnerDTO(partner));
        }
        return result;
    }

    private List<CSVPartnerGroupDTO> getPartnerGroupListToExport() {
        HashMap<String, CSVPartnerGroupDTO> result = new HashMap<>();
        List<BusinessPartnerGroup> groups = getAllGroup();
        for(BusinessPartnerGroup group : groups) {
            String key = generateKey(group);
            if(!result.containsKey(key)) {
                result.put(key, new CSVPartnerGroupDTO(group));
            }
        }
        return new ArrayList<CSVPartnerGroupDTO>(result.values());
    }

    private String generateKey(BusinessPartnerGroup group) {
        BusinessPartner partner = group.getPartner();
        BusinessPartner withPartner = group.getWithPartner();
        long partnerId = partner.getId();
        long withPartnerId = withPartner.getId();
        if(partnerId < withPartnerId) {
            return partnerId + "-" + withPartnerId;
        }
        return withPartnerId + "-" + partnerId;
    }

    public List<ImportLogDTO> importPartner(MultipartFile multipartFile, boolean skipHeader) throws Exception {

        List<ImportLogDTO> importLogs = new ArrayList<ImportLogDTO>();
        List<BusinessPartner> partners = new ArrayList<>();
        File file = null;
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
                final String[] headers = new String[]{"name", "kanaName", "companyType", "stockShare", "partnerCode", "domain1", "domain2", "domain3", "ourCompany"};

                CSVPartnerDTO partnerDTO;
                if(skipHeader) {
                    beanReader.getHeader(skipHeader);
                }
                while ((partnerDTO = beanReader.read(CSVPartnerDTO.class, headers)) != null) {
                    partners.add(partnerDTO.build());
                }
                for(int line = 0; line < partners.size(); line++) {
                    BusinessPartner partner = partners.get(line);
                    String partnerName = partner.getName();
                    String partnerKanaName = partner.getKanaName();
                    String partnerCode = partner.getPartnerCode();
                    String domain1 = partner.getDomain1();
                    String domain2 = partner.getDomain2();
                    String domain3 = partner.getDomain3();
                    if(partnerName == null || partnerKanaName == null || partnerCode == null
                        || (domain1 == null && domain2 == null && domain3 == null)) {
                        String type = "【取引先インポート】";
                        int lineIndex = skipHeader ? line + 2 : line + 1;
                        String info = "取引先名 " + Objects.toString(partnerName, "");
                        List<String> missingList = new ArrayList<>();
                        if(partnerName == null) {
                            missingList.add("取引先名称がありません");
                        }
                        if(partnerKanaName == null) {
                            missingList.add("カナ名称がありません");
                        }
                        if(partnerCode == null) {
                            missingList.add("識別IDがありません");
                        }
                        if(domain1 == null && domain2 == null && domain3 == null) {
                            missingList.add("ドメインがありません");
                        }
                        String detail = String.join("、", missingList) + "。";
                        ImportLogDTO importLog = new ImportLogDTO(type, lineIndex, info, detail);
                        importLogs.add(importLog);
                        continue;
                    }
                    BusinessPartner existPartner = findOneByPartnerCode(partner.getPartnerCode());
                    if(existPartner == null) {
                        partnerDAO.save(partner);
                    } else {
                        String type = "【取引先インポート】";
                        int lineIndex = skipHeader ? line + 2 : line + 1;
                        String info = "取引先名 " + partner.getName();
                        String detail = "識別ID「" + partner.getPartnerCode() + "」は既に使用されているためインポートしません。";
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
            throw new Exception("取引先のインポートに失敗しました");
        }
        if(file != null) {
            file.delete();
        }

        return importLogs;
    }

    public List<ImportLogDTO> importPartnerGroup(MultipartFile multipartFile, boolean skipHeader) throws Exception {

        File file = null;

        List<ImportLogDTO> importLogs = new ArrayList<ImportLogDTO>();
        List<CSVPartnerGroupDTO> partnerGroups = new ArrayList<>();

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
                final String[] headers = new String[]{ "partnerName", "partnerCode", "withPartnerName", "withPartnerCode" };

                CSVPartnerGroupDTO partnerGroupDTO;
                if(skipHeader) {
                    beanReader.getHeader(skipHeader);
                }
                while ((partnerGroupDTO = beanReader.read(CSVPartnerGroupDTO.class, headers)) != null) {
                    partnerGroups.add(partnerGroupDTO);
                }
                for(int line = 0; line < partnerGroups.size(); line++) {
                    CSVPartnerGroupDTO partnerGroup = partnerGroups.get(line);
                    String partnerCode = partnerGroup.getPartnerCode();
                    String withPartnerCode = partnerGroup.getWithPartnerCode();
                    if(partnerCode == null || withPartnerCode == null) {
                        String type = "【取引先グループインポート】";
                        int lineIndex = skipHeader ? line + 2 : line + 1;
                        String info = "取引先名 " + partnerGroup.getPartnerName();
                        String detail;
                        if(partnerCode == null && withPartnerCode != null) {
                            detail = "識別IDがありません。";
                        } else if (partnerCode != null && withPartnerCode == null) {
                            detail = "取引グループ識別IDがありません。";
                        } else {
                            detail = "識別IDがありません、取引グループ識別IDがありません。";
                        }
                        ImportLogDTO importLog = new ImportLogDTO(type, lineIndex, info, detail);
                        importLogs.add(importLog);
                        continue;
                    }
                    BusinessPartner partner = findOneByPartnerCode(partnerGroup.getPartnerCode());
                    BusinessPartner withPartner = findOneByPartnerCode(partnerGroup.getWithPartnerCode());
                    if(partner != null && withPartner != null) {
                        List<BusinessPartnerGroup> existPartnerGroups = partnerGroupDAO.findByPartnerIdAndWithPartnerId(partner.getId(), withPartner.getId());
                        if(existPartnerGroups.size() == 0) {
                            List<BusinessPartnerGroup> saveList = new ArrayList<>();
                            saveList.add(new BusinessPartnerGroup(partner, withPartner));
                            saveList.add(new BusinessPartnerGroup(withPartner, partner));
                            partnerGroupDAO.save(saveList);
                        } else {
                            String type = "【取引先グループインポート】";
                            int lineIndex = skipHeader ? line + 2 : line + 1;
                            String info = "取引先名 " + Objects.toString(partnerGroup.getPartnerName());
                            String detail = "識別ID「" + partnerGroup.getPartnerCode() + "」- 取引グループ 識別ID「" + partnerGroup.getWithPartnerCode() + "」は既に使用されているためインポートしません。";
                            ImportLogDTO importLog = new ImportLogDTO(type, lineIndex, info, detail);
                            importLogs.add(importLog);
                        }
                    } else {
                        String type = "【取引先グループインポート】";
                        int lineIndex = skipHeader ? line + 2 : line + 1;
                        String info = "取引先名 " + Objects.toString(partnerGroup.getPartnerName());
                        String detail;
                        if(partner == null && withPartner != null) {
                            detail = "識別は存在しません。";
                        } else if (partner != null && withPartner == null) {
                            detail = "取引グループは存在しません。";
                        } else {
                            detail = "識別は存在しません、取引グループは存在しません。";
                        }
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
            throw new Exception("取引先グループのインポートに失敗しました");
        }
        if(file != null) {
            file.delete();
        }

        return importLogs;
    }
}
