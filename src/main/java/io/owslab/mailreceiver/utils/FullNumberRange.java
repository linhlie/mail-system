package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.enums.NumberCompare;

/**
 * Created by khanhlvb on 3/12/18.
 */
public class FullNumberRange {
    private SimpleNumberRange left;
    private SimpleNumberRange right;

    public FullNumberRange(SimpleNumberRange left, SimpleNumberRange right) {
        this.left = left;
        this.right = right;
    }

    public FullNumberRange(SimpleNumberRange range) {
        this.left = range;
        this.right = new SimpleNumberRange();
    }

    public FullNumberRange(SimpleNumberRange range, boolean isLeft) {
        if(isLeft){
            this.left = range;
            this.right = new SimpleNumberRange();
        } else {
            this.right = range;
            this.left = new SimpleNumberRange();
        }
    }

    public FullNumberRange() {
        this.left = new SimpleNumberRange();
        this.right = new SimpleNumberRange();
    }

    public SimpleNumberRange getLeft() {
        return left;
    }

    public void setLeft(SimpleNumberRange left) {
        this.left = left;
    }

    public SimpleNumberRange getRight() {
        return right;
    }

    public void setRight(SimpleNumberRange right) {
        this.right = right;
    }

    public boolean match(FullNumberRange other, double ratio, NumberCompare replaceCompare){
        return (this.getLeft().match(other.getLeft(), ratio, replaceCompare)
                && this.getLeft().match(other.getRight(), ratio, replaceCompare))
                && (this.getRight().match(other.getLeft(), ratio, replaceCompare)
                && this.getRight().match(other.getRight(), ratio, replaceCompare));
    }

    public boolean match(FullNumberRange other, double ratio){
        return (this.getLeft().match(other.getLeft(), ratio)
                && this.getLeft().match(other.getRight(), ratio))
                && (this.getRight().match(other.getLeft(), ratio)
                && this.getRight().match(other.getRight(), ratio));
    }

    public String toString(){
        String result = "";
        String leftPart = this.getLeft().toString();
        String rightPart = this.getRight().toString();
        result = result + leftPart;
        if(result.length() > 0 && rightPart.length() > 0){
            result = result.substring(0, result.length() - 1);
        }
        result = result + rightPart;
        return result;
    }

    public void multiple(Double multipleWidth){
        this.left.multiple(multipleWidth);
        this.right.multiple(multipleWidth);
    }

    public void replace(NumberCompare numberCompare){
        this.left.replace(numberCompare);
        this.right.replace(numberCompare);
    }
}
