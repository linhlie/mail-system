package io.owslab.mailreceiver.form;

/**
 * Created by khanhlvb on 6/29/18.
 */
public class SentMailHistoryForm {
    public class FilterType {
        public static final String TODAY = "本日";
        public static final String ALL = "全て";
    }
    private String filterType;
    private String fromDateStr;
    private String toDateStr;

    public SentMailHistoryForm() {
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFromDateStr() {
        return fromDateStr;
    }

    public void setFromDateStr(String fromDateStr) {
        this.fromDateStr = fromDateStr;
    }

    public String getToDateStr() {
        return toDateStr;
    }

    public void setToDateStr(String toDateStr) {
        this.toDateStr = toDateStr;
    }

    @Override
    public String toString() {
        return "SentMailHistoryForm" + " | " + this.getFilterType() + " | " + this.getFromDateStr() + " | " + this.getToDateStr();
    }
}
