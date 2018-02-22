package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.ReplaceNumber;
import io.owslab.mailreceiver.model.ReplaceUnit;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * Created by khanhlvb on 2/11/18.
 */
public class NumberTreatmentForm {

    @NotBlank
    private String name;

    @NotBlank
    private String upperLimitName;
    private String upperLimitSign;
    private Double upperLimitRate;

    @NotBlank
    private String lowerLimitName;
    private String lowerLimitSign;
    private Double lowerLimitRate;

    private List<ReplaceNumber> replaceNumberList;
    private List<ReplaceUnit> replaceUnitList;

    public NumberTreatmentForm() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpperLimitName() {
        return upperLimitName;
    }

    public void setUpperLimitName(String upperLimitName) {
        this.upperLimitName = upperLimitName;
    }

    public String getUpperLimitSign() {
        return upperLimitSign;
    }

    public void setUpperLimitSign(String upperLimitSign) {
        this.upperLimitSign = upperLimitSign;
    }

    public Double getUpperLimitRate() {
        return upperLimitRate;
    }

    public void setUpperLimitRate(Double upperLimitRate) {
        this.upperLimitRate = upperLimitRate;
    }

    public String getLowerLimitName() {
        return lowerLimitName;
    }

    public void setLowerLimitName(String lowerLimitName) {
        this.lowerLimitName = lowerLimitName;
    }

    public String getLowerLimitSign() {
        return lowerLimitSign;
    }

    public void setLowerLimitSign(String lowerLimitSign) {
        this.lowerLimitSign = lowerLimitSign;
    }

    public Double getLowerLimitRate() {
        return lowerLimitRate;
    }

    public void setLowerLimitRate(Double lowerLimitRate) {
        this.lowerLimitRate = lowerLimitRate;
    }

    public List<ReplaceNumber> getReplaceNumberList() {
        return replaceNumberList;
    }

    public void setReplaceNumberList(List<ReplaceNumber> replaceNumberList) {
        this.replaceNumberList = replaceNumberList;
    }

    public List<ReplaceUnit> getReplaceUnitList() {
        return replaceUnitList;
    }

    public void setReplaceUnitList(List<ReplaceUnit> replaceUnitList) {
        this.replaceUnitList = replaceUnitList;
    }
}
