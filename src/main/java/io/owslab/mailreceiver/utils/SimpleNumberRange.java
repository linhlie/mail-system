package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.enums.NumberCompare;

import java.util.Objects;

/**
 * Created by khanhlvb on 3/8/18.
 */
public class SimpleNumberRange {
    private NumberCompare numberCompare;
    private double value;
    private int replaceValue;
    private double rawValue;
    private String replaceNumberText;
    private String replaceUnitText;
    private String replaceLetterText;
    private boolean bfNumber;
    private boolean containComma;

    public SimpleNumberRange(double value, double rawValue, String replaceNumberText, String replaceUnitText, String replaceLetterText, boolean bfNumber, boolean containComma) {
        this(value, 4, 1, rawValue, replaceNumberText, replaceUnitText, replaceLetterText, bfNumber, containComma);
    }

    public SimpleNumberRange(double value, int replace, double rawValue, String replaceNumberText, String replaceUnitText, String replaceLetterText, boolean bfNumber, boolean containComma) {
        this(value, replace, 1, rawValue, replaceNumberText, replaceUnitText, replaceLetterText, bfNumber, containComma);
    }

    public SimpleNumberRange(double value, int replace, int replaceValue, double rawValue, String replaceNumberText, String replaceUnitText, String replaceLetterText, boolean bfNumber, boolean containComma) {
        this.value = value;
        this.numberCompare = NumberCompare.fromReplace(replace);
        this.replaceValue = replaceValue;
        this.rawValue = rawValue;
        this.replaceNumberText = replaceNumberText;
        this.replaceUnitText = replaceUnitText;
        this.replaceLetterText = replaceLetterText;
        this.bfNumber = bfNumber;
        this.containComma = containComma;
    }

    public SimpleNumberRange(NumberCompare numberCompare, double value) {
        this.numberCompare = numberCompare;
        this.value = value;
        this.rawValue = value;
        this.replaceValue = 1;
        this.containComma = false;
    }

    public SimpleNumberRange(double value) {
        this.value = value;
        this.numberCompare = NumberCompare.EQ;
        this.replaceValue = 1;
        this.containComma = false;
    }

    public SimpleNumberRange(double value, int replace) {
        this.value = value;
        this.numberCompare = NumberCompare.fromReplace(replace);
        this.replaceValue = 1;
        this.containComma = false;
    }

    public SimpleNumberRange(){
        this.value = 0;
        this.numberCompare = NumberCompare.AUTOMATCH;
        this.replaceValue = 1;
        this.containComma = false;
    }

    public NumberCompare getNumberCompare() {
        return numberCompare;
    }

    public NumberCompare getNumberCompare(NumberCompare replaceCompare) {
        if(replaceCompare != null) {
            return  replaceCompare;
        }
        return numberCompare;
    }

    public int getReplaceValue() {
        return replaceValue;
    }

    public void setReplaceValue(int replaceValue) {
        this.replaceValue = replaceValue;
    }

    public void setNumberCompare(NumberCompare numberCompare) {
        this.numberCompare = numberCompare;
    }

    public double getValue() {
        return value;
    }

    public double getValue(double ratio) {
        return value*ratio;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getRawValue() {
        return rawValue;
    }

    public double getRawValue(double ratio) {
        return rawValue*ratio;
    }

    public void setRawValue(double rawValue) {
        this.rawValue = rawValue;
    }

    public String getReplaceNumberText() {
        return replaceNumberText;
    }

    public void setReplaceNumberText(String replaceNumberText) {
        this.replaceNumberText = replaceNumberText;
    }

    public String getReplaceUnitText() {
        return replaceUnitText;
    }

    public void setReplaceUnitText(String replaceUnitText) {
        this.replaceUnitText = replaceUnitText;
    }

    public String getReplaceLetterText() {
        return replaceLetterText;
    }

    public void setReplaceLetterText(String replaceLetterText) {
        this.replaceLetterText = replaceLetterText;
    }

    public boolean isBfNumber() {
        return bfNumber;
    }

    public void setBfNumber(boolean bfNumber) {
        this.bfNumber = bfNumber;
    }

    public boolean isContainComma() {
        return containComma;
    }

    public void setContainComma(boolean containComma) {
        this.containComma = containComma;
    }

    public boolean match(SimpleNumberRange other, double ratio) {
        return this.match(other, ratio, null);
    }

    public boolean match(SimpleNumberRange other, double ratio, NumberCompare replaceCompare){
        if(this.getNumberCompare().equals(NumberCompare.AUTOMATCH)){
            return true;
        }
        if(other.getNumberCompare().equals(NumberCompare.AUTOMATCH)){
            return true;
        }
        boolean match = false;
        switch (this.getNumberCompare()){
            case EQ:
                switch (other.getNumberCompare(replaceCompare)){
                    case EQ:
                        match = this.getValue(ratio) == other.getValue();
                        break;
                    case NE:
                        match = this.getValue(ratio) != other.getValue();
                        break;
                    case GE:
                        match = this.getValue(ratio) >= other.getValue();
                        break;
                    case GT:
                        match = this.getValue(ratio) > other.getValue();
                        break;
                    case LE:
                        match = this.getValue(ratio) <= other.getValue();
                        break;
                    case LT:
                        match = this.getValue(ratio) < other.getValue();
                        break;
                    default:
                        match = false;
                }
                break;
            case NE:
                switch (other.getNumberCompare(replaceCompare)){
                    case EQ:
                        match = this.getValue(ratio) != other.getValue();
                        break;
                    case NE:
                        match = this.getValue(ratio) == other.getValue();
                        break;
                    case GE:
                        match = true;
                        break;
                    case GT:
                        match = true;
                        break;
                    case LE:
                        match = true;
                        break;
                    case LT:
                        match = true;
                        break;
                    default:
                        match = false;
                }
                break;
            case GE:
                switch (other.getNumberCompare(replaceCompare)){
                    case EQ:
                        match = this.getValue(ratio) <= other.getValue();
                        break;
                    case NE:
                        match = true;
                        break;
                    case GE:
                        match = true;
                        break;
                    case GT:
                        match = true;
                        break;
                    case LE:
                        match = this.getValue(ratio) <= other.getValue();
                        break;
                    case LT:
                        match = this.getValue(ratio) < other.getValue();
                        break;
                    default:
                        match = false;
                }
                break;
            case GT:
                switch (other.getNumberCompare(replaceCompare)){
                    case EQ:
                        match = this.getValue(ratio) <= other.getValue();
                        break;
                    case NE:
                        match = true;
                        break;
                    case GE:
                        match = true;
                        break;
                    case GT:
                        match = true;
                        break;
                    case LE:
                        match = this.getValue(ratio) < other.getValue();
                        break;
                    case LT:
                        match = this.getValue(ratio) < other.getValue();
                        break;
                    default:
                        match = false;
                }
                break;
            case LE:
                switch (other.getNumberCompare(replaceCompare)){
                    case EQ:
                        match = this.getValue(ratio) >= other.getValue();
                        break;
                    case NE:
                        match = true;
                        break;
                    case GE:
                        match = this.getValue(ratio) >= other.getValue();
                        break;
                    case GT:
                        match = this.getValue(ratio) > other.getValue();
                        break;
                    case LE:
                        match = true;
                        break;
                    case LT:
                        match = true;
                        break;
                    default:
                        match = false;
                }
                break;
            case LT:
                switch (other.getNumberCompare(replaceCompare)){
                    case EQ:
                        match = this.getValue(ratio) > other.getValue();
                        break;
                    case NE:
                        match = true;
                        break;
                    case GE:
                        match = this.getValue(ratio) > other.getValue();
                        break;
                    case GT:
                        match = this.getValue(ratio) > other.getValue();
                        break;
                    case LE:
                        match = true;
                        break;
                    case LT:
                        match = true;
                        break;
                    default:
                        match = false;
                }
                break;
            default:
                match = false;
        }
        return match;
    }

    public String toString(){
        return toString(1);
    }

    public String toString(double multiple){
        if(this.getNumberCompare().equals(NumberCompare.AUTOMATCH)){
            return "";
        }

        String result;

        double value = this.getRawValue(multiple);
        String valueStr;
        if((value % 1) == 0){
            valueStr = this.isContainComma() ? Utils.formatNumber((int)value) : Integer.toString((int)value);
        } else {
            value = NumberFormat.round(value, 2);
            valueStr = this.isContainComma() ? Utils.formatNumber(value) : Double.toString(value);
        }
        result = valueStr + Objects.toString(this.getReplaceNumberText(), "") + Objects.toString(this.getReplaceUnitText(), "");
        result = this.isBfNumber() ? Objects.toString(this.getReplaceLetterText(), "") + result : result + Objects.toString(this.getReplaceLetterText(), "");
        return result;
    }

    public void multiple(Double multipleWidth){
        this.setValue(this.getValue() * multipleWidth);
    }

    public void replace(NumberCompare numberCompare){
        if(!this.numberCompare.equals(NumberCompare.AUTOMATCH)){
            this.numberCompare = numberCompare;
        }
    }
}
