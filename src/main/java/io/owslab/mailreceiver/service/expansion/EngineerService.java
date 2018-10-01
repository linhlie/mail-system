package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.EngineerDAO;
import io.owslab.mailreceiver.dao.RelationshipEngineerPartnerDAO;
import io.owslab.mailreceiver.dto.CSVEngineerDTO;
import io.owslab.mailreceiver.dto.EngineerListItemDTO;
import io.owslab.mailreceiver.dto.EngineerMatchingDTO;
import io.owslab.mailreceiver.dto.ImportLogDTO;
import io.owslab.mailreceiver.exception.EngineerFieldValidationException;
import io.owslab.mailreceiver.exception.EngineerNotFoundException;
import io.owslab.mailreceiver.exception.PartnerNotFoundException;
import io.owslab.mailreceiver.form.EngineerFilterForm;
import io.owslab.mailreceiver.form.EngineerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Engineer;
import io.owslab.mailreceiver.model.RelationshipEngineerPartner;
import io.owslab.mailreceiver.service.file.UploadFileService;
import io.owslab.mailreceiver.utils.CSVBundle;
import io.owslab.mailreceiver.utils.EngineerFilter;
import io.owslab.mailreceiver.utils.EngineerMatchingFilter;
import io.owslab.mailreceiver.utils.Utils;

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
 * Created by khanhlvb on 8/17/18.
 */
@Service
@CacheConfig(cacheNames = "short_term_matching")
public class EngineerService {
    private static final Logger logger = LoggerFactory.getLogger(EngineerService.class);

    @Autowired
    private EngineerDAO engineerDAO;
    
    @Autowired
    private RelationshipEngineerPartnerDAO relationshipEngineerPartnerDAO;

    @Autowired
    private BusinessPartnerService partnerService;

    @Autowired
    private UploadFileService uploadFileService;
    
    @Autowired
    private ExpansionTransaction expansionTransaction;

    @CacheEvict(key="\"EngineerService:getPartnerIds:\"+#id")
    public void delete(long id) {
        engineerDAO.delete(id);
        relationshipEngineerPartnerDAO.deleteByEngineerId(id);
    }
    
    @CacheEvict(allEntries = true)
    public void deleteAll() {
        engineerDAO.deleteAll();
        relationshipEngineerPartnerDAO.deleteAll();
    }
    
    public List<RelationshipEngineerPartner> getRelationshipEngineerPartner(long EngineerId){
    	List<RelationshipEngineerPartner> result = relationshipEngineerPartnerDAO.findByEngineerId(EngineerId);
    	return result;
    }

    public void add(EngineerForm form) throws PartnerNotFoundException, ParseException{
        expansionTransaction.addEngineerAndRelation(form);
    }

    public void update(EngineerForm form, long id) throws EngineerNotFoundException, PartnerNotFoundException, ParseException {
        Engineer engineer = form.getBuilder().build();
        engineer.setId(id);
        expansionTransaction.updateEngineerAndRelation(engineer, form.getGroupAddIds(), form.getGroupRemoveIds());
    }

    public List<EngineerListItemDTO> filter(EngineerFilterForm form, Timestamp now) {
    	List<EngineerFilter> listEngineerByStatus = filterEngineerByStatus(form, now);
    	
    	boolean filterTime = form.isFilterTime();
    	boolean filterTimeNull = form.isFilterTimeNull();
    	long filteDate=0;
    	if(filterTime){
    		filteDate = form.getFilterDate();
    	}

    	for(int i = listEngineerByStatus.size()-1 ;i>=0 ; i--){
    		EngineerFilter engineer = listEngineerByStatus.get(i);
    		if(engineer==null){
    			listEngineerByStatus.remove(i);
    			continue;
    		}
        	if(engineer.FilterEngineerToRemove(filteDate, filterTime, filterTimeNull)){
        		listEngineerByStatus.remove(i);
        	}
    	}
    	return buildEngineerDTOFromEngineerFilter(listEngineerByStatus);
    }
    
    public List<EngineerFilter> filterEngineerByStatus(EngineerFilterForm form, Timestamp now) {
    	switch (form.getFilterType()) {
        	case EngineerFilterForm.FilterType.ACTIVE:
            	return getActive(now);
        	case EngineerFilterForm.FilterType.INACTIVE:
        		return getInactive(now);
        	case EngineerFilterForm.FilterType.ALL:
        	default:
        		return getAll(now);
    	}
    }
    
    public List<EngineerMatchingDTO> filterEngineerMatching(EngineerFilterForm form, Timestamp now) {
    	List<EngineerMatchingFilter> listEngineerMatchingByStatus = filterEngineerMatchingyStatus(form, now);
    	boolean filterTime = form.isFilterTime();
    	boolean filterTimeNull = form.isFilterTimeNull();
    	long filterDate = 0;
        if(filterTime){
        	filterDate = form.getFilterDate();
        }
    	for(int i = listEngineerMatchingByStatus.size()-1 ;i>=0 ; i--){
    		EngineerMatchingFilter engineer = listEngineerMatchingByStatus.get(i);
    		if(engineer==null){
    			listEngineerMatchingByStatus.remove(i);
    			continue;
    		}
    		if(engineer.FilterEngineerToRemove(filterDate, filterTime, filterTimeNull)){
    			listEngineerMatchingByStatus.remove(i);
        	}
    	}
    	return buildEngineerMatchingDTOFromEngineerMatchingFilter(listEngineerMatchingByStatus);
    }
    
    public List<EngineerMatchingFilter> filterEngineerMatchingyStatus(EngineerFilterForm form, Timestamp now) {
        switch (form.getFilterType()) {
            case EngineerFilterForm.FilterType.ACTIVE:
                return getActiveEngineerMatching(now);
            case EngineerFilterForm.FilterType.INACTIVE:
                return getInactiveEngineerMatching(now);
            case EngineerFilterForm.FilterType.ALL:
            default:
                return getAllEngineerMatching(now);
        }
    }

    public List<EngineerFilter> getActive(Timestamp now) {
        List<EngineerFilter> listEngineer = getAll(now);
        List<EngineerFilter> result = new ArrayList<>();
        for (EngineerFilter item : listEngineer) {
            if (item.isActive()) {
                result.add(item);
            }
        }
        return result;
    }
    
    public List<EngineerMatchingFilter> getActiveEngineerMatching(Timestamp now) {
        List<EngineerMatchingFilter> listEngineerDTO = getAllEngineerMatching(now);
        List<EngineerMatchingFilter> result = new ArrayList<>();
        for (EngineerMatchingFilter item : listEngineerDTO) {
            if (item.isActive()) {
                result.add(item);
            }
        }
        return result;
    }

    public List<EngineerFilter> getInactive(Timestamp now) {
        List<EngineerFilter> listEngineer = getAll(now);
        List<EngineerFilter> result = new ArrayList<>();
        for (EngineerFilter item : listEngineer) {
            if (!item.isActive()) {
                result.add(item);
            }
        }
        return result;
    }
    
    public List<EngineerMatchingFilter> getInactiveEngineerMatching(Timestamp now) {
        List<EngineerMatchingFilter> listEngineerDTO = getAllEngineerMatching(now);
        List<EngineerMatchingFilter> result = new ArrayList<>();
        for (EngineerMatchingFilter item : listEngineerDTO) {
            if (!item.isActive()) {
                result.add(item);
            }
        }
        return result;
    }

    public List<EngineerFilter> getAll(Timestamp now) {
        List<Engineer> engineers = (List<Engineer>) engineerDAO.findAll();
        return buildEngineerFilter(engineers, now);
    }
    
    public List<EngineerMatchingFilter> getAllEngineerMatching(Timestamp now) {
        List<Engineer> engineers = (List<Engineer>) engineerDAO.findAll();
        return buildEngineerMatchingFilter(engineers, now);
    }

    public List<Engineer> getList() {
        List<Engineer> engineers = (List<Engineer>) engineerDAO.findAll();
        return engineers;
    }

    private List<EngineerListItemDTO> buildEngineerDTOFromEngineerFilter(List<EngineerFilter> engineers) {
        List<EngineerListItemDTO> engineerDTOs = new ArrayList<>();
        for(EngineerFilter engineer : engineers){
        	EngineerListItemDTO eng = new EngineerListItemDTO();
        	eng.setId(engineer.getId());
        	eng.setName(engineer.getName());
        	eng.setPartnerName(engineer.getPartnerName());
        	eng.setActive(engineer.isActive());
        	eng.setAutoExtend(engineer.isAutoExtend());
        	eng.setDormant(engineer.isDormant());
        	engineerDTOs.add(eng);
        }
        return engineerDTOs;
    }
    
    
    private List<EngineerFilter> buildEngineerFilter(List<Engineer> engineers, Timestamp now) {
        List<EngineerFilter> engineerFilters = new ArrayList<>();
        for(Engineer engineer : engineers){
            BusinessPartner partner = partnerService.findOne(engineer.getPartnerId());
            if(partner != null) {
            	engineerFilters.add(new EngineerFilter(engineer, partner.getName(), now));
            }
        }
        return engineerFilters;
    }
    
    private List<EngineerMatchingDTO> buildEngineerMatchingDTOFromEngineerMatchingFilter(List<EngineerMatchingFilter> engineers) {
        List<EngineerMatchingDTO> engineerDTOs = new ArrayList<>();
        for(EngineerMatchingFilter engineer : engineers){
        	EngineerMatchingDTO eng = new EngineerMatchingDTO(engineer);
        	engineerDTOs.add(eng);
        }
        return engineerDTOs;
    }
    
    private List<EngineerMatchingFilter> buildEngineerMatchingFilter(List<Engineer> engineers, Timestamp now) {
        List<EngineerMatchingFilter> engineerMatchingFilters = new ArrayList<>();
        for(Engineer engineer : engineers){
            BusinessPartner partner = partnerService.findOne(engineer.getPartnerId());
            if(partner != null) {
            	engineerMatchingFilters.add(new EngineerMatchingFilter(engineer, partner.getName(), now));
            }
        }
        return engineerMatchingFilters;
    }

    public List<Engineer.Builder> getById(long id) {
        List<Engineer.Builder> result = new ArrayList<>();
        Engineer engineer = engineerDAO.findOne(id);
        if(engineer != null) {
            result.add(new Engineer.Builder(engineer));
        }
        return result;
    }

    public void autoExtend() {
        List<Engineer> autoExtends = engineerDAO.findByAutoExtend(true);
        long current = System.currentTimeMillis();
        for(Engineer engineer: autoExtends) {
            long projectPeriodEnd = engineer.getProjectPeriodEnd();
            if(projectPeriodEnd==0){
            	continue;
            }
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
                final String[] headers = new String[]{ "name", "kanaName", "initial", "mailAddress", "employmentStatus", "partnerCode", "projectPeriodStart", "projectPeriodEnd", "autoExtend", "extendMonth", "matchingWord", "notGoodWord", "monetaryMoney", "stationLine", "stationNearest", "commutingTime", "skillSheet",  "introduction"};

                CSVEngineerDTO engineerDTO;
                if(skipHeader) {
                    beanReader.getHeader(skipHeader);
                }
                while ((engineerDTO = beanReader.read(CSVEngineerDTO.class, headers)) != null) {
                    engineerDTOS.add(engineerDTO);
                }
                if(deleteOld) {
                    deleteAll();
                }
                for(int line = 0; line < engineerDTOS.size(); line++) {
                    CSVEngineerDTO csvEngineerDTO = engineerDTOS.get(line);
                    String name = csvEngineerDTO.getName();
                    String kanaName = csvEngineerDTO.getKanaName();
                    String employmentStatus = csvEngineerDTO.getEmploymentStatus();
                    String partnerCode = csvEngineerDTO.getPartnerCode();
                    String projectPeriodStart = csvEngineerDTO.getProjectPeriodStart();
                    String projectPeriodEnd = csvEngineerDTO.getProjectPeriodEnd();
                    String skillSheet = csvEngineerDTO.getSkillSheet();
                    
                    String typetmp = "【技術者インポート】";
                    int lineIndextmp = skipHeader ? line + 2 : line + 1;
                    String infotmp = "技術者名 " + Objects.toString(name, "");
                    List<String> missingList = new ArrayList<>();
                    
                    if(name == null || kanaName == null || employmentStatus == null || partnerCode == null) {
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
                        String detail = String.join("、", missingList) + "。";
                        ImportLogDTO importLog = new ImportLogDTO(typetmp, lineIndextmp, infotmp, detail);
                        importLogs.add(importLog);
                        continue;
                    }
                    if(projectPeriodStart != null && projectPeriodEnd != null){
                    	if(projectPeriodStart.compareTo(projectPeriodEnd)>0){
                        	missingList.add("案件期間「終了」は案件期間「開始」以上");
                            String detail = String.join("、", missingList) + "。";
                            ImportLogDTO importLog = new ImportLogDTO(typetmp, lineIndextmp, infotmp, detail);
                            importLogs.add(importLog);
                            continue;
                    	}
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
        String[] csvHeader = { "技術者名", "カナ氏名", "イニシャル", "メールアドレス", "雇用形態",
                "所属企業", "案件期間 開始", "案件期間 終了", "延長", "延長期間", "マッチングワード", "NGワード", "単金", "最寄り駅 線", "最寄り駅 駅", "通勤時間", "スキルシートのフォルダー", "技術者紹介文" };
        String[] keys = { "name", "kanaName", "initial", "mailAddress", "employmentStatus", "partnerCode", "projectPeriodStart", "projectPeriodEnd", "autoExtend", "extendMonth", "matchingWord", "notGoodWord", "monetaryMoney", "stationLine", "stationNearest", "commutingTime", "skillSheet", "introduction" };
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
    
    @Cacheable(key="\"EngineerService:getPartnerIds:\"+#engineerId")
    public List<Long> getPartnerIds(Long engineerId){
    	List<Long> listPartnerId = relationshipEngineerPartnerDAO.getPartnerIds(engineerId);
    	if(listPartnerId == null || listPartnerId.size()==0) return null;
    	return listPartnerId;
    }
}
