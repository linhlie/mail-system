package io.owslab.mailreceiver.model;

import io.owslab.mailreceiver.form.NumberTreatmentForm;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by khanhlvb on 2/23/18.
 */
@Entity
@Table(name = "Number_Treatments")
public class NumberTreatment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String name;

    @NotNull
    private String upperLimitName;

    private int upperLimitSign;

    @NotNull
    private Double upperLimitRate;

    @NotNull
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

    private boolean enablePrettyNumber;

    private int prettyNumberStep;

    public NumberTreatment() {}

    public NumberTreatment(long id) {
        this.id = id;
    }

    public NumberTreatment(String name, String upperLimitName, int upperLimitSign, Double upperLimitRate,
                           String lowerLimitName, int lowerLimitSign, Double lowerLimitRate, Double leftBoundaryValue,
                           int leftBoundaryOperator, int combineOperator, Double rightBoundaryValue,
                           int rightBoundaryOperator, boolean enableReplaceLetter, boolean enablePrettyNumber, int prettyNumberStep) {
        this.name = name;
        this.upperLimitName = upperLimitName;
        this.upperLimitSign = upperLimitSign;
        this.upperLimitRate = upperLimitRate;
        this.lowerLimitName = lowerLimitName;
        this.lowerLimitSign = lowerLimitSign;
        this.lowerLimitRate = lowerLimitRate;
        this.leftBoundaryValue = leftBoundaryValue;
        this.leftBoundaryOperator = leftBoundaryOperator;
        this.combineOperator = combineOperator;
        this.rightBoundaryValue = rightBoundaryValue;
        this.rightBoundaryOperator = rightBoundaryOperator;
        this.enableReplaceLetter = enableReplaceLetter;
        this.enablePrettyNumber = enablePrettyNumber;
        this.prettyNumberStep = prettyNumberStep;
    }

    public NumberTreatment(NumberTreatmentForm form) {
        this.name = form.getName();
        this.upperLimitName = form.getUpperLimitName();
        this.upperLimitSign = form.getUpperLimitSign();
        this.upperLimitRate = form.getUpperLimitRate();
        this.lowerLimitName = form.getLowerLimitName();
        this.lowerLimitSign = form.getLowerLimitSign();
        this.lowerLimitRate = form.getLowerLimitRate();
        this.leftBoundaryValue = form.getLeftBoundaryValue();
        this.leftBoundaryOperator = form.getLeftBoundaryOperator();
        this.combineOperator = form.getCombineOperator();
        this.rightBoundaryValue = form.getRightBoundaryValue();
        this.rightBoundaryOperator = form.getRightBoundaryOperator();
        this.enableReplaceLetter = form.isEnableReplaceLetter();
        this.enablePrettyNumber = form.isEnablePrettyNumber();
        this.prettyNumberStep = form.getPrettyNumberStep();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getUpperLimitSign() {
        return upperLimitSign;
    }

    public void setUpperLimitSign(int upperLimitSign) {
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

    public int getLowerLimitSign() {
        return lowerLimitSign;
    }

    public void setLowerLimitSign(int lowerLimitSign) {
        this.lowerLimitSign = lowerLimitSign;
    }

    public Double getLowerLimitRate() {
        return lowerLimitRate;
    }

    public void setLowerLimitRate(Double lowerLimitRate) {
        this.lowerLimitRate = lowerLimitRate;
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

    public boolean isEnablePrettyNumber() {
        return enablePrettyNumber;
    }

    public void setEnablePrettyNumber(boolean enablePrettyNumber) {
        this.enablePrettyNumber = enablePrettyNumber;
    }

    public int getPrettyNumberStep() {
        return prettyNumberStep;
    }

    public void setPrettyNumberStep(int prettyNumberStep) {
        this.prettyNumberStep = prettyNumberStep;
    }

    public class CombineOperators {
        public static final int AND = 0;
        public static final int OR = 1;
    }

    public class BoundaryOperators {
        public static final int GE = 0;
        public static final int LE = 1;
        public static final int LT = 2;
        public static final int GT = 3;
    }
}
