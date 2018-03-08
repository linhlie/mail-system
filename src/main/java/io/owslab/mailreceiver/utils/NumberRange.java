package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.enums.NumberCompare;

/**
 * Created by khanhlvb on 3/8/18.
 */
public class NumberRange {
    private NumberCompare numberCompare;
    private double value;

    public NumberRange(NumberCompare numberCompare, double value) {
        this.numberCompare = numberCompare;
        this.value = value;
    }

    public NumberRange(double value) {
        this.value = value;
        this.numberCompare = NumberCompare.EQ;
    }

    public NumberCompare getNumberCompare() {
        return numberCompare;
    }

    public void setNumberCompare(NumberCompare numberCompare) {
        this.numberCompare = numberCompare;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean match(NumberRange other){
        boolean match = false;
        switch (this.getNumberCompare()){
            case EQ:
                switch (other.getNumberCompare()){
                    case EQ:
                        match = this.getValue() == other.getValue();
                        break;
                    case NE:
                        match = this.getValue() != other.getValue();
                        break;
                    case GE:
                        match = this.getValue() >= other.getValue();
                        break;
                    case GT:
                        match = this.getValue() > other.getValue();
                        break;
                    case LE:
                        match = this.getValue() <= other.getValue();
                        break;
                    case LT:
                        match = this.getValue() < other.getValue();
                        break;
                    default:
                        match = false;
                }
                break;
            case NE:
                switch (other.getNumberCompare()){
                    case EQ:
                        match = this.getValue() != other.getValue();
                        break;
                    case NE:
                        match = this.getValue() == other.getValue();
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
                switch (other.getNumberCompare()){
                    case EQ:
                        match = this.getValue() <= other.getValue();
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
                        match = this.getValue() <= other.getValue();
                        break;
                    case LT:
                        match = this.getValue() < other.getValue();
                        break;
                    default:
                        match = false;
                }
                break;
            case GT:
                switch (other.getNumberCompare()){
                    case EQ:
                        match = this.getValue() <= other.getValue();
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
                        match = this.getValue() < other.getValue();
                        break;
                    case LT:
                        match = this.getValue() < other.getValue();
                        break;
                    default:
                        match = false;
                }
                break;
            case LE:
                switch (other.getNumberCompare()){
                    case EQ:
                        match = this.getValue() >= other.getValue();
                        break;
                    case NE:
                        match = true;
                        break;
                    case GE:
                        match = this.getValue() >= other.getValue();
                        break;
                    case GT:
                        match = this.getValue() > other.getValue();
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
                switch (other.getNumberCompare()){
                    case EQ:
                        match = this.getValue() > other.getValue();
                        break;
                    case NE:
                        match = true;
                        break;
                    case GE:
                        match = this.getValue() > other.getValue();
                        break;
                    case GT:
                        match = this.getValue() > other.getValue();
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
}
