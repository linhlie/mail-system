package io.owslab.mailreceiver.service.matching;

import com.mariten.kanatools.KanaConverter;
import io.owslab.mailreceiver.dao.MatchingConditionDAO;
import io.owslab.mailreceiver.enums.*;
import io.owslab.mailreceiver.form.MatchingConditionForm;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.MatchingCondition;
import io.owslab.mailreceiver.model.NumberTreatment;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.replace.NumberRangeService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.word.EmailWordJobService;
import io.owslab.mailreceiver.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by khanhlvb on 3/6/18.
 */
@Service
public class MatchingConditionService {

    private static final Logger logger = LoggerFactory.getLogger(MatchingConditionService.class);

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

    private NumberTreatment numberTreatment;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

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

    public List<MatchingResult> matching(MatchingConditionForm matchingConditionForm){
        logger.info("start matching");
        List<MatchingResult> results = new ArrayList<>();
        numberTreatment = numberTreatmentService.getFirst();
        List<MatchingCondition> sourceConditionList = matchingConditionForm.getSourceConditionList();
        List<MatchingCondition> destinationConditionList = matchingConditionForm.getDestinationConditionList();
        List<MatchingCondition> matchingConditionList = matchingConditionForm.getMatchingConditionList();
        List<MatchingConditionGroup> groupedSourceConditions = divideIntoGroups(sourceConditionList);
        List<MatchingConditionGroup> groupedDestinationConditions = divideIntoGroups(destinationConditionList);
        List<Email> emailList = mailBoxService.getAll();
        logger.info("get EmailList done: " + emailList.size() + " emails");
        boolean distinguish = matchingConditionForm.isDistinguish();
        List<Email> matchSourceList;
        if(groupedSourceConditions.size() > 0) {
            findMailMatching(emailList, groupedSourceConditions, distinguish);
            matchSourceList = mergeResultGroups(groupedSourceConditions);
        } else {
            matchSourceList = emailList;
        }
        List<Email> matchDestinationList;
        if(groupedDestinationConditions.size() > 0) {
            findMailMatching(emailList, groupedDestinationConditions, distinguish);
            matchDestinationList = mergeResultGroups(groupedDestinationConditions);
        } else {
            matchDestinationList = emailList;
        }
        logger.info("filter condition done: " + matchSourceList.size() + " " + matchDestinationList.size());
        List<String> matchingWords = getWordList(matchingConditionForm);
        List<MatchingWordResult> matchWordSource = findMatchWithWord(matchingWords, matchSourceList);
        List<MatchingWordResult> matchWordDestination = findMatchWithWord(matchingWords, matchDestinationList);
//        System.out.println(matchSourceList.size() + " " + matchDestinationList.size());
        logger.info("matching pharse word done: " + matchWordSource.size() + " " + matchWordDestination.size());
        ConcurrentHashMap<String, MatchingResult> matchingResultMap = new ConcurrentHashMap<String, MatchingResult>();
//        preBuildRanges(matchingConditionList, matchWordSource, matchWordDestination);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Callable<MatchingPartResult>> callables = new ArrayList<>();
        for(MatchingWordResult sourceResult : matchWordSource) {
            for(String word : sourceResult.getWords()){
                addToList(matchingResultMap, word, sourceResult.getEmail(), null);
            }
            for(MatchingWordResult destinationResult : matchWordDestination) {
                List<String> intersectWords = sourceResult.intersect(destinationResult);
                if(intersectWords.size() == 0) continue;
                callables.add(toCallable(intersectWords, matchingConditionList, sourceResult, destinationResult, distinguish));
            }
        }
        logger.info("start range invokeAll pharse: " + callables.size());
        try {
            List<Future<MatchingPartResult>> futures = executorService.invokeAll(callables);
            executorService.shutdown();
            for(Future<MatchingPartResult> future: futures) {
                MatchingPartResult result = future.get();
                if(result.isMatch()){
                    for(String word : result.getIntersectWords()) {
                        addToList(matchingResultMap, word, result.getSourceMail(), result.getDestinationMail(), result.getMatchRange(), result.getRange());
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        results = new ArrayList<MatchingResult>(matchingResultMap.values());
        logger.info("Matching done: " + results.size());
        return results;
    }

    private Callable<MatchingPartResult> toCallable(List<String> intersectWords, List<MatchingCondition> matchingConditionList,
                                      MatchingWordResult sourceResult, MatchingWordResult destinationResult,
                                      boolean distinguish) {
        return new Callable<MatchingPartResult>() {
            public MatchingPartResult call() {
                List<MatchingConditionGroup> groupedMatchingConditions = divideIntoGroups(matchingConditionList);
                MatchingPartResult matchingPartResult = groupedMatchingConditions.size() == 0 ?
                        new MatchingPartResult(true) : isMailMatching(sourceResult, destinationResult, groupedMatchingConditions, distinguish);
                matchingPartResult.setSourceMail(sourceResult.getEmail());
                matchingPartResult.setDestinationMail(destinationResult.getEmail());
                matchingPartResult.setIntersectWords(intersectWords);
                return matchingPartResult;
            }
        };
    }

    private void preBuildRanges(List<MatchingCondition> conditionList, List<MatchingWordResult> matchSource, List<MatchingWordResult> matchDestination){
        boolean mustPreBuild = false;
        for(MatchingCondition condition : conditionList) {
            MailItemOption option = MailItemOption.fromValue(condition.getItem());
            if(option.equals(MailItemOption.NUMBER) || option.equals(MailItemOption.NUMBER_LOWER) || option.equals(MailItemOption.NUMBER_UPPER)) {
                mustPreBuild = true;
                break;
            }
        }

        if(!mustPreBuild) return;
        ExecutorService executorService= Executors.newFixedThreadPool(10);
        List<Callable<Void>> callableList=new ArrayList<Callable<Void>>();
        for(MatchingWordResult result : matchSource){
            if(result.hasMatchWord()) {
                callableList.add(preBuildRange(result.getEmail()));
            }
        }
        for(MatchingWordResult result : matchDestination){
            if(result.hasMatchWord()) {
                callableList.add(preBuildRange(result.getEmail()));
            }
        }
        try {
            executorService.invokeAll(callableList);
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return;
    }

    private Callable<Void> preBuildRange(Email email){
        Callable<Void> callable = new Callable<Void>(){
            public Void call() {
            numberRangeService.buildNumberRangeForInput(email.getMessageId(), email.getOptimizedText(false));
                return null;
            }
        };

        return callable;
    }

    private List<MatchingConditionGroup> divideIntoGroups(List<MatchingCondition> conditions){
        List<MatchingConditionGroup> result = new ArrayList<MatchingConditionGroup>();
        MatchingConditionGroup group = new MatchingConditionGroup();
        for(MatchingCondition condition : conditions){
            if(condition.getRemove() != MatchingCondition.Remove.REMOVED) {
                if(!condition.isGroup()){
                    if(!group.isEmpty()){
                        result.add(group);
                    }
                    group = new MatchingConditionGroup();
                }
                group.add(new MatchingConditionResult(condition));
            }
            if(conditions.indexOf(condition) == (conditions.size() - 1)) {
                result.add(group);
            }
        }
        return result;
    }

    private List<MatchingConditionGroup> findMailMatching(List<Email> emailList, List<MatchingConditionGroup> groupList, boolean distinguish){
        for(MatchingConditionGroup group : groupList){
            for(MatchingConditionResult result : group.getConditionResults()){
                ExecutorService executorService= Executors.newFixedThreadPool(20);
                List<Callable<Email>> callableList=new ArrayList<Callable<Email>>();
                for(Email email : emailList){
                    callableList.add(getMatchingConditionResultCallable(email, result, distinguish));
                }
                try {
                    List<Future<Email>> futures = executorService.invokeAll(callableList);
                    executorService.shutdown();
                    for(Future<Email> future: futures) {
                        Email email = future.get();
                        if(email != null){
                            result.add(email);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return groupList;
    }

    private Callable<Email> getMatchingConditionResultCallable(Email email, MatchingConditionResult result, boolean distinguish){
        Callable<Email> callable = new Callable<Email>(){
            public Email call() {
                MatchingCondition condition = result.getMatchingCondition();
                if(isMatch(email, condition, distinguish)){
                    return email;
                } else {
                    return null;
                }
            }
        };

        return callable;
    }

    private MatchingPartResult isMailMatching(MatchingWordResult sourceResult,
                                                          MatchingWordResult destinationResult,
                                                          List<MatchingConditionGroup> groupList,
                                                          boolean distinguish){
        MatchingPartResult finalMatchingPartResult = new MatchingPartResult();
        FullNumberRange firstMatchRange = null;
        FullNumberRange firstRange = null;
        for(MatchingConditionGroup group : groupList){
            for(MatchingConditionResult result : group.getConditionResults()){
                MatchingCondition condition = result.getMatchingCondition();
                Email targetEmail = destinationResult.getEmail();
                MatchingPartResult matchingPartResult = isMatch(sourceResult.getEmail(), targetEmail, condition, distinguish);
                if(firstMatchRange == null && matchingPartResult.getMatchRange() != null) {
                    firstMatchRange = matchingPartResult.getMatchRange();
                    firstRange = matchingPartResult.getRange();
                }
                if(matchingPartResult.isMatch()){
                    result.add(targetEmail);
                }
            }
        }

        List<Email> matching = mergeResultGroups(groupList);
        finalMatchingPartResult.setMatch(matching.size() > 0);
        finalMatchingPartResult.setMatchRange(firstMatchRange);
        finalMatchingPartResult.setRange(firstRange);
        return finalMatchingPartResult;
    }

    private MatchingPartResult isMatch(Email source, Email target, MatchingCondition condition, boolean distinguish){
        MatchingPartResult result = new MatchingPartResult();
        boolean match = false;
        MailItemOption option = MailItemOption.fromValue(condition.getItem());
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

    private boolean isMatch(Email email, MatchingCondition condition, boolean distinguish){
        boolean match = false;
        MailItemOption option = MailItemOption.fromValue(condition.getItem());
        switch (option){
            case SENDER:
                match = isMatchPart(email.getFrom(), condition, distinguish);
                break;
            case RECEIVER:
                match = isMatchPart(email.getTo(), condition, distinguish);
                break;
            case SUBJECT:
                match = isMatchPart(email.getSubject(), condition, distinguish);
                break;
            case BODY:
                match = isMatchPart(email.getOptimizedBody(), condition, distinguish);
                break;
            case NUMBER:
            case NUMBER_UPPER:
            case NUMBER_LOWER:
                MatchingPartResult matchingPartResult = isMatchRange(email, condition, distinguish);
                match = matchingPartResult.isMatch();
                break;
            case HAS_ATTACHMENT:
                match = email.isHasAttachment();
                break;
            case NO_ATTACHMENT:
                match = !email.isHasAttachment();
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

    private boolean isMatchingPart (String part,Email target, MatchingCondition condition, boolean distinguish){
        boolean match = false;
        ConditionOption option = ConditionOption.fromValue(condition.getCondition());
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

    private String getTargetPartValue(Email target, MatchingCondition condition, boolean distinguish){
        String conditionValue = condition.getValue();
        String optimizedValue = getOptimizedText(conditionValue, distinguish);
        String targetPart = optimizedValue;
        MatchingItemOption option = MatchingItemOption.fromText(conditionValue);
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
                targetPart = target.getSubjectAndOptimizedBody();
                break;
            default:
                break;
        }
        return targetPart;
    }

    private boolean isMatchPart (String part, MatchingCondition condition, boolean distinguish){
        boolean match = false;
        ConditionOption option = ConditionOption.fromValue(condition.getCondition());
        String conditionValue = condition.getValue();
        String optimizedPart = getOptimizedText(part, distinguish);
        String optimizedValue = getOptimizedText(conditionValue, distinguish);
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

    private boolean isMatchPart (Date part, MatchingCondition condition, boolean distinguish) {
        //TODO: condition value date string may have many format???;
        if(part == null) return false;
        boolean match = false;
        try {
            ConditionOption option = ConditionOption.fromValue(condition.getCondition());
            String dateValue = condition.getValue();
            Date conditionDate = formatter.parse(dateValue);
            conditionDate = Utils.trim(conditionDate);
            part = Utils.trim(part);
            switch (option){
                case EQ:
                    match = part.compareTo(conditionDate) == 0;
                    break;
                case NE:
                    match = part.compareTo(conditionDate)  != 0;
                    break;
                case GE:
                    match = part.compareTo(conditionDate)  >= 0;
                    break;
                case GT:
                    match = part.compareTo(conditionDate)  > 0;
                    break;
                case LE:
                    match = part.compareTo(conditionDate)  <= 0;
                    break;
                case LT:
                    match = part.compareTo(conditionDate)  < 0;
                    break;
                case INC:
                case NINC:
                case WITHIN:
                case NONE:
                default:
                    break;
            }
        } catch (ParseException e){
            logger.error(e.getMessage());
        }
        return match;
    }

    private String getOptimizedText(String text, boolean distinguish){
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

    private List<Email> mergeWithoutDuplicate(List<Email> list1, List<Email> list2){
        List<Email> list1Copy = new ArrayList<>(list1);
        List<Email> list2Copy = new ArrayList<>(list2);
        list2Copy.removeAll(list1Copy);
        list1Copy.addAll(list2Copy);
        return list1Copy;
    }

    private List<Email> findDuplicateList(List<Email> list1, List<Email> list2){
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

//    private List<MatchingWordResult> findMatchWithWord(List<String> words, List<Email> emailList){
//        List<MatchingWordResult> matchingWordResults = new ArrayList<>();
//        for(Email email : emailList){
//            String contentToSearch = email.getSubjectAndOptimizedBody();
//            MatchingWordResult result = new MatchingWordResult(email);
//            for(String word : words){
//                if(emailWordJobService.matchWord(email.getMessageId(), contentToSearch, word)){
//                    result.addMatchWord(word);
//                }
//            }
//            if(result.hasMatchWord()){
//                matchingWordResults.add(result);
//            }
//        }
//        return matchingWordResults;
//    }

    private List<MatchingWordResult> findMatchWithWord(List<String> words, List<Email> emailList){
        ExecutorService executorService= Executors.newFixedThreadPool(50);
        List<Callable<MatchingWordResult>> callableList =new ArrayList<Callable<MatchingWordResult>>();
        List<MatchingWordResult> matchingWordResults = new ArrayList<>();

        for(Email email : emailList){
            callableList.add(getInstanceOfCallable(words, email));
        }
        try {
            List<Future<MatchingWordResult>> futures = executorService.invokeAll(callableList);
            executorService.shutdown();
            for(Future<MatchingWordResult> future: futures) {
                MatchingWordResult result = future.get();
                if(result != null){
                    matchingWordResults.add(result);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return matchingWordResults;
    }

    private Callable<MatchingWordResult> getInstanceOfCallable(final List<String> words, final Email email) {

        Callable<MatchingWordResult> clientPlanCall=new Callable<MatchingWordResult>(){
            public MatchingWordResult call() {
                MatchingWordResult result = emailWordJobService.matchWords(email, words);
                if(result.hasMatchWord()){
                    return result;
                } else {
                    return null;
                }
            }
        };

        return clientPlanCall;
    }

    private MatchingPartResult isMatchRange(Email source, Email target, MatchingCondition condition, boolean distinguish){
        MatchingPartResult result = new MatchingPartResult();
        String conditionValue = condition.getValue();
        MatchingItemOption option = MatchingItemOption.fromText(conditionValue);
        if(option == null){
            return isMatchRange(target, condition, distinguish);
        } else {
            ConditionOption conditionOption = ConditionOption.fromValue(condition.getCondition());
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
                    sourceRanges = numberRangeService.buildNumberRangeForInput(source.getMessageId(), optimizedSourcePart);
                    targetRanges = numberRangeService.buildNumberRangeForInput(target.getMessageId()+postFixCacheId, optimizedTargetPart);
                    result = hasMatchRange(sourceRanges, targetRanges, condition);
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private MatchingPartResult isMatchRange(Email target, MatchingCondition condition, boolean distinguish){
        MatchingPartResult result = new MatchingPartResult();
        try {
            MailItemOption mailItemOption = MailItemOption.fromValue(condition.getItem());
            ConditionOption conditionOption = ConditionOption.fromValue(condition.getCondition());
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
                    if(forFindListRange.size() == 0){
                        result.setMatch(true);
                        return result;
                    }
                    findRange = forFindListRange.get(0);
                    toFindListRange = numberRangeService.buildNumberRangeForInput(target.getMessageId(), optimizedPart);
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
                    }
                    break;
                case EQ:
                case NE:
                case GE:
                case GT:
                case LE:
                case LT:
                    Double numberCondition = Double.parseDouble(optimizedValue);
                    NumberCompare compare = NumberCompare.fromConditionOption(conditionOption);
                    SimpleNumberRange simpleRange = new SimpleNumberRange(compare, numberCondition);
                    findRange = new FullNumberRange(simpleRange);
                    toFindListRange = numberRangeService.buildNumberRangeForInput(target.getMessageId(), optimizedPart);
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



    private List<String> getWordList(MatchingConditionForm matchingConditionForm){
        List<String> matchingWords = Arrays.asList(matchingConditionForm.getMatchingWords().split(","));
        List<String> normalizedMatchingWords = new ArrayList<>();
        for(String word : matchingWords) {
            word = word.toLowerCase();
            if(word.isEmpty()) continue;
            if(!normalizedMatchingWords.contains(word)){
                normalizedMatchingWords.add(word);
            }
        }
        return normalizedMatchingWords;
    }

    private MatchingPartResult hasMatchRange(List<FullNumberRange> sourceRanges, List<FullNumberRange> targetRanges, MatchingCondition condition) {
        if(targetRanges.size() == 0) return new MatchingPartResult(true);
        MatchingPartResult result = new MatchingPartResult();
        MailItemOption mailItemOption = MailItemOption.fromValue(condition.getItem());
        ConditionOption conditionOption = ConditionOption.fromValue(condition.getCondition());
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
}
