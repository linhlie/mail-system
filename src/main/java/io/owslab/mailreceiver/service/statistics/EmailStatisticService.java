package io.owslab.mailreceiver.service.statistics;

import io.owslab.mailreceiver.dto.EmailDTO;
import io.owslab.mailreceiver.dto.EmailStatisticDTO;
import io.owslab.mailreceiver.dto.ExtractMailDTO;
import io.owslab.mailreceiver.form.EmailStatisticDetailForm;
import io.owslab.mailreceiver.form.StatisticConditionForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.PeopleInChargePartner;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import io.owslab.mailreceiver.service.expansion.PeopleInChargePartnerService;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.matching.MatchingConditionService;
import io.owslab.mailreceiver.utils.FilterRule;
import io.owslab.mailreceiver.utils.FullNumberRange;
import io.owslab.mailreceiver.utils.MatchingResult;
import io.owslab.mailreceiver.utils.MatchingWordResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailStatisticService {

    @Autowired
    MatchingConditionService matchingConditionService;

    @Autowired
    MailBoxService mailBoxService;

    @Autowired
    BusinessPartnerService partnerService;

    @Autowired
    PeopleInChargePartnerService peopleInChargeService;


    public List<EmailStatisticDTO> statisticEmail(StatisticConditionForm form){
        if (form==null) new ArrayList<>();

        boolean isDate = form.isStatisticByDay();
        boolean isHour = form.isStatisticByHour();
        boolean isFrom = form.isStatisticByDomain();
        boolean isWord = form.isStatisticByWord();

        List<EmailStatisticDTO> listResult = new ArrayList<>();
        List<Email> emailList = getStatisEmail(form.getStatisticConditionData());
        List<String> matchingWords = matchingConditionService.getWordList(form.getMatchingWords());
        if(matchingWords==null || matchingWords.size()<=0){
            for(Email email : emailList){
                EmailStatisticDTO emailStatistic = new EmailStatisticDTO(email, "", isDate, isHour, isFrom, isWord);
                listResult.add(emailStatistic);
            }
        }else{
            List<MatchingWordResult> matchStatisticCondition= matchingConditionService.findMatchWithWord(matchingWords, emailList, false, false);
            for(MatchingWordResult matchingWordResult : matchStatisticCondition){
                List<String> words = matchingWordResult.getWords();
                if(words!=null && words.size()>0){
                    for(String word : words){
                        EmailStatisticDTO emailStatistic = new EmailStatisticDTO(matchingWordResult.getEmail(), word, isDate, isHour, isFrom, isWord);
                        listResult.add(emailStatistic);
                    }
                }
            }
        }
        List<EmailStatisticDTO> listResultFinal = new ArrayList<>();
        if(!isDate && !isHour){
            findDupliateMatching(listResult, listResultFinal, 0, listResult.size(), isFrom, isWord);
            return listResultFinal;
        }

        sortList(listResult, isDate, isHour);
        solutionMatchingStatistic(listResult, listResultFinal, isDate, isHour, isFrom, isWord);
        return listResultFinal;
    }

    public List<Email> getStatisEmail(FilterRule rootRule){
        List<Email> emailList = mailBoxService.getAllASC();
        List<Email> matchList;
        if(rootRule.getRules().size() > 0) {
            matchList = matchingConditionService.findMailMatching(emailList, rootRule, false, false);
        } else {
            matchList = emailList;
        }
        return matchList;
    }

    public void sortList(List<EmailStatisticDTO> matchingStatistic, boolean isDate, boolean isHour){
        if(isDate){
            sortListByDate(matchingStatistic, isHour);
        }else{
            if(isHour){
                sortListByHour(matchingStatistic);
            }
        }
    }

    public void sortListByDate(List<EmailStatisticDTO> matchingStatistic, boolean isHour){
        Collections.sort(matchingStatistic, new Comparator<EmailStatisticDTO>() {
            @Override
            public int compare(EmailStatisticDTO obj1,EmailStatisticDTO obj2) {
                if(obj1.getDate().compareTo(obj2.getDate()) != 0){
                    return obj1.getDate().compareTo(obj2.getDate());
                }else{
                    if(isHour){
                        return obj1.getHour().compareTo(obj2.getHour());
                    }else{
                        return 0;
                    }
                }
            }
        });
    }

    public void sortListByHour(List<EmailStatisticDTO> matchingStatistic){
        Collections.sort(matchingStatistic, new Comparator<EmailStatisticDTO>() {
            @Override
            public int compare(EmailStatisticDTO obj1,EmailStatisticDTO obj2) {
                return obj1.getHour().compareTo(obj2.getHour());
            }
        });
    }

    public void solutionMatchingStatistic(List<EmailStatisticDTO> list, List<EmailStatisticDTO> listResult,boolean isDate, boolean isHour, boolean isFrom, boolean isWrod){
        if(isDate){
            statisticByDate(list, listResult, isDate, isHour, isFrom, isWrod);
        }else{
            if(isHour){
                statisticByHour(list, listResult, isDate, isHour, isFrom, isWrod);
            }
        }
    }

    public void statisticByDate(List<EmailStatisticDTO> list, List<EmailStatisticDTO> listResult,boolean isDate, boolean isHour, boolean isFrom, boolean isWord){
        String date="";
        String hour="";
        int start=0;

        if(isHour){
            for(int i=0;i<list.size();i++){
                if(list.get(i).getDate().equals(date)){
                    list.get(i).setDate("");
                    if(list.get(i).getHour().equals(hour)){
                        list.get(i).setHour("");
                    }else{
                        findDupliateMatching(list, listResult, start, i, isFrom, isWord);
                        start = i;
                        hour = list.get(i).getHour();
                    }
                }else{
                    findDupliateMatching(list, listResult, start, i, isFrom, isWord);
                    start = i;
                    date = list.get(i).getDate();
                    hour = list.get(i).getHour();
                }
            }
        }else{
            for(int i=0;i<list.size();i++){
                if(list.get(i).getDate().equals(date)){
                    list.get(i).setDate("");
                }else{
                    findDupliateMatching(list, listResult, start, i, isFrom, isWord);
                    start = i;
                    date = list.get(i).getDate();
                }
            }
        }
        findDupliateMatching(list, listResult, start, list.size(), isFrom, isWord);
    }

    public void statisticByHour(List<EmailStatisticDTO> list, List<EmailStatisticDTO> listResult,boolean isDate, boolean isHour, boolean isFrom, boolean isWord){
        String hour="";
        int start=0;

        for(int i=0;i<list.size();i++){
            if(list.get(i).getHour().equals(hour)){
                list.get(i).setHour("");
            }else{
                findDupliateMatching(list, listResult, start, i, isFrom, isWord);
                start = i;
                hour = list.get(i).getHour();
            }
        }
        findDupliateMatching(list, listResult, start, list.size(), isFrom, isWord);
    }

    public void findDupliateMatching(List<EmailStatisticDTO> list, List<EmailStatisticDTO> listResult,int start, int end,boolean isFrom, boolean isWord){
        List<EmailStatisticDTO> listMatching = new ArrayList<>();
        if(!isFrom && !isWord){
            listMatching = findDupliate(list, start, end);
        }
        if(isFrom && isWord){
            listMatching = findDupliateByFromAndWord(list, start, end);
        }else{
            if(isFrom){
                listMatching = findDupliateByFrom(list, start, end);
            }
            if(isWord){
                listMatching = findDupliateByWord(list, start, end);
            }
        }

        for(int i=0;i<listMatching.size();i++){
            listResult.add(listMatching.get(i));
        }
    }

    public List<EmailStatisticDTO> findDupliateByFromAndWord(List<EmailStatisticDTO> list, int start, int end){
        LinkedHashMap<String, EmailStatisticDTO> hashMail = new LinkedHashMap<String, EmailStatisticDTO>();
        for(int i=start;i<end;i++){
            String key = list.get(i).getDomain()+list.get(i).getWord();
            if(hashMail.containsKey(key)){
                hashMail.get(key).setCount(hashMail.get(key).getCount()+1);
                hashMail.get(key).addMessageId(list.get(i).getMessageId());
            }else{
                hashMail.put(key, list.get(i));
            }
        }
        List<EmailStatisticDTO> result = new ArrayList<EmailStatisticDTO>();
        for (Map.Entry<String, EmailStatisticDTO> entry : hashMail.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    public List<EmailStatisticDTO> findDupliateByFrom(List<EmailStatisticDTO> list, int start, int end){
        LinkedHashMap<String, EmailStatisticDTO> hashMail = new LinkedHashMap<String, EmailStatisticDTO>();
        for(int i=start;i<end;i++){
            String key = list.get(i).getDomain();
            if(hashMail.containsKey(key)){
                hashMail.get(key).setCount(hashMail.get(key).getCount()+1);
                hashMail.get(key).addMessageId(list.get(i).getMessageId());
            }else{
                hashMail.put(key, list.get(i));
            }
        }
        List<EmailStatisticDTO> result = new ArrayList<EmailStatisticDTO>();
        for (Map.Entry<String, EmailStatisticDTO> entry : hashMail.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    public List<EmailStatisticDTO> findDupliateByWord(List<EmailStatisticDTO> list, int start, int end){
        LinkedHashMap<String, EmailStatisticDTO> hashMail = new LinkedHashMap<String, EmailStatisticDTO>();
        for(int i=start;i<end;i++){
            String key = list.get(i).getWord();
            if(hashMail.containsKey(key)){
                hashMail.get(key).setCount(hashMail.get(key).getCount()+1);
                hashMail.get(key).addMessageId(list.get(i).getMessageId());
            }else{
                hashMail.put(key, list.get(i));
            }
        }
        List<EmailStatisticDTO> result = new ArrayList<EmailStatisticDTO>();
        for (Map.Entry<String, EmailStatisticDTO> entry : hashMail.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    public List<EmailStatisticDTO> findDupliate(List<EmailStatisticDTO> list, int start, int end){
        LinkedHashMap<String, EmailStatisticDTO> hashMail = new LinkedHashMap<String, EmailStatisticDTO>();
        for(int i=start;i<end;i++){
            String key = "default";
            if(hashMail.containsKey(key)){
                hashMail.get(key).setCount(hashMail.get(key).getCount()+1);
                hashMail.get(key).addMessageId(list.get(i).getMessageId());
            }else{
                hashMail.put(key, list.get(i));
            }
        }
        List<EmailStatisticDTO> result = new ArrayList<EmailStatisticDTO>();
        for (Map.Entry<String, EmailStatisticDTO> entry : hashMail.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    public List<ExtractMailDTO> getDetailEmails(EmailStatisticDetailForm form) {
        List<ExtractMailDTO> listResult = new ArrayList<>();
        List<String> listMessageId = form.getListMessageId();
        List<Email> listEmail = mailBoxService.getEmailsByMessageId(listMessageId);

        List<BusinessPartner> listPartner = partnerService.getAll();
        List<PeopleInChargePartner> peoleIncharges = peopleInChargeService.getAll();
        LinkedHashMap<String, PeopleInChargePartner> peopleInChargeMap = new LinkedHashMap<>();
        for(PeopleInChargePartner people : peoleIncharges){
            peopleInChargeMap.put(people.getEmailAddress().toLowerCase(), people);
        }

        for(Email email : listEmail){
            listResult.add(new ExtractMailDTO(email, listPartner, peopleInChargeMap));
        }
        return listResult;
    }
}