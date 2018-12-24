package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.utils.FilterRule;

public class InboxForm {
    private FilterRule filterRule;
    private int page;

    public FilterRule getFilterRule() {
        return filterRule;
    }

    public void setFilterRule(FilterRule filterRule) {
        this.filterRule = filterRule;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
