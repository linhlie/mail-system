package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.utils.FilterRule;

public class ExtractForm {

    private FilterRule conditionData;
    private boolean distinguish;
    private boolean spaceEffective;

    public ExtractForm() {
    }

    public FilterRule getConditionData() {
        return conditionData;
    }

    public void setConditionData(FilterRule conditionData) {
        this.conditionData = conditionData;
    }

    public boolean isDistinguish() {
        return distinguish;
    }

    public void setDistinguish(boolean distinguish) {
        this.distinguish = distinguish;
    }

    public boolean isSpaceEffective() {
        return spaceEffective;
    }

    public void setSpaceEffective(boolean spaceEffective) {
        this.spaceEffective = spaceEffective;
    }
}
