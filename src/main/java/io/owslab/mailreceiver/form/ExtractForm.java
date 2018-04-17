package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.MatchingCondition;
import io.owslab.mailreceiver.model.ReplaceLetter;
import io.owslab.mailreceiver.model.ReplaceNumber;
import io.owslab.mailreceiver.model.ReplaceUnit;

import java.util.List;

public class ExtractForm {

    private List<MatchingCondition> conditionList;
    private boolean distinguish;

    public ExtractForm() {
    }

    public List<MatchingCondition> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<MatchingCondition> conditionList) {
        this.conditionList = conditionList;
    }

    public boolean isDistinguish() {
        return distinguish;
    }

    public void setDistinguish(boolean distinguish) {
        this.distinguish = distinguish;
    }
}
