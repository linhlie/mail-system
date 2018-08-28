package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.BusinessPartnerDAO;
import io.owslab.mailreceiver.dao.BusinessPartnerGroupDAO;
import io.owslab.mailreceiver.dto.CSVPartnerDTO;
import io.owslab.mailreceiver.dto.CSVPartnerGroupDTO;
import io.owslab.mailreceiver.exception.PartnerCodeException;
import io.owslab.mailreceiver.form.PartnerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.BusinessPartnerGroup;
import io.owslab.mailreceiver.utils.CSVBundle;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by khanhlvb on 8/13/18.
 */
@Service
public class BusinessPartnerService {
    @Autowired
    private BusinessPartnerDAO partnerDAO;

    @Autowired
    private BusinessPartnerGroupDAO partnerGroupDAO;

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

    private BusinessPartner findOneByPartnerCode(String partnerCode){
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

    public List<BusinessPartner> importPartner(MultipartFile multipartFile, boolean skipHeader) throws IOException {

        File file = convertMultiPartToFile(multipartFile);

        List<BusinessPartner> saveList = new ArrayList<BusinessPartner>();
        List<BusinessPartner> partners = new ArrayList<BusinessPartner>();

        try(ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(file), CsvPreference.STANDARD_PREFERENCE))
        {
            // the header elements are used to map the values to the bean
            final String[] headers = new String[]{"name", "kanaName", "companyType", "stockShare", "partnerCode", "domain1", "domain2", "domain3", "ourCompany"};

            CSVPartnerDTO partnerDTO;
            while ((partnerDTO = beanReader.read(CSVPartnerDTO.class, headers)) != null) {
                partners.add(partnerDTO.build());
            }
            if(partners.size() > 0 && skipHeader) {
                partners.remove(0);
            }
            for(BusinessPartner partner : partners) {
                BusinessPartner existPartner = findOneByPartnerCode(partner.getPartnerCode());
                if(existPartner == null) {
                    saveList.add(partner);
                }
            }

            partnerDAO.save(saveList);
        }

        file.delete();

        return saveList;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
