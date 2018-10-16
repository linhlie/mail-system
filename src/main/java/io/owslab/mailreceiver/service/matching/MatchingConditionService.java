package io.owslab.mailreceiver.service.matching;

import com.mariten.kanatools.KanaConverter;

import io.owslab.mailreceiver.dao.MatchingConditionDAO;
import io.owslab.mailreceiver.dto.ExtractMailDTO;
import io.owslab.mailreceiver.dto.PreviewMailDTO;
import io.owslab.mailreceiver.enums.*;
import io.owslab.mailreceiver.form.ExtractForm;
import io.owslab.mailreceiver.form.MatchingConditionForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.MatchingCondition;
import io.owslab.mailreceiver.model.NumberTreatment;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.replace.NumberRangeService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.service.word.EmailWordJobService;
import io.owslab.mailreceiver.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by khanhlvb on 3/6/18.
 */
@Service
@CacheConfig(cacheNames = "short_term_data")
public class MatchingConditionService {

    private static final Logger logger = LoggerFactory.getLogger(MatchingConditionService.class);

    public static final long ONE_DAY_MILLISECONDS = 24 * 60 * 60 *1000;

    @Autowired
    private MatchingConditionDAO matchingConditionDAO;

    @Autowired
    private MailBoxService mailBoxService;

    @Autowired
    private EmailWordJobService emailWordJobService;

    @Autowired
    private NumberRangeService numberRangeService;

    @Autowired
    private NumberTreatmentService numberTreatmentService;
    
    @Autowired
    private BusinessPartnerService partnerService;

    @Autowired
    private MailAccountsService mailAccountsService;

    private NumberTreatment numberTreatment;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private int matchingCounter = 0;

    @Value("${mailreceiver.app.daysago}")
    private int daysago;

    public void saveList(List<MatchingCondition> matchingConditions, int type){
        //TODO: Must be transaction
        for(MatchingCondition matchingCondition : matchingConditions){
            matchingCondition.setType(type);
            MatchingCondition existCondition = matchingConditionDAO.findOne(matchingCondition.getId());
            if(existCondition != null){
                if(matchingCondition.getRemove() == 1){
                    matchingConditionDAO.delete(existCondition.getId());
                } else {
                    matchingConditionDAO.save(matchingCondition);
                }
            } else {
                if(matchingCondition.getRemove() == 0){
                    matchingConditionDAO.save(matchingCondition);
                }
            }
        }
    }

    public List<MatchingCondition> getSourceConditionList(){
        List<MatchingCondition> conditionList = getListByType(MatchingCondition.Type.SOURCE);
        MatchingCondition receivedDateCondition = null;
        for(MatchingCondition condition : conditionList){
            MailItemOption option = MailItemOption.fromValue(condition.getItem());
            if(option.equals(MailItemOption.RECEIVED_DATE)){
                receivedDateCondition = condition;
            }
        }
        if(receivedDateCondition == null){
            receivedDateCondition = new MatchingCondition(
                    true,
                    CombineOption.AND.getValue(),
                    MailItemOption.RECEIVED_DATE.getValue(),
                    ConditionOption.GE.getValue(),
                    formatter.format(getDaysAgo()),
                    MatchingCondition.Type.SOURCE
            );

            conditionList.add(receivedDateCondition);
        }
        return conditionList;
    }

    public List<MatchingCondition> getDestinationConditionList(){
        List<MatchingCondition> conditionList = getListByType(MatchingCondition.Type.DESTINATION);
        MatchingCondition receivedDateCondition = null;
        for(MatchingCondition condition : conditionList){
            MailItemOption option = MailItemOption.fromValue(condition.getItem());
            if(option.equals(MailItemOption.RECEIVED_DATE)){
                receivedDateCondition = condition;
            }
        }
        if(receivedDateCondition == null){
            receivedDateCondition = new MatchingCondition(
                    true,
                    CombineOption.AND.getValue(),
                    MailItemOption.RECEIVED_DATE.getValue(),
                    ConditionOption.GE.getValue(),
                    formatter.format(getDaysAgo()),
                    MatchingCondition.Type.DESTINATION
            );

            conditionList.add(receivedDateCondition);
        }
        return conditionList;
    }

    public List<MatchingCondition> getMatchingConditionList(){
        return getListByType(MatchingCondition.Type.MATCHING);
    }

    private List<MatchingCondition> getListByType(int type){
        return matchingConditionDAO.findByType(type);
    }

    public List<String> filter(List<Email> emailList, FilterRule rootRule) {
        List<String> msgIdList = new ArrayList<>();
        if(rootRule == null) return  msgIdList;
        numberTreatment = numberTreatmentService.getFirst();
        boolean distinguish = false;
        boolean spaceEffective = false;
        List<Email> matchList;
        if(rootRule.getRules().size() > 0) {
            matchList = findMailMatching(emailList, rootRule, distinguish, spaceEffective);
        } else {
            matchList = emailList;
        }
        for(Email email : matchList) {
            msgIdList.add(email.getMessageId());
        }
        return msgIdList;
    }

    public List<ExtractMailDTO> extract(ExtractForm extractForm){
        List<ExtractMailDTO> extractResult = new ArrayList<>();
        numberTreatment = numberTreatmentService.getFirst();
        FilterRule rootRule = extractForm.getConditionData();
        boolean filterSender = extractForm.isHandleDuplicateSender();
        boolean filterSubject = extractForm.isHandleDuplicateSubject();
        List<Email> emailList = mailBoxService.getAll();
        List<Email> matchList;
//        boolean distinguish = extractForm.isDistinguish();
//        boolean spaceEffective = extractForm.isSpaceEffective();
        boolean distinguish = false;
        boolean spaceEffective = false;
        if(rootRule.getRules().size() > 0) {
            matchList = findMailMatching(emailList, rootRule, distinguish, spaceEffective);
        } else {
            matchList = emailList;
        }
        matchList = mailBoxService.filterDuplicate(matchList, filterSender, filterSubject);
        for(Email email : matchList){
            if(email.getRangeList().size() == 0){
                String optimizedPart = email.getOptimizedText(false);
                List<FullNumberRange> rangeList = numberRangeService.buildNumberRangeForInput(email.getMessageId(), optimizedPart);
                email.setRangeList(rangeList);
            }
            extractResult.add(new ExtractMailDTO(email));
        }

        return extractResult;
    }

    public FinalMatchingResult matching(MatchingConditionForm matchingConditionForm){
        logger.info("start matching");
        List<MatchingResult> matchingResults = new ArrayList<>();
        Map<String, PreviewMailDTO> previewMailDTOList = new HashMap<>();
        numberTreatment = numberTreatmentService.getFirst();
        FilterRule sourceRule = matchingConditionForm.getSourceConditionData();
        FilterRule destinationRule = matchingConditionForm.getDestinationConditionData();
        FilterRule matchingRule = matchingConditionForm.getMatchingConditionData();
        FilterRule greaterOrEqualThanZeroRule = new FilterRule("4", "greater_or_equal", "数値");
        FilterRule lessOrEqualThanZeroRule = new FilterRule("4", "less_or_equal", "数値");
        FilterRule allRangeMatchRule = new FilterRule();
        allRangeMatchRule.setCondition("OR");
        allRangeMatchRule
                .addRule(greaterOrEqualThanZeroRule)
                .addRule(lessOrEqualThanZeroRule);
        //TODO: fix AUTO add a condition force range matching
        FilterRule matchingWrapperRule = new FilterRule();
        matchingWrapperRule.setCondition("AND");
        matchingWrapperRule
                .addRule(matchingRule)
                .addRule(allRangeMatchRule);
        boolean filterSender = matchingConditionForm.isHandleDuplicateSender();
        boolean filterSubject = matchingConditionForm.isHandleDuplicateSubject();
        boolean filterSameDomain = matchingConditionForm.isHandleSameDomain();
        boolean filterDomainInPartnerGroup = matchingConditionForm.isCheckDomainInPartnerGroup();
        List<Email> emailList = mailBoxService.getAll();
        logger.info("get EmailList done: " + emailList.size() + " emails");
//        boolean distinguish = matchingConditionForm.isDistinguish();
//        boolean spaceEffective = matchingConditionForm.isSpaceEffective();
        boolean distinguish = false;
        boolean spaceEffective = false;
        List<Email> matchSourceList;
        if(sourceRule.getRules().size() > 0) {
            matchSourceList = findMailMatching(emailList, sourceRule, distinguish, spaceEffective);
        } else {
            matchSourceList = emailList;
        }
        matchSourceList = mailBoxService.filterDuplicate(matchSourceList, filterSender, filterSubject);
        List<Email> matchDestinationList;
        if(destinationRule.getRules().size() > 0) {
            matchDestinationList = findMailMatching(emailList, destinationRule, distinguish, spaceEffective);
        } else {
            matchDestinationList = emailList;
        }
        matchDestinationList = mailBoxService.filterDuplicate(matchDestinationList, filterSender, filterSubject);
        logger.info("filter condition done: " + matchSourceList.size() + " " + matchDestinationList.size());
        List<String> matchingWords = getWordList(matchingConditionForm.getMatchingWords());
        List<MatchingWordResult> matchWordSource = findMatchWithWord(matchingWords, matchSourceList, spaceEffective, distinguish);
        List<MatchingWordResult> matchWordDestination = findMatchWithWord(matchingWords, matchDestinationList, spaceEffective, distinguish);
        logger.info("matching pharse word done: " + matchWordSource.size() + " " + matchWordDestination.size());
        ConcurrentHashMap<String, MatchingResult> matchingResultMap = new ConcurrentHashMap<String, MatchingResult>();
        HashMap<String, List<String>> domainsRelation = new HashMap<String, List<String>>();
        if(filterDomainInPartnerGroup){
        	domainsRelation = partnerService.getDomainRelationPartnerGroup();
    	}       
        for(MatchingWordResult sourceResult : matchWordSource) {
            Email sourceMail = sourceResult.getEmail();
            previewMailDTOList.put(sourceMail.getMessageId(), new PreviewMailDTO(sourceMail));
            for(String word : sourceResult.getWords()){
                addToList(matchingResultMap, word, sourceMail, null);
            }
            for(MatchingWordResult destinationResult : matchWordDestination) {
                if(filterSameDomain) {
                    boolean isSameDomain = matchingMailDomain(sourceResult, destinationResult);
                    if(isSameDomain) {
                        continue;
                    }
                }  
                if(filterDomainInPartnerGroup) {
                    boolean isDomainInPartnerGroup = matchingMailDomainInPartnerGroup(sourceResult, destinationResult, domainsRelation);
                    if(isDomainInPartnerGroup) {
                        continue;
                    }
                }
                
                List<String> realIntersectWords;
                if(matchingWords.size() == 0){
                    realIntersectWords = matchingWords;
                } else {
                    List<String> intersectWords = sourceResult.intersect(destinationResult);
                    if(intersectWords.size() == 0) continue;
                    realIntersectWords = intersectWords;
                }
                FilterRule copyMatchingRule = new FilterRule(matchingWrapperRule);
                MatchingPartResult matchingPartResult = isMailMatching(sourceResult, destinationResult, copyMatchingRule, distinguish);
                matchingPartResult.setSourceMail(sourceResult.getEmail());
                matchingPartResult.setDestinationMail(destinationResult.getEmail());
                matchingPartResult.setIntersectWords(realIntersectWords);
                if(matchingPartResult.isMatch()){
                    Email soureMail = matchingPartResult.getSourceMail();
                    Email destinationMail = matchingPartResult.getDestinationMail();
                    FullNumberRange matchRange = matchingPartResult.getMatchRange();
                    FullNumberRange range = matchingPartResult.getRange();
                    previewMailDTOList.put(destinationMail.getMessageId(), new PreviewMailDTO(destinationMail));
                    if(matchingWords.size() == 0){
                        addToList(matchingResultMap, null, soureMail, destinationMail, matchRange, range);
                    } else {
                        for(String word : matchingPartResult.getIntersectWords()) {
                            addToList(matchingResultMap, word, soureMail, destinationMail, matchRange, range);
                        }
                    }
                }
            }
        }
        matchingResults = new ArrayList<MatchingResult>(matchingResultMap.values());
        logger.info("Matching done: " + matchingResults.size());
        FinalMatchingResult result = new FinalMatchingResult(matchingResults, previewMailDTOList);
        this.increaseMatchingCounter();
        return result;
    }

    private boolean matchingMailDomain(MatchingWordResult sourceResult, MatchingWordResult destinationResult){
        Email sourceEmail = sourceResult.getEmail();
        String sourceEmailAddress = sourceEmail.getFrom();
        Email destinationEmail = destinationResult.getEmail();
        String destinationEmailAddress = destinationEmail.getFrom();
        return isSameDomain(sourceEmailAddress, destinationEmailAddress);
    }

    @Cacheable(key="\"MatchingConditionService:isSameDomain:\"+#a+'-'+#b")
    public boolean isSameDomain(String a, String b) {
        String aDomain = getEmailDomain(a);
        String bDomain = getEmailDomain(b);
        return aDomain.equalsIgnoreCase(bDomain);
    }
    
    private boolean matchingMailDomainInPartnerGroup(MatchingWordResult sourceResult, 
    		MatchingWordResult destinationResult, HashMap<String, List<String>>domainsRelation){
        Email sourceEmail = sourceResult.getEmail();
        String sourceEmailAddress = sourceEmail.getFrom();
        Email destinationEmail = destinationResult.getEmail();
        String destinationEmailAddress = destinationEmail.getFrom();      
        String domainSource = getDomainFromEmailAddress(sourceEmailAddress);
        if(domainSource==null) return false;
        String domainDestination = getDomainFromEmailAddress(destinationEmailAddress);
        if(domainDestination==null) return false;
        
        List<String> listDomainGroup = domainsRelation.get(domainSource);
        if(listDomainGroup==null) return false;
        
        for(String domain : listDomainGroup){
        	if(domainDestination.equals(domain)){
        		return true;
        	}
        }
        return false;
    }

    @Cacheable(key="\"MatchingConditionService:getEmailDomain:\"+#someEmail")
    public String getEmailDomain(String someEmail)
    {
        someEmail = someEmail != null ? someEmail : "";
        return  someEmail.substring(someEmail.indexOf("@") + 1);
    }

    public List<Email> findMailMatching(List<Email> emailList, FilterRule filterRule, boolean distinguish, boolean spaceEffective){
        List<Email> listResult = new ArrayList<Email>();
        for(Email email : emailList){
            if(checkMatchingRule(email, filterRule, distinguish, spaceEffective)){
                listResult.add(email);
            }
        }
        return listResult;
    }

    private boolean checkMatchingRule(Email email, FilterRule filterRule, boolean distinguish, boolean spaceEffective){
        if(filterRule.isGroup()){
            if(filterRule.getCondition().equalsIgnoreCase("AND")){
                for(FilterRule rule : filterRule.getRules()){
                    if(!checkMatchingRule(email, rule, distinguish, spaceEffective)){
                        return false;
                    }
                }
                return true;
            }else{
                for(FilterRule rule : filterRule.getRules()){
                    if(checkMatchingRule(email, rule, distinguish, spaceEffective)){
                        return true;
                    }
                }
                return false;
            }
        }else{
            return isMatch(email, filterRule, distinguish, spaceEffective);
        }
    }

    private MatchingPartResult isMailMatching(MatchingWordResult sourceResult,
                  MatchingWordResult destinationResult, FilterRule matchingRule, boolean distinguish){
        MatchingPartResult finalMatchingPartResult = new MatchingPartResult();
        FullNumberRange firstMatchRange = null;
        FullNumberRange firstRange = null;
        Email targetEmail = destinationResult.getEmail();
        if(matchingRule.isGroup()){
            if(matchingRule.hasSubRules()) {
                for(FilterRule rule : matchingRule.getRules()){
                    MatchingPartResult matchingPartResult = isMailMatching(sourceResult, destinationResult, rule, distinguish);
                    if(firstRange == null && firstMatchRange == null && ((matchingPartResult.getRange() != null) || (matchingPartResult.getMatchRange() != null))) {
                        firstMatchRange = matchingPartResult.getMatchRange();
                        firstRange = matchingPartResult.getRange();
                    }
                }
            } else {
                matchingRule.add(targetEmail);
            }
        } else {
            MatchingPartResult matchingPartResult = isMatch(sourceResult.getEmail(), targetEmail, matchingRule, distinguish);
            if(firstRange == null && firstMatchRange == null && ((matchingPartResult.getRange() != null) || (matchingPartResult.getMatchRange() != null))) {
                firstMatchRange = matchingPartResult.getMatchRange();
                firstRange = matchingPartResult.getRange();
            }
            if(matchingPartResult.isMatch()){
                matchingRule.add(targetEmail);
            }
        }

        List<Email> matching = matchingRule.getMatchEmails();
        finalMatchingPartResult.setMatch(matching.size() > 0);
        finalMatchingPartResult.setMatchRange(firstMatchRange);
        finalMatchingPartResult.setRange(firstRange);
        return finalMatchingPartResult;
    }
    
    public MatchingPartResult isMailMatchingEngineer(Email destinationResult,
            FilterRule matchingRule,
            boolean distinguish){
    	MatchingPartResult finalMatchingPartResult = new MatchingPartResult();
    	FullNumberRange firstMatchRange = null;
    	FullNumberRange firstRange = null;
    	Email targetEmail = destinationResult;
    	if(matchingRule.isGroup()){
    		if(matchingRule.hasSubRules()) {
    			for(FilterRule rule : matchingRule.getRules()){
    				MatchingPartResult matchingPartResult = isMailMatchingEngineer(destinationResult, rule, distinguish);
    				if(firstRange == null && firstMatchRange == null && ((matchingPartResult.getRange() != null) || (matchingPartResult.getMatchRange() != null))) {
    					firstMatchRange = matchingPartResult.getMatchRange();
    					firstRange = matchingPartResult.getRange();
    				}
    			}
    		} else {
    			matchingRule.add(targetEmail);
    		}
    	} else {
    		MatchingPartResult matchingPartResult = isMatchAllRange(targetEmail, matchingRule, distinguish);
    		if(firstRange == null && firstMatchRange == null && ((matchingPartResult.getRange() != null) || (matchingPartResult.getMatchRange() != null))) {
    			firstMatchRange = matchingPartResult.getMatchRange();
    			firstRange = matchingPartResult.getRange();
    		}
    		if(matchingPartResult.isMatch()){
    			matchingRule.add(targetEmail);
    		}		
    	}

    	List<Email> matching = matchingRule.getMatchEmails();
    	finalMatchingPartResult.setMatch(matching.size() > 0);
    	finalMatchingPartResult.setMatchRange(firstMatchRange);
    	finalMatchingPartResult.setRange(firstRange);
    	return finalMatchingPartResult;
    }

    private MatchingPartResult isMatch(Email source, Email target, FilterRule condition, boolean distinguish){
        MatchingPartResult result = new MatchingPartResult();
        boolean match = false;
        MailItemOption option = condition.getMailItemOption();
        switch (option){
            case SENDER:
                match = isMatchingPart(source.getFrom(), target, condition, distinguish);
                result.setMatch(match);
                break;
            case RECEIVER:
                match = isMatchingPart(source.getTo(), target, condition, distinguish);
                result.setMatch(match);
                break;
            case SUBJECT:
                match = isMatchingPart(source.getSubject(), target, condition, distinguish);
                result.setMatch(match);
                break;
            case BODY:
                match = isMatchingPart(source.getOptimizedBody(), target, condition, distinguish);
                result.setMatch(match);
                break;
            case NUMBER:
            case NUMBER_UPPER:
            case NUMBER_LOWER:
                result = isMatchRange(source, target, condition, distinguish);
                break;
            case NONE:
            default:
                break;
        }
        return result;
    }

    private boolean isMatch(Email email, FilterRule condition, boolean distinguish, boolean spaceEffective){
        boolean match = false;
        MailItemOption option = condition.getMailItemOption();
        ConditionOption conditionOption;
        switch (option){
            case SENDER:
                match = isMatchPart(email.getFrom(), condition, distinguish, spaceEffective);
                break;
            case RECEIVER:
                match = isMatchPart(email.getTo(), condition, distinguish, spaceEffective);
                break;
            case CC:
                match = isMatchPart(email.getCc(), condition, distinguish, spaceEffective);
                break;
            case BCC:
                match = isMatchPart(email.getBcc(), condition, distinguish, spaceEffective);
                break;
            case AND_RECEIVER_CC_BCC:
                conditionOption = condition.getConditionOption();
                switch (conditionOption){
                    case INC:
                    case NINC:
                    case EQ:
                    case NE:
                        match = isMatchPart(email.getTo(), condition, distinguish, spaceEffective)
                                && isMatchPart(email.getCc(), condition, distinguish, spaceEffective)
                                && isMatchPart(email.getBcc(), condition, distinguish, spaceEffective)
                                && isMatchPart(mailAccountsService.findAccountAddress(email.getAccountId()), condition, distinguish, spaceEffective);
                        break;
                    default:
                        break;
                }
                break;
            case OR_RECEIVER_CC_BCC:
                conditionOption = condition.getConditionOption();
                switch (conditionOption){
                    case INC:
                    case NINC:
                    case EQ:
                    case NE:
                        match = isMatchPart(email.getTo(), condition, distinguish, spaceEffective)
                                || isMatchPart(email.getCc(), condition, distinguish, spaceEffective)
                                || isMatchPart(email.getBcc(), condition, distinguish, spaceEffective)
                                || isMatchPart(mailAccountsService.findAccountAddress(email.getAccountId()), condition, distinguish, spaceEffective);
                        break;
                    default:
                        break;
                }
                break;
            case SUBJECT:
                match = isMatchPart(email.getSubject(), condition, distinguish, spaceEffective);
                break;
            case BODY:
                match = isMatchPart(email.getOptimizedBody(), condition, distinguish, spaceEffective);
                break;
            case AND_SUBJECT_BODY:
                match = isMatchPart(email.getSubject(), condition, distinguish, spaceEffective)
                        && isMatchPart(email.getOptimizedBody(), condition, distinguish, spaceEffective);
                break;
            case OR_SUBJECT_BODY:
                match = isMatchPart(email.getSubject(), condition, distinguish, spaceEffective)
                        || isMatchPart(email.getOptimizedBody(), condition, distinguish, spaceEffective);
                break;
            case MARK:
                match = isMatchPart(email.getMark(), condition, distinguish, spaceEffective);
                break;
            case NUMBER:
            case NUMBER_UPPER:
            case NUMBER_LOWER:
                MatchingPartResult matchingPartResult = isMatchAllRange(email, condition, distinguish);
                match = matchingPartResult.isMatch();
                break;
            case ATTACHMENT:
                if(condition.getValue().equalsIgnoreCase(Email.HAS_ATTACHMENT)){
                    match = email.isHasAttachment();
                } else {
                    match = !email.isHasAttachment();
                }
                break;
            case RECEIVED_DATE:
                match = isMatchPart(email.getSentAt(), condition, distinguish);
                break;
            case NONE:
            default:
                break;
        }
        return match;
    }

    private boolean isMatchingPart (String part,Email target, FilterRule condition, boolean distinguish){
        boolean match = false;
        ConditionOption option = condition.getConditionOption();
        String optimizedPart = getOptimizedText(part, distinguish);
        String optimizedValue = getTargetPartValue(target, condition, distinguish);
        switch (option){
            case INC:
                match = optimizedPart.indexOf(optimizedValue) >= 0;
                break;
            case NINC:
                match = optimizedPart.indexOf(optimizedValue) == -1;
                break;
            case EQ:
                match = optimizedPart.equals(optimizedValue);
                break;
            case NE:
                match = !optimizedPart.equals(optimizedValue);
                break;
            default:
                break;
        }
        return match;
    }

    private String getTargetPartValue(Email target, FilterRule condition, boolean distinguish){
        String conditionValue = condition.getValue();
        String optimizedValue = getOptimizedText(conditionValue, distinguish);
        String targetPart = optimizedValue;
        MatchingItemOption option = MatchingItemOption.fromText(conditionValue);
        if(option == null) return targetPart;
        switch (option){
            case SENDER:
                targetPart = target.getFrom();
                break;
            case RECEIVER:
                targetPart = target.getTo();
                break;
            case SUBJECT:
                targetPart = target.getSubject();
                break;
            case BODY:
                targetPart = target.getOptimizedBody();
                break;
            case NUMBER:
            case NUMBER_LOWER:
            case NUMBER_UPPER:
                targetPart = target.getOptimizedText(distinguish);
                break;
            default:
                break;
        }
        return targetPart;
    }

    private boolean isMatchPart (String part, FilterRule condition, boolean distinguish, boolean spaceEffective){
        boolean match = false;
        ConditionOption option = condition.getConditionOption();
        String conditionValue = condition.getValue();
        String optimizedPart = getOptimizedText(part, distinguish);
        String optimizedValue = getOptimizedText(conditionValue, distinguish);
        switch (option){
            case INC:
                match = emailWordJobService.matchWord(optimizedPart, optimizedPart, optimizedValue, spaceEffective);
                break;
            case NINC:
                match = !emailWordJobService.matchWord(optimizedPart, optimizedPart, optimizedValue, spaceEffective);
                break;
            case EQ:
                match = optimizedPart.equals(optimizedValue);
                break;
            case NE:
                match = !optimizedPart.equals(optimizedValue);
                break;
            case GE:
            case GT:
            case LE:
            case LT:
            case WITHIN:
            case NONE:
            default:
                break;
        }
        return match;
    }

    private synchronized boolean isMatchPart (Date part, FilterRule condition, boolean distinguish) {
        //TODO: condition value date string may have many format???;
        if(part == null) return false;
        boolean match = false;
        ConditionOption option = condition.getConditionOption();
        String dateValue = condition.getValue();
        try {
//            ConditionOption option = ConditionOption.fromValue(condition.getCondition());
//            String dateValue = condition.getValue();
            Date conditionDate;
            if(dateValue.matches("-?\\d+")){
                Date now = new Date();
                conditionDate = Utils.addDayToDate(now, Integer.parseInt(dateValue));
                conditionDate = Utils.trim(conditionDate);
            } else {
                conditionDate = Utils.parseDateStr(dateValue);
            }
            long diff = part.getTime() - conditionDate.getTime();
            switch (option){
                case EQ:
                    match = diff >= 0 && diff < ONE_DAY_MILLISECONDS;
                    break;
                case NE:
                    match = diff >= ONE_DAY_MILLISECONDS || diff < 0;
                    break;
                case GE:
                    match = diff >= 0;
                    break;
                case GT:
                    match = diff >= ONE_DAY_MILLISECONDS;
                    break;
                case LE:
                    match = diff < ONE_DAY_MILLISECONDS;
                    break;
                case LT:
                    match = diff < 0;
                    break;
                case INC:
                case NINC:
                case WITHIN:
                case NONE:
                default:
                    break;
            }
        } catch (Exception e){
            logger.warn("dateValue: " + dateValue);
            e.printStackTrace();
        }
        return match;
    }

    public String getOptimizedText(String text, boolean distinguish){
        if(text == null) {
            text = "";
        }
        if(distinguish){
            return text.toLowerCase();
        } else {
            int conv_op_flags = 0;
            conv_op_flags |= KanaConverter.OP_HAN_KATA_TO_ZEN_KATA;
            conv_op_flags |= KanaConverter.OP_ZEN_ASCII_TO_HAN_ASCII;
            String japaneseOptimizedText = KanaConverter.convertKana(text, conv_op_flags);
            return japaneseOptimizedText.toLowerCase();
        }
    }

    private List<Email> mergeResultGroups(List<MatchingConditionGroup> groups){
        List<Email> result = new ArrayList<>();
        for(MatchingConditionGroup group : groups){
            CombineOption option = group.getCombineOption();
            List<Email> emailList;
            if(groups.indexOf(group) == 0){
                emailList = mergeResultWithAGroup(group);
                result = mergeWithoutDuplicate(result, emailList);
            } else {
                switch (option){
                    case NONE:
                        break;
                    case AND:
                        emailList = mergeResultWithAGroup(group);
                        result = findDuplicateList(result, emailList);
                        break;
                    case OR:
                        emailList = mergeResultWithAGroup(group);
                        result = mergeWithoutDuplicate(result, emailList);
                        break;
                }
            }
        }
        return result;
    }

    private List<Email> mergeResultWithAGroup(MatchingConditionGroup group){
        List<Email> result = new ArrayList<>();
        List<MatchingConditionResult> conditionResults = group.getConditionResults();
        for(MatchingConditionResult conditionResult : conditionResults){
            CombineOption option = conditionResult.getCombineOption();
            List<Email> emailList = conditionResult.getEmailList();
            if(conditionResults.indexOf(conditionResult) == 0){
                result = mergeWithoutDuplicate(result, emailList);
            } else {
                switch (option){
                    case NONE:
                        break;
                    case AND:
                        result = findDuplicateList(result, emailList);
                        break;
                    case OR:
                        result = mergeWithoutDuplicate(result, emailList);
                        break;
                }
            }
        }
        return result;
    }

    public synchronized static List<Email> mergeWithoutDuplicate(List<Email> list1, List<Email> list2){
        List<Email> list1Copy = new ArrayList<>(list1);
        List<Email> list2Copy = new ArrayList<>(list2);
        list2Copy.removeAll(list1Copy);
        list1Copy.addAll(list2Copy);
        return list1Copy;
    }

    public synchronized static List<Email> findDuplicateList(List<Email> list1, List<Email> list2){
        List<Email> list = list1.size() >= list2.size() ? list2 : list1;
        List<Email> remainList = list1.size() >= list2.size() ? list1 : list2;
        List<Email> result = new ArrayList<>();
        for(Email email: list){
            if(remainList.contains(email)){
                result.add(email);
            }
        }
        return result;
    }

    private List<MatchingWordResult> findMatchWithWord(List<String> words, List<Email> emailList, boolean spaceEffective, boolean distinguish){
        List<MatchingWordResult> matchingWordResults = new ArrayList<>();
        for(Email email : emailList){
            MatchingWordResult result = emailWordJobService.matchWords(email, words, spaceEffective, distinguish);
            matchingWordResults.add(result);
        }
        return matchingWordResults;
    }

    private MatchingPartResult isMatchRange(Email source, Email target, FilterRule condition, boolean distinguish){
        MatchingPartResult result = new MatchingPartResult();
        String conditionValue = condition.getValue();
        MatchingItemOption option = MatchingItemOption.fromText(conditionValue);
        if(option == null){
            return isMatchRange(target, condition, distinguish);
        } else {
            ConditionOption conditionOption = condition.getConditionOption();
            String optimizedSourcePart = source.getOptimizedText(false);
            String optimizedTargetPart = getTargetPartValue(target, condition, false);
            List<FullNumberRange> sourceRanges;
            List<FullNumberRange> targetRanges;
            switch (conditionOption){
                case WITHIN:
                case EQ:
                case NE:
                case GE:
                case GT:
                case LE:
                case LT:
                    String postFixCacheId = conditionValue;
                    if(conditionValue.indexOf("数値") > 0) {
                        postFixCacheId = "";
                    }
                    sourceRanges = getMailRanges(source, source.getMessageId(), optimizedSourcePart);
                    targetRanges = getMailRanges(target, target.getMessageId()+postFixCacheId, optimizedTargetPart);
                    result = hasMatchRange(sourceRanges, targetRanges, condition);
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private MatchingPartResult isMatchRange(Email target, FilterRule condition, boolean distinguish){
        MatchingPartResult result = new MatchingPartResult();
        try {
            MailItemOption mailItemOption = condition.getMailItemOption();
            ConditionOption conditionOption = condition.getConditionOption();
            String conditionValue = condition.getValue();
            String optimizedPart = target.getOptimizedText(false);
            String optimizedValue = getOptimizedText(conditionValue, false);

            FullNumberRange findRange;
            List<FullNumberRange> toFindListRange;

            double ratio = 1;
            if(numberTreatment != null){
                switch (mailItemOption){
                    case NUMBER_UPPER:
                        ratio = numberTreatment.getUpperLimitRate();
                        break;
                    case NUMBER_LOWER:
                        ratio = numberTreatment.getLowerLimitRate();
                        break;
                }
            }

            switch (conditionOption){
                case WITHIN:
                    String cacheId = optimizedValue + "OwsCacheIdRandom89172398";
                    List<FullNumberRange> forFindListRange = numberRangeService.buildNumberRangeForInput(cacheId, optimizedValue);
                    toFindListRange = getMailRanges(target, target.getMessageId(), optimizedPart);
                    if(forFindListRange.size() == 0){
                        result.setMatch(true);
                        if(toFindListRange.size() > 0){
                            result.setMatchRange(toFindListRange.get(0));
                        }
                        return result;
                    }
                    findRange = forFindListRange.get(0);
                    if(toFindListRange.size() > 0){
                        for(FullNumberRange range : toFindListRange){
                            if(range.match(findRange, ratio)){
                                result.setMatch(true);
                                result.setMatchRange(range);
                                result.setRange(findRange);
                                break;
                            }
                        }
                    } else {
                        result.setMatch(true);
                        result.setRange(findRange);
                    }
                    break;
                case EQ:
                case NE:
                case GE:
                case GT:
                case LE:
                case LT:
                    optimizedValue = optimizedValue.replaceAll(",", "");
                    Double numberCondition = Double.parseDouble(optimizedValue);
                    NumberCompare compare = NumberCompare.fromConditionOption(conditionOption);
                    SimpleNumberRange simpleRange = new SimpleNumberRange(compare, numberCondition);
                    findRange = new FullNumberRange(simpleRange);
                    toFindListRange = getMailRanges(target, target.getMessageId(), optimizedPart);
                    if(toFindListRange.size() > 0){
                        for(FullNumberRange range : toFindListRange){
                            if(range.match(findRange, ratio)){
                                result.setMatch(true);
                                result.setMatchRange(range);
                                result.setRange(findRange);
                                break;
                            }
                        }
                    } else { //Not found a range => auto natch
                        result.setMatch(true);
                        result.setRange(findRange);
                    }
                    break;
                default:
                    break;
            }
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException: " + e.getMessage());
        }
        return result;
    }

    private MatchingPartResult isMatchAllRange(Email target, FilterRule condition, boolean distinguish){
        MatchingPartResult result = new MatchingPartResult();
        try {
            MailItemOption mailItemOption = condition.getMailItemOption();
            ConditionOption conditionOption = condition.getConditionOption();
            String conditionValue = condition.getValue();
            String optimizedPart = target.getOptimizedText(false);
            String optimizedValue = getOptimizedText(conditionValue, false);

            FullNumberRange findRange;
            List<FullNumberRange> toFindListRange;

            double ratio = 1;
            if(numberTreatment != null){
                switch (mailItemOption){
                    case NUMBER_UPPER:
                        ratio = numberTreatment.getUpperLimitRate();
                        break;
                    case NUMBER_LOWER:
                        ratio = numberTreatment.getLowerLimitRate();
                        break;
                }
            }

            switch (conditionOption){
                case WITHIN:
                    String cacheId = optimizedValue + "OwsCacheIdRandom89172398";
                    List<FullNumberRange> forFindListRange = numberRangeService.buildNumberRangeForInput(cacheId, optimizedValue);
                    toFindListRange = numberRangeService.buildNumberRangeForInput(target.getMessageId(), optimizedPart);
                    if(forFindListRange.size() == 0){
                        result.setMatch(true);
                        if(toFindListRange.size() > 0){
                            result.setMatchRange(toFindListRange.get(0));
                        }
                        return result;
                    }
                    findRange = forFindListRange.get(0);
                    target.setRangeList(new ArrayList<>());
                    if(toFindListRange.size() > 0){
                        for(FullNumberRange range : toFindListRange){
                            if(range.match(findRange, ratio)){
                                if(!result.isMatch()){
                                    result.setMatch(true);
                                    result.setMatchRange(range);
                                    result.setRange(findRange);
                                }
                                List<FullNumberRange> targetRangeList = target.getRangeList();
                                targetRangeList.add(range);
                            }
                        }
                    } else {
                        result.setMatch(true);
                        result.setRange(findRange);
                    }
                    break;
                case EQ:
                case NE:
                case GE:
                case GT:
                case LE:
                case LT:
                    optimizedValue = optimizedValue.replaceAll(",", "");
                    Double numberCondition = Double.parseDouble(optimizedValue);
                    NumberCompare compare = NumberCompare.fromConditionOption(conditionOption);
                    SimpleNumberRange simpleRange = new SimpleNumberRange(compare, numberCondition);
                    findRange = new FullNumberRange(simpleRange);
                    toFindListRange = numberRangeService.buildNumberRangeForInput(target.getMessageId(), optimizedPart);
                    target.setRangeList(new ArrayList<>());
                    if(toFindListRange.size() > 0){
                        for(FullNumberRange range : toFindListRange){
                            if(range.match(findRange, ratio)){
                                if(!result.isMatch()){
                                    result.setMatch(true);
                                    result.setMatchRange(range);
                                    result.setRange(findRange);
                                }
                                List<FullNumberRange> targetRangeList = target.getRangeList();
                                targetRangeList.add(range);
                            }
                        }
                    } else { //Not found a range => auto natch
                        result.setMatch(true);
                        result.setRange(findRange);
                    }
                    break;
                default:
                    break;
            }
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException: " + e.getMessage());
        }
        return result;
    }

    public  List<String> getWordList(String matchingWprdsStr){
        List<String> normalizedMatchingWords = new ArrayList<>();
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(matchingWprdsStr);
        while (m.find()) {
            matchingWprdsStr = matchingWprdsStr.replaceAll(m.group(), "");
            String word = m.group(1);
            word = word.toLowerCase();
            if(word.isEmpty()) continue;
            if(!normalizedMatchingWords.contains(word)){
                normalizedMatchingWords.add(word);
            }
        }
        List<String> matchingWords = Arrays.asList(matchingWprdsStr.split(","));
        for(String word : matchingWords) {
            word = word.toLowerCase();
            if(word.isEmpty()) continue;
            if(!normalizedMatchingWords.contains(word)){
                normalizedMatchingWords.add(word);
            }
        }
        return normalizedMatchingWords;
    }

    public  MatchingPartResult hasMatchRange(List<FullNumberRange> sourceRanges, List<FullNumberRange> targetRanges, FilterRule condition) {
        MatchingPartResult result = new MatchingPartResult();
        if(targetRanges.size() == 0) {
            if(sourceRanges.size() > 0) {
                result.setRange(sourceRanges.get(0));
            }
            result.setMatch(true);
            return result;
        }
        if(sourceRanges.size() == 0) {
            if(targetRanges.size() > 0) {
                result.setMatchRange(targetRanges.get(0));
            }
            result.setMatch(true);
            return result;
        }
        MailItemOption mailItemOption = condition.getMailItemOption();
        ConditionOption conditionOption = condition.getConditionOption();
        MatchingItemOption matchingOption = MatchingItemOption.fromText(condition.getValue());
        double ratio = 1;
        if(numberTreatment != null){
            if(mailItemOption.equals(MailItemOption.NUMBER_UPPER)){
                ratio = ratio * numberTreatment.getUpperLimitRate();
            } else if (mailItemOption.equals(MailItemOption.NUMBER_LOWER)){
                ratio = ratio * numberTreatment.getLowerLimitRate();
            }

            if(matchingOption != null && matchingOption.equals(MatchingItemOption.NUMBER_UPPER)){
                ratio = ratio / numberTreatment.getUpperLimitRate();
            } else if (matchingOption != null && matchingOption.equals(MatchingItemOption.NUMBER_LOWER)){
                ratio = ratio / numberTreatment.getLowerLimitRate();
            }
        }
        for(FullNumberRange findRange : sourceRanges){
            NumberCompare replaceCompare = NumberCompare.fromConditionOption(conditionOption);
            for(FullNumberRange range : targetRanges){
                if(findRange.match(range, ratio, replaceCompare)){
                    result.setMatch(true);
                    result.setMatchRange(range);
                    result.setRange(findRange);
                    break;
                }
            }
            if(result.isMatch()) break;
        }
        return result;
    }

    private synchronized void addToList(ConcurrentHashMap<String, MatchingResult> map, String word, Email source, Email destination) {
        this.addToList(map, word, source, destination, null, null);
    }

    private synchronized void addToList(ConcurrentHashMap<String, MatchingResult> map, String word, Email source, Email destination, FullNumberRange matchRange, FullNumberRange range) {
        String mapKey = word + "+" + source.getMessageId();
        MatchingResult matchingResult = map.get(mapKey);
        if(matchingResult == null) {
            matchingResult = new MatchingResult(word, source);
            if(destination != null) {
                matchingResult.addDestination(destination, matchRange, range);
            }
            map.put(mapKey, matchingResult);
        } else {
            if(destination != null) {
                matchingResult.addDestination(destination, matchRange, range);
            }
        }
    }

    private Date getDaysAgo(){
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        return new Date(System.currentTimeMillis() - (daysago * DAY_IN_MS));
    }

    public List<FullNumberRange> getMailRanges(Email email, String cacheId, String input) {
        List<FullNumberRange> mailRanges = email.getRangeList();
        return mailRanges != null && mailRanges.size() > 0 ? mailRanges : numberRangeService.buildNumberRangeForInput(cacheId, input);
    }
    
    public String getDomainFromEmailAddress(String emailAddress){
    	int index = emailAddress.indexOf("@");
		if(index<=0) return null;
		String domainEmail =  emailAddress.substring(index+1).toLowerCase();
		return domainEmail;
    }

    private void increaseMatchingCounter() {
        this.setMatchingCounter(this.getMatchingCounter() + 1);
    }

    private void setMatchingCounter(int counter) {
        matchingCounter = counter;
    }

    private int getMatchingCounter() {
        return matchingCounter;
    }

    public int getMatchingCount() {
        return this.getMatchingCounter();
    }
}
