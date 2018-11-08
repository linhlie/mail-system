package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.PeopleInChargePartnerDAO;
import io.owslab.mailreceiver.dto.CSVPeopleInChargePartnerDTO;
import io.owslab.mailreceiver.dto.ImportLogDTO;
import io.owslab.mailreceiver.dto.PeopleInChargePartnerDTO;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.PeopleInChargePartner;
import io.owslab.mailreceiver.service.file.UploadFileService;
import io.owslab.mailreceiver.utils.CSVBundle;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@CacheConfig(cacheNames = "short_term_matching")
public class PeopleInChargePartnerService {
    private static final Logger logger = LoggerFactory.getLogger(PeopleInChargePartnerService.class);
    @Autowired
    PeopleInChargePartnerDAO peopleInChargePartnerDAO;

    @Autowired
    BusinessPartnerService partnerService;

    @Autowired
    UploadFileService uploadFileService;

    public PeopleInChargePartner getById(long id){
        return peopleInChargePartnerDAO.findOne(id);
    }

    public void deletePeople(long id) {
        peopleInChargePartnerDAO.delete(id);
    }

    public void deleteAll(){
        peopleInChargePartnerDAO.deleteAll();
    }

    public List<PeopleInChargePartnerDTO> getByPartnerId(long partnerId){
        List<PeopleInChargePartner> listPeople =  peopleInChargePartnerDAO.findByPartnerId(partnerId);
        List<PeopleInChargePartnerDTO> listPeopleDTO =  new ArrayList<>();
        for(PeopleInChargePartner people : listPeople){
            PeopleInChargePartnerDTO peopleDTO = new PeopleInChargePartnerDTO(people);
            listPeopleDTO.add(peopleDTO);
        }
        return listPeopleDTO;
    }

    //need transaction
    public void addPeopleInChargePartner(PeopleInChargePartner people) throws Exception {
        BusinessPartner partner = partnerService.findOne(people.getPartnerId());
        if(partner==null){
            throw new Exception("Partner doesn't esxit");
        }
        PeopleInChargePartner peopleInCharge = peopleInChargePartnerDAO.findByEmailAddress(people.getEmailAddress());
        if(peopleInCharge != null){
            throw new Exception("Email esxited");
        }
        if(people.isEmailInChargePartner()){
            PeopleInChargePartner peopleInChargePartner = peopleInChargePartnerDAO.findByEmailInChargePartner(true);
            if(peopleInChargePartner == null){
                peopleInChargePartnerDAO.save(people);
            }else{
                peopleInChargePartner.setEmailInChargePartner(false);
                peopleInChargePartnerDAO.save(peopleInChargePartner);
                peopleInChargePartnerDAO.save(people);
            }
        }else{
            peopleInChargePartnerDAO.save(people);
        }
    }

    //need transaction
    public void editPeopleInChargePartner(PeopleInChargePartner people) throws Exception {
        PeopleInChargePartner peopleInCharge = peopleInChargePartnerDAO.findByEmailAddress(people.getEmailAddress());
        if(peopleInCharge != null && peopleInCharge.getId() != people.getId()){
            throw new Exception("Email esxited");
        }
        if(people.isEmailInChargePartner()){
            PeopleInChargePartner peopleInChargePartner = peopleInChargePartnerDAO.findByEmailInChargePartner(true);
            if(peopleInChargePartner != null && peopleInChargePartner.getId() != people.getId()){
                peopleInChargePartner.setEmailInChargePartner(false);
                peopleInChargePartnerDAO.save(peopleInChargePartner);
                peopleInChargePartnerDAO.save(people);
            }else{
                peopleInChargePartnerDAO.save(people);
            }
        }else{
            peopleInChargePartnerDAO.save(people);
        }
    }

    private List<CSVPeopleInChargePartnerDTO> getPeopleInChargePartnersToExport() {
        List<CSVPeopleInChargePartnerDTO> result = new ArrayList<>();
        List<PeopleInChargePartner> peopleInChargePartners = peopleInChargePartnerDAO.findAll();
        for(PeopleInChargePartner people : peopleInChargePartners) {
            BusinessPartner partner = partnerService.findOne(people.getPartnerId());
            if(partner != null) {
                result.add(new CSVPeopleInChargePartnerDTO(people, partner));
            }
        }
        return result;
    }

    public CSVBundle<CSVPeopleInChargePartnerDTO> export() {
        CSVBundle<CSVPeopleInChargePartnerDTO> csvBundle = new CSVBundle<CSVPeopleInChargePartnerDTO>();
        csvBundle.setFileName("担当者.csv");
        String[] csvHeader = { "所属企業", "ドメイン", "担当者氏姓", "担当者氏名", "所属部署", "役職", "メールアドレス", "代表メールアドレス", "電話番号1", "電話番号2", "特記事項", "休止"};
        String[] keys = { "partnerCode", "domainPartner", "lastName", "firstName", "department", "position", "emailAddress", "emailInChargePartner", "numberPhone1", "numberPhone2", "note", "pause"};
        csvBundle.setHeaders(csvHeader);
        csvBundle.setKeys(keys);
        List<CSVPeopleInChargePartnerDTO> data = getPeopleInChargePartnersToExport();
        csvBundle.setData(data);
        return csvBundle;
    }

    boolean checkValidateEmail(String emailAddress){
        Pattern pattern = Pattern.compile("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    public boolean checkValidateNumberPhone(String numberPhone){
        String pattern1 = "^\\+\\d+$";
        String pattern2 = "^\\d+$";
        if(numberPhone.matches(pattern1) || numberPhone.matches(pattern2)){
            return true;
        }
        return false;
    }

    public List<ImportLogDTO> importPeopleinChargePartner(MultipartFile multipartFile, boolean skipHeader, boolean deleteOld) throws Exception {

        File file = null;

        List<ImportLogDTO> importLogs = new ArrayList<ImportLogDTO>();
        List<CSVPeopleInChargePartnerDTO> peopleDTOS = new ArrayList<>();

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
                final String[] headers = new String[]{ "partnerCode", "domainPartner", "lastName", "firstName", "department", "position", "emailAddress", "emailInChargePartner", "numberPhone1", "numberPhone2", "note", "pause"};

                CSVPeopleInChargePartnerDTO peopleDTO;
                if(skipHeader) {
                    beanReader.getHeader(skipHeader);
                }
                while ((peopleDTO = beanReader.read(CSVPeopleInChargePartnerDTO.class, headers)) != null) {
                    peopleDTOS.add(peopleDTO);
                }
                if(deleteOld) {
                    deleteAll();
                }
                for(int line = 0; line < peopleDTOS.size(); line++) {
                    CSVPeopleInChargePartnerDTO csvPeopleDTO = peopleDTOS.get(line);
                    String partnerCode = csvPeopleDTO.getPartnerCode();
                    String lastName = csvPeopleDTO.getLastName();
                    String firstName = csvPeopleDTO.getFirstName();
                    String department = csvPeopleDTO.getDepartment();
                    String position = csvPeopleDTO.getPosition();
                    String emailAddress = csvPeopleDTO.getEmailAddress();
                    String numberPhone1 = csvPeopleDTO.getNumberPhone1();
                    String numberPhone2 = csvPeopleDTO.getNumberPhone2();

                    String typetmp = "【担当者インポート】";
                    int lineIndextmp = skipHeader ? line + 2 : line + 1;
                    String infotmp = "担当者氏名 " + Objects.toString(lastName+"　"+firstName, "");
                    List<String> missingList = new ArrayList<>();

                    if(lastName == null || firstName == null || department == null || position == null || emailAddress ==null || numberPhone1 ==null) {
                        if(lastName == null) {
                            missingList.add("担当者氏姓がありません");
                        }
                        if(firstName == null) {
                            missingList.add("担当者氏名がありません");
                        }
                        if(department == null) {
                            missingList.add("department null");
                        }
                        if(position == null) {
                            missingList.add("position null");
                        }
                        if(emailAddress == null) {
                            missingList.add("email null");
                        }
                        if(numberPhone1 == null) {
                            missingList.add("number phone 1 null");
                        }
                        String detail = String.join("、", missingList) + "。";
                        ImportLogDTO importLog = new ImportLogDTO(typetmp, lineIndextmp, infotmp, detail);
                        importLogs.add(importLog);
                        continue;
                    }
                    if(emailAddress != null && !checkValidateEmail(emailAddress)){
                        missingList.add("Email invalidate");
                        String detail = String.join("、", missingList) + "。";
                        ImportLogDTO importLog = new ImportLogDTO(typetmp, lineIndextmp, infotmp, detail);
                        importLogs.add(importLog);
                        continue;
                    }
                    if(numberPhone1 != null && !checkValidateNumberPhone(numberPhone1)){
                        missingList.add("phone 1 number invalidate");
                        String detail = String.join("、", missingList) + "。";
                        ImportLogDTO importLog = new ImportLogDTO(typetmp, lineIndextmp, infotmp, detail);
                        importLogs.add(importLog);
                        continue;
                    }

                    if(numberPhone2 != null && !checkValidateNumberPhone(numberPhone2)){
                        missingList.add("phone 2 number invalidate");
                        String detail = String.join("、", missingList) + "。";
                        ImportLogDTO importLog = new ImportLogDTO(typetmp, lineIndextmp, infotmp, detail);
                        importLogs.add(importLog);
                        continue;
                    }
                    BusinessPartner existPartner = partnerService.findOneByPartnerCode(partnerCode);
                    if(existPartner != null) {
                        PeopleInChargePartner people = csvPeopleDTO.build(existPartner);
                        try {
                            System.out.println(people.getLastName()+"  "+people.isEmailInChargePartner());
                            peopleInChargePartnerDAO.save(people);
                        }catch (Exception e){
                            String type = "【担当者インポート】";
                            int lineIndex = skipHeader ? line + 2 : line + 1;
                            String info = "担当者氏名 " + Objects.toString(lastName+"　"+firstName, "");
                            String detail = e.getMessage();
                            ImportLogDTO importLog = new ImportLogDTO(type, lineIndex, info, detail);
                            importLogs.add(importLog);
                        }
                    } else {
                        String type = "【担当者インポート】";
                        int lineIndex = skipHeader ? line + 2 : line + 1;
                        String info = "担当者氏名 " + Objects.toString(lastName+"　"+firstName, "");
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
}
