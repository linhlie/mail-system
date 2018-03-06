package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.MatchingCondition;
import io.owslab.mailreceiver.model.ReplaceLetter;
import io.owslab.mailreceiver.model.ReplaceNumber;
import io.owslab.mailreceiver.model.ReplaceUnit;

import java.util.List;

/**
 * Created by khanhlvb on 3/6/18.
 */
public class MatchingConditionForm {

    private List<MatchingCondition> sourceConditionList;
    private List<MatchingCondition> destinationConditionList;
    private List<MatchingCondition> matchingConditionList;

    public MatchingConditionForm() {
    }

    public List<MatchingCondition> getSourceConditionList() {
        return sourceConditionList;
    }

    public void setSourceConditionList(List<MatchingCondition> sourceConditionList) {
        this.sourceConditionList = sourceConditionList;
    }

    public List<MatchingCondition> getDestinationConditionList() {
        return destinationConditionList;
    }

    public void setDestinationConditionList(List<MatchingCondition> destinationConditionList) {
        this.destinationConditionList = destinationConditionList;
    }

    public List<MatchingCondition> getMatchingConditionList() {
        return matchingConditionList;
    }

    public void setMatchingConditionList(List<MatchingCondition> matchingConditionList) {
        this.matchingConditionList = matchingConditionList;
    }
}
