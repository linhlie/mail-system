package io.owslab.mailreceiver.form;

/**
 * Created by khanhlvb on 8/22/18.
 */
public class EngineerFilterForm {

    private int filterType;
    private long filterDate;
    private boolean filterTime;
    private boolean filterTimeNull;

    public EngineerFilterForm() {
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public long getFilterDate() {
        return filterDate;
    }

    public void setFilterDate(long filterDate) {
        this.filterDate = filterDate;
    }
    
    public boolean isFilterTime() {
		return filterTime;
	}

	public void setFilterTime(boolean filterTime) {
		this.filterTime = filterTime;
	}

	public boolean isFilterTimeNull() {
		return filterTimeNull;
	}

	public void setFilterTimeNull(boolean filterTimeNull) {
		this.filterTimeNull = filterTimeNull;
	}

	public static class FilterType {
        public static final int ALL = 1;
        public static final int ACTIVE = 2;
        public static final int INACTIVE = 3;
    }
}
