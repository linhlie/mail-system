package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.MatchingCondition;
import io.owslab.mailreceiver.model.ReplaceLetter;
import io.owslab.mailreceiver.model.ReplaceNumber;
import io.owslab.mailreceiver.model.ReplaceUnit;
import io.owslab.mailreceiver.utils.FilterRule;

import java.util.List;

/**
 * Created by khanhlvb on 3/6/18.
 */
public class MatchingConditionForm {

    private boolean distinguish;
    private boolean spaceEffective;
    private String matchingWords;
    private FilterRule sourceConditionData;
    private FilterRule destinationConditionData;
    private FilterRule matchingConditionData;

    public MatchingConditionForm() {
    }

    public FilterRule getSourceConditionData() {
        return sourceConditionData;
    }

    public void setSourceConditionData(FilterRule sourceConditionData) {
        this.sourceConditionData = sourceConditionData;
    }

    public FilterRule getDestinationConditionData() {
        return destinationConditionData;
    }

    public void setDestinationConditionData(FilterRule destinationConditionData) {
        this.destinationConditionData = destinationConditionData;
    }

    public FilterRule getMatchingConditionData() {
        return matchingConditionData;
    }

    public void setMatchingConditionData(FilterRule matchingConditionData) {
        this.matchingConditionData = matchingConditionData;
    }

    public boolean isDistinguish() {
        return distinguish;
    }

    public void setDistinguish(boolean distinguish) {
        this.distinguish = distinguish;
    }

    public String getMatchingWords() {
        return matchingWords;
    }

    public void setMatchingWords(String matchingWords) {
        this.matchingWords = matchingWords;
    }

    public boolean isSpaceEffective() {
        return spaceEffective;
    }

    public void setSpaceEffective(boolean spaceEffective) {
        this.spaceEffective = spaceEffective;
    }
}
