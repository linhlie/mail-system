package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.enums.NumberCompare;

/**
 * Created by khanhlvb on 3/8/18.
 */
public class SimpleNumberRange {
    private NumberCompare numberCompare;
    private double value;

    public SimpleNumberRange(NumberCompare numberCompare, double value) {
        this.numberCompare = numberCompare;
        this.value = value;
    }

    public SimpleNumberRange(double value) {
        this.value = value;
        this.numberCompare = NumberCompare.EQ;
    }

    public SimpleNumberRange(double value, int replace) {
        this.value = value;
        this.numberCompare = NumberCompare.fromReplace(replace);
    }

    public SimpleNumberRange(){
        this.value = 0;
        this.numberCompare = NumberCompare.AUTOMATCH;
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

    public boolean match(SimpleNumberRange other){
        if(this.getNumberCompare().equals(NumberCompare.AUTOMATCH)){
            return true;
        }
        if(other.getNumberCompare().equals(NumberCompare.AUTOMATCH)){
            return true;
        }
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

    public String toString(){
        return this.getNumberCompare().toString() + " " + this.getValue();
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
