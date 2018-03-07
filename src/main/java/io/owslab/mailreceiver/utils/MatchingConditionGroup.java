package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.enums.CombineOption;
import io.owslab.mailreceiver.model.MatchingCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 3/7/18.
 */
public class MatchingConditionGroup {
    private List<MatchingConditionResult> conditionResults;

    public MatchingConditionGroup(List<MatchingConditionResult> conditionResults) {
        this.conditionResults = conditionResults;
    }

    public MatchingConditionGroup() {
        this.conditionResults = new ArrayList<MatchingConditionResult>();
    }

    public List<MatchingConditionResult> getConditionResults() {
        return conditionResults;
    }

    public void setConditionResults(List<MatchingConditionResult> conditionResults) {
        this.conditionResults = conditionResults;
    }

    public boolean isEmpty(){
        return this.conditionResults.isEmpty();
    }

    public boolean add(MatchingConditionResult result){
        return this.conditionResults.add(result);
    }

    public String toString(){
        String result = "";
        for(MatchingConditionResult conditionResult : conditionResults){
            result = result + "\n"  + conditionResult.getMatchingCondition().toString();
        }
        result = result + "\n";
        return result;
    }

    public CombineOption getCombineOption(){
        if(conditionResults.size() > 0){
            return (conditionResults.get(0)).getCombineOption();
        } else {
            //TODO: what should default option
            return CombineOption.NONE;
        }
    }

}
