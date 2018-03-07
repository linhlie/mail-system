package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.model.MatchingCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 3/7/18.
 */
public class MatchingConditionGroup {
    private List<MatchingCondition> matchingConditionList;

    public MatchingConditionGroup(List<MatchingCondition> matchingConditionList) {
        this.matchingConditionList = matchingConditionList;
    }

    public MatchingConditionGroup() {
        this.matchingConditionList = new ArrayList<MatchingCondition>();
    }

    public List<MatchingCondition> getMatchingConditionList() {
        return matchingConditionList;
    }

    public void setMatchingConditionList(List<MatchingCondition> matchingConditionList) {
        this.matchingConditionList = matchingConditionList;
    }

    public boolean isEmpty(){
        return this.matchingConditionList.isEmpty();
    }

    public boolean add(MatchingCondition condition){
        return this.matchingConditionList.add(condition);
    }

    public String toString(){
        String result = "";
        for(MatchingCondition matchingCondition : matchingConditionList){
            result = result + "\n"  + matchingCondition.getItem() + " " + matchingCondition.getValue();
        }
        result = result + "\n";
        return result;
    }

}
