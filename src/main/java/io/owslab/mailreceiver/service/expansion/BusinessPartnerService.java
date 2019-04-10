package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.BusinessPartnerDAO;
import io.owslab.mailreceiver.dao.BusinessPartnerGroupDAO;
import io.owslab.mailreceiver.dto.*;
import io.owslab.mailreceiver.enums.CompanyType;
import io.owslab.mailreceiver.exception.PartnerCodeException;
import io.owslab.mailreceiver.form.PartnerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.BusinessPartnerGroup;
import io.owslab.mailreceiver.model.Engineer;
import io.owslab.mailreceiver.service.file.UploadFileService;
import io.owslab.mailreceiver.utils.CSVBundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import io.owslab.mailreceiver.utils.ConvertDomain;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Created by khanhlvb on 8/13/18.
 */
@Service
@CacheConfig(cacheNames = "short_term_matching")
public class BusinessPartnerService {
    private static final Logger logger = LoggerFactory.getLogger(BusinessPartnerService.class);

    @Autowired
    private BusinessPartnerDAO partnerDAO;

    @Autowired
    private BusinessPartnerGroupDAO partnerGroupDAO;

    @Autowired
    private UploadFileService uploadFileService;
    
    @Autowired
    private DomainService domainService;

    public List<BusinessPartner> getAll() {
        return (List<BusinessPartner>) partnerDAO.findAll();
    }

    public List<BusinessPartner> getBusinessPartner() {
        List<BusinessPartner> list =  getAll();
        Collections.sort(list, new BusinessPartnerComparator());
        return list;
    }

    public List<BusinessPartnerGroup> getAllGroup() {
        return (List<BusinessPartnerGroup>) partnerGroupDAO.findAll();
    }
    
    @CacheEvict(allEntries = true)
    public void deleteAllGroup(){
    	partnerGroupDAO.deleteAll();
    }
    
    @CacheEvict(key="\"BusinessPartnerService:getWithPartnerIds:\"+#partnerId")
    public void deletePartnerGroup(Long partnerId, List<BusinessPartnerGroup> partnerGroups){
    	partnerGroupDAO.delete(partnerGroups);
    }
    
    @CacheEvict(allEntries = true)
    public void deleteAllPartner(){
    	partnerDAO.deleteAll();
    }

    public BusinessPartner savePartner(BusinessPartner partner){
        return partnerDAO.save(partner);
    }

    public void savePartnerGroup(List<BusinessPartnerGroup> partnerGroups) {
        partnerGroupDAO.save(partnerGroups);
    }

    public BusinessPartner findOneByPartnerCode(String partnerCode){
        List<BusinessPartner> partners = partnerDAO.findByPartnerCode(partnerCode);
        return partners.size() > 0 ? partners.get(0) : null;
    }

    public BusinessPartner findOne(long id){
        return partnerDAO.findOne(id);
    }

    @CacheEvict(key="\"BusinessPartnerService:getDomainPartner:\"+#id")
    public void delete(long id){
        partnerDAO.delete(id);
    }

    public List<BusinessPartnerGroup> findByPartner(long partnerId){
        return partnerGroupDAO.findByPartnerId(partnerId);
    }

    public void deleteGroupList(BusinessPartner partner, List<Long> groupWithPartnerIds) {
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
        deletePartnerGroup(partner.getId(), partnerGroups);
    }

    public CSVBundle<CSVPartnerDTO> export() {
        CSVBundle<CSVPartnerDTO> csvBundle = new CSVBundle<CSVPartnerDTO>();
        csvBundle.setFileName("取引先.csv");
        String[] csvHeader = { "取引先名称", "カナ名称", "会社形態", "前株後株",
                "識別ID", "ドメイン", "ドメイン", "ドメイン", "自社", "アラート重要度", "アラート内容" };
        String[] keys = {"name", "kanaName", "companyType", "stockShare", "partnerCode", "domain1", "domain2", "domain3", "ourCompany", "alertLevel", "alertContent"};
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
    
    public List<ImportLogDTO> importPartner(MultipartFile multipartFile, boolean skipHeader, boolean deleteOld) throws Exception {
    	List<String> listDomain = new ArrayList<String>();
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
                final String[] headers = new String[]{"name", "kanaName", "companyType", "stockShare", "partnerCode", "domain1", "domain2", "domain3", "ourCompany", "alertLevel", "alertContent"};

                CSVPartnerDTO partnerDTO;
                if(skipHeader) {
                    beanReader.getHeader(skipHeader);
                }
                while ((partnerDTO = beanReader.read(CSVPartnerDTO.class, headers)) != null) {
                    partners.add(partnerDTO.build());
                }
                if(deleteOld) {
                    deleteAllPartner();
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
                        if(partner.getDomain1()!=null && !partner.getDomain1().equals("")){
                            listDomain.add(partner.getDomain1().toLowerCase());
                        }
                        if(partner.getDomain2()!=null && !partner.getDomain2().equals("")){
                            listDomain.add(partner.getDomain2().toLowerCase());
                        }
                        if(partner.getDomain3()!=null && !partner.getDomain3().equals("")){
                            listDomain.add(partner.getDomain3().toLowerCase());
                        }
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
        domainService.deleteDomainByListDomain(listDomain);
        return importLogs;
    }

    public List<ImportLogDTO> importPartnerGroup(MultipartFile multipartFile, boolean skipHeader, boolean deleteOld) throws Exception {

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
                if(deleteOld) {
                    deleteAllGroup();
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
    
    public HashMap<String, List<String>> getDomainRelationPartnerGroup(){
    	HashMap<String, List<String>> hashMapDomainPartnerGroups = new HashMap<String, List<String>>();
    	List<BusinessPartnerGroup> listPartnerGroup = (List<BusinessPartnerGroup>) partnerGroupDAO.findAll();
    	for(BusinessPartnerGroup partnerGroup : listPartnerGroup){
   		 String partnerDomain1 = partnerGroup.getPartner().getDomain1();
   		 String partnerDomain2 = partnerGroup.getPartner().getDomain2();
   		 String partnerDomain3 = partnerGroup.getPartner().getDomain3();
   		 
   		 String otherPartnerDomain1 = partnerGroup.getWithPartner().getDomain1();
   		 String otherPartnerDomain2 = partnerGroup.getWithPartner().getDomain2();
   		 String otherPartnerDomain3 = partnerGroup.getWithPartner().getDomain3();
   		 
   		putToHashMap(hashMapDomainPartnerGroups, partnerDomain1, partnerDomain2, partnerDomain3, 
   				otherPartnerDomain1, otherPartnerDomain2, otherPartnerDomain3);
   		putToHashMap(hashMapDomainPartnerGroups, partnerDomain2, partnerDomain1, partnerDomain3, 
   				otherPartnerDomain1, otherPartnerDomain2, otherPartnerDomain3);
   		putToHashMap(hashMapDomainPartnerGroups, partnerDomain3, partnerDomain1, partnerDomain2, 
   				otherPartnerDomain1, otherPartnerDomain2, otherPartnerDomain3);
   	}
   	return hashMapDomainPartnerGroups;
    }
    
    public void putToHashMap(HashMap<String, List<String>> hashMapDomainPartnerGroups, String key, String ownDomain1, String ownDomain2, 
    		String domain1, String domain2, String domain3){
  		if(key==null || key.equals("")) return;
    	if(hashMapDomainPartnerGroups.containsKey(key)){
    		hashMapDomainPartnerGroups.get(key).add(key);
    		hashMapDomainPartnerGroups.get(key).add(ownDomain1);
  			hashMapDomainPartnerGroups.get(key).add(ownDomain2);
  			hashMapDomainPartnerGroups.get(key).add(domain1);
  			hashMapDomainPartnerGroups.get(key).add(domain2);
  			hashMapDomainPartnerGroups.get(key).add(domain3);
  		 }else{
  			List<String> lisDomain = new ArrayList<String>();
  			lisDomain.add(ownDomain1);
  			lisDomain.add(ownDomain2);
  			lisDomain.add(domain1);
  			lisDomain.add(domain2);
  			lisDomain.add(domain3);
  			hashMapDomainPartnerGroups.put(key, lisDomain);
  		 }
    }
    
    public List<BusinessPartner> getPartnersByDomain(String domain){
    	List<BusinessPartner> listPartner= partnerDAO.findByDomain(domain);
    	return listPartner;
    }

    @Cacheable(key="\"BusinessPartnerService:getDomainPartner:\"+#partnerId")
    public List<String> getDomainPartner(Long partnerId){
    	BusinessPartner partner = findOne(partnerId);
    	if(partner==null) return null;
    	
    	List<String> listDomain = new ArrayList<String>();
        String domain1 =partner.getDomain1();
        String domain2 =partner.getDomain2();
        String domain3 =partner.getDomain3();
        if(domain1!=null && !domain1.equals("")){
            listDomain.add(domain1.toLowerCase());
        }
        if(domain2!=null && !domain2.equals("")){
            listDomain.add(domain2.toLowerCase());
        }
        if(domain3!=null && !domain3.equals("")){
            listDomain.add(domain3.toLowerCase());
        }
        return listDomain;
    }
    
    @Cacheable(key="\"BusinessPartnerService:getWithPartnerIds:\"+#partnerId")
    public List<Long> getWithPartnerIds(Long partnerId){
    	List<Long> listWithPartnerId = partnerGroupDAO.findWithPartnerIdByPartnerId(partnerId);
    	if(listWithPartnerId == null || listWithPartnerId.size()==0) return null;
    	return listWithPartnerId;
    }

    public List<PartnerForPeopleInChargeDTO> getPartnerForPeopleInCharge() {
        List<BusinessPartner> list =  partnerDAO.findDistinctByCompanyType(CompanyType.JOINT_STOCK.getValue());
        Collections.sort(list, new BusinessPartnerComparator());
        List<PartnerForPeopleInChargeDTO> result =  new ArrayList<>();
        for(BusinessPartner partner : list){
            PartnerForPeopleInChargeDTO p = new PartnerForPeopleInChargeDTO();
            p.setId(partner.getId());
            p.setName(partner.getName());
            p.setKanaName(partner.getKanaName());
            String domain = "";
            if(partner.getDomain1() != null && !partner.getDomain1().equals("")){
                domain = domain + partner.getDomain1();
            }
            if(partner.getDomain2() != null && !partner.getDomain2().equals("")){
                domain = domain + ", " + partner.getDomain2();
            }
            if(partner.getDomain3() != null && !partner.getDomain3().equals("")){
                domain = domain + ", " + partner.getDomain3();
            }
            p.setDomain(domain);
            result.add(p);
        }
        return result;
    }

    public long getEmailAccountId(Engineer engineer, List<EmailAccountToSendMailDTO> accountList) throws Exception {
        BusinessPartner partner = partnerDAO.findOne(engineer.getPartnerId());
        if(partner == null){
            throw new Exception("[Partner Service][get email account] partner null");
        }
        for(EmailAccountToSendMailDTO account : accountList){
            String domain = ConvertDomain.convertEmailToDomain(account.getAccount().toLowerCase());
            if(partner.getDomain1()!=null && partner.getDomain1().equalsIgnoreCase(domain)){
                return account.getId();
            }
            if(partner.getDomain2()!=null && partner.getDomain2().equalsIgnoreCase(domain)){
                return account.getId();
            }
            if(partner.getDomain3()!=null && partner.getDomain3().equalsIgnoreCase(domain)){
                return account.getId();
            }
        }
        return -1;
    }

    public class BusinessPartnerComparator implements Comparator<BusinessPartner> {
        public int compare(BusinessPartner o1, BusinessPartner o2) {
            if(o1.isOurCompany() == o2.isOurCompany()) {
                int value1 = o1.getKanaName().compareTo(o2.getKanaName());
                if (value1 == 0) {
                    int value2 = o1.getName().compareTo(o2.getName());
                    return value2;
                }
                return value1;
            } else if (o1.isOurCompany() && !o2.isOurCompany()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
