package io.owslab.mailreceiver.service.matching;

import io.owslab.mailreceiver.dao.MatchingConditionDAO;
import io.owslab.mailreceiver.model.MatchingCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 3/6/18.
 */
@Service
public class MatchingConditionService {

    @Autowired
    private MatchingConditionDAO matchingConditionDAO;

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
}
