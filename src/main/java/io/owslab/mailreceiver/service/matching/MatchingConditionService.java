package io.owslab.mailreceiver.service.matching;

import com.mariten.kanatools.KanaConverter;
import io.owslab.mailreceiver.dao.MatchingConditionDAO;
import io.owslab.mailreceiver.enums.ConditionOption;
import io.owslab.mailreceiver.enums.MailItemOption;
import io.owslab.mailreceiver.form.MatchingConditionForm;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.MatchingCondition;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.utils.MatchingConditionGroup;
import io.owslab.mailreceiver.utils.MatchingConditionResult;
import io.owslab.mailreceiver.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        return getListByType(MatchingCondition.Type.SOURCE);
    }

    public List<MatchingCondition> getDestinationConditionList(){
        return getListByType(MatchingCondition.Type.DESTINATION);
    }

    public List<MatchingCondition> getMatchingConditionList(){
        return getListByType(MatchingCondition.Type.MATCHING);
    }

    private List<MatchingCondition> getListByType(int type){
        return matchingConditionDAO.findByType(type);
    }

    public void matching(MatchingConditionForm matchingConditionForm){
        List<MatchingCondition> sourceConditionList = matchingConditionForm.getSourceConditionList();
        List<MatchingCondition> destinationConditionList = matchingConditionForm.getDestinationConditionList();
        List<MatchingCondition> matchingConditionList = matchingConditionForm.getMatchingConditionList();
        List<MatchingConditionGroup> groupedSourceConditions = divideIntoGroups(sourceConditionList);
        List<MatchingConditionGroup> groupedDestinationConditions = divideIntoGroups(destinationConditionList);
        List<MatchingConditionGroup> groupedMatchingConditions = divideIntoGroups(matchingConditionList);
        List<MatchingConditionGroup> groupedList = new ArrayList<MatchingConditionGroup>();
        groupedList.addAll(groupedSourceConditions);
        groupedList.addAll(groupedDestinationConditions);
        groupedList.addAll(groupedMatchingConditions);
        List<Email> emailList = mailBoxService.getAll();
        boolean distinguish = matchingConditionForm.isDistinguish();
        findMailMatching(emailList, groupedList, distinguish);
//        logger.info("find mail done: " + emailList.size());
//        logger.info("condition size: " + groupedSourceConditions.size() + " " + groupedDestinationConditions.size() + " " + groupedMatchingConditions.size());
//        logger.info("groupedList size: " + groupedList.size());
//        for(MatchingConditionGroup group : groupedSourceConditions){
//            System.out.println("Group " + groupedSourceConditions.indexOf(group) + ":" + group.toString());
//        }
    }

    private List<MatchingConditionGroup> divideIntoGroups(List<MatchingCondition> conditions){
        List<MatchingConditionGroup> result = new ArrayList<MatchingConditionGroup>();
        MatchingConditionGroup group = new MatchingConditionGroup();
        for(MatchingCondition condition : conditions){
            if(condition.getRemove() == MatchingCondition.Remove.REMOVED) continue;
            if(!condition.isGroup()){
                if(!group.isEmpty()){
                    result.add(group);
                }
                group = new MatchingConditionGroup();
            }
            group.add(new MatchingConditionResult(condition));
            if(conditions.indexOf(condition) == (conditions.size() - 1)) {
                result.add(group);
            }
        }
        return result;
    }

    private List<MatchingConditionGroup> findMailMatching(List<Email> emailList, List<MatchingConditionGroup> groupList, boolean distinguish){
        int countMatch = 0;
        for(Email email : emailList){
            for(MatchingConditionGroup group : groupList){
                for(MatchingConditionResult result : group.getConditionResults()){
                    MatchingCondition condition = result.getMatchingCondition();
                    if(isMatch(email, condition, distinguish)){
                        result.add(email);
                        countMatch ++;
                        System.out.println("Matching: " + countMatch + " " + condition.toString() + " | " + email.getSubject());
                    }
                }
            }
        }
        return groupList;
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
                break;
            case NUMBER_UPPER:
                break;
            case NUMBER_LOWER:
                break;
            case HAS_ATTACHMENT:
                match = email.isHasAttachment();
                break;
            case NO_ATTACHMENT:
                match = !email.isHasAttachment();
                break;
            case RECEIVED_DATE:
                match = isMatchPart(email.getReceivedAt(), condition, distinguish);
                break;
            case NONE:
            default:
                break;
        }
        return match;
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
        //TODO: condition value date string many format;
        if(part == null) return false;
        boolean match = false;
        try {
            ConditionOption option = ConditionOption.fromValue(condition.getCondition());
            String dateValue = condition.getValue();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date conditionDate = formatter.parse(dateValue);
            conditionDate = Utils.trim(conditionDate);
            part = Utils.trim(part);
            switch (option){
                case EQ:
                    match = conditionDate.compareTo(part) == 0;
                    break;
                case NE:
                    match = conditionDate.compareTo(part) != 0;
                    break;
                case GE:
                    match = conditionDate.compareTo(part) >= 0;
                    break;
                case GT:
                    match = conditionDate.compareTo(part) > 0;
                    break;
                case LE:
                    match = conditionDate.compareTo(part) <= 0;
                    break;
                case LT:
                    match = conditionDate.compareTo(part) < 0;
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
}
