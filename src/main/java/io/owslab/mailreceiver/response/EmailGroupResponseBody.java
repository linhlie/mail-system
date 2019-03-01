package io.owslab.mailreceiver.response;

import io.owslab.mailreceiver.model.PeopleInChargePartner;

import java.util.List;

public class EmailGroupResponseBody extends AjaxResponseBody  {
    private List<PeopleInChargePartner> listPeople;

    public EmailGroupResponseBody(String msg, boolean status) {
        super(msg, status);
    }

    public EmailGroupResponseBody(String msg) {
        this(msg, false);
    }

    public EmailGroupResponseBody() {
        this("");
    }

    public List<PeopleInChargePartner> getListPeople() {
        return listPeople;
    }

    public void setListPeople(List<PeopleInChargePartner> listPeople) {
        this.listPeople = listPeople;
    }
}
