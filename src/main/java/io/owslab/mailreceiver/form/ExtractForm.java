package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.utils.FilterRule;

public class ExtractForm {

    private FilterRule conditionData;
    private boolean distinguish;
    private boolean spaceEffective;
    private boolean handleDuplicateSender;
    private boolean handleDuplicateSubject;

    private int type;

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

    public boolean isHandleDuplicateSender() {
        return handleDuplicateSender;
    }

    public void setHandleDuplicateSender(boolean handleDuplicateSender) {
        this.handleDuplicateSender = handleDuplicateSender;
    }

    public boolean isHandleDuplicateSubject() {
        return handleDuplicateSubject;
    }

    public void setHandleDuplicateSubject(boolean handleDuplicateSubject) {
        this.handleDuplicateSubject = handleDuplicateSubject;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
