package io.owslab.mailreceiver.service.matching;

import io.owslab.mailreceiver.dao.MatchingConditionDAO;
import io.owslab.mailreceiver.form.MatchingConditionForm;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.MatchingCondition;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<MatchingCondition> groupedSourceConditions = filterGroupCondition(sourceConditionList);
        List<MatchingCondition> groupedDestinationConditions = filterGroupCondition(destinationConditionList);
        List<MatchingCondition> groupedMatchingConditions = filterGroupCondition(matchingConditionList);
        logger.info("start find mail");
        List<Email> emailList = mailBoxService.getAll();
        logger.info("find mail done: " + emailList.size());
        logger.info("condition size: " + groupedSourceConditions.size() + " " + groupedDestinationConditions.size() + " " + groupedMatchingConditions.size());
    }

    private List<MatchingCondition> filterGroupCondition(List<MatchingCondition> conditions){
        List<MatchingCondition> result = new ArrayList<MatchingCondition>();
        for(MatchingCondition condition : conditions){
            if(condition.isGroup()){
                result.add(condition);
            }
        }
        return result;
    }
}
