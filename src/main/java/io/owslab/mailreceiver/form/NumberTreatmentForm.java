package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.NumberTreatment;
import io.owslab.mailreceiver.model.ReplaceLetter;
import io.owslab.mailreceiver.model.ReplaceNumber;
import io.owslab.mailreceiver.model.ReplaceUnit;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by khanhlvb on 2/11/18.
 */
public class NumberTreatmentForm {

    @NotBlank
    private String name;

    @NotBlank
    private String upperLimitName;
    private int upperLimitSign;
    @NotNull
    private Double upperLimitRate;

    @NotBlank
    private String lowerLimitName;
    private int lowerLimitSign;
    @NotNull
    private Double lowerLimitRate;

    private Double leftBoundaryValue;
    private int leftBoundaryOperator;
    private int combineOperator;
    private Double rightBoundaryValue;
    private int rightBoundaryOperator;

    private boolean enableReplaceLetter;

    private List<ReplaceNumber> replaceNumberList;
    private List<ReplaceUnit> replaceUnitList;
    private List<ReplaceLetter> replaceLetterList;

    public NumberTreatmentForm() {
    }

    public NumberTreatmentForm(NumberTreatment numberTreatment) {
        if(numberTreatment != null){
            this.setNumberTreatment(numberTreatment);
        }
    }

    public void setNumberTreatment(NumberTreatment numberTreatment){
        this.name = numberTreatment.getName();
        this.upperLimitName = numberTreatment.getUpperLimitName();
        this.upperLimitSign = numberTreatment.getUpperLimitSign();
        this.upperLimitRate = numberTreatment.getUpperLimitRate();
        this.lowerLimitName = numberTreatment.getLowerLimitName();
        this.lowerLimitSign = numberTreatment.getLowerLimitSign();
        this.lowerLimitRate = numberTreatment.getLowerLimitRate();
        this.leftBoundaryValue = numberTreatment.getLeftBoundaryValue();
        this.leftBoundaryOperator = numberTreatment.getLeftBoundaryOperator();
        this.combineOperator = numberTreatment.getCombineOperator();
        this.rightBoundaryValue = numberTreatment.getRightBoundaryValue();
        this.rightBoundaryOperator = numberTreatment.getRightBoundaryOperator();
        this.enableReplaceLetter = numberTreatment.isEnableReplaceLetter();
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

    public List<ReplaceLetter> getReplaceLetterList() {
        return replaceLetterList;
    }

    public void setReplaceLetterList(List<ReplaceLetter> replaceLetterList) {
        this.replaceLetterList = replaceLetterList;
    }

    public int getUpperLimitSign() {
        return upperLimitSign;
    }

    public void setUpperLimitSign(int upperLimitSign) {
        this.upperLimitSign = upperLimitSign;
    }

    public int getLowerLimitSign() {
        return lowerLimitSign;
    }

    public void setLowerLimitSign(int lowerLimitSign) {
        this.lowerLimitSign = lowerLimitSign;
    }

    public Double getLeftBoundaryValue() {
        return leftBoundaryValue;
    }

    public void setLeftBoundaryValue(Double leftBoundaryValue) {
        this.leftBoundaryValue = leftBoundaryValue;
    }

    public int getLeftBoundaryOperator() {
        return leftBoundaryOperator;
    }

    public void setLeftBoundaryOperator(int leftBoundaryOperator) {
        this.leftBoundaryOperator = leftBoundaryOperator;
    }

    public int getCombineOperator() {
        return combineOperator;
    }

    public void setCombineOperator(int combineOperator) {
        this.combineOperator = combineOperator;
    }

    public Double getRightBoundaryValue() {
        return rightBoundaryValue;
    }

    public void setRightBoundaryValue(Double rightBoundaryValue) {
        this.rightBoundaryValue = rightBoundaryValue;
    }

    public int getRightBoundaryOperator() {
        return rightBoundaryOperator;
    }

    public void setRightBoundaryOperator(int rightBoundaryOperator) {
        this.rightBoundaryOperator = rightBoundaryOperator;
    }

    public boolean isEnableReplaceLetter() {
        return enableReplaceLetter;
    }

    public void setEnableReplaceLetter(boolean enableReplaceLetter) {
        this.enableReplaceLetter = enableReplaceLetter;
    }
}
