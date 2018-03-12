package io.owslab.mailreceiver.utils;

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

    public boolean match(FullNumberRange other){
        return (this.getLeft().match(other.getLeft())
                && this.getLeft().match(other.getRight()))
                && (this.getRight().match(other.getLeft())
                && this.getRight().match(other.getRight()));
    }

    public String toString(){
        return this.getLeft().toString() + " ~ " + this.getRight().toString();
    }

    public void multiple(Double multipleWidth){
        this.left.multiple(multipleWidth);
        this.right.multiple(multipleWidth);
    }
}
