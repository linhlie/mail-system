package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.PeopleInChargePartnerUnregister;

import java.util.List;

public class EmailsAvoidRegisterPeopleInChargeForm {
    List<PeopleInChargePartnerUnregister> emailsUpdate;
    List<PeopleInChargePartnerUnregister> emailsDelete;

    public List<PeopleInChargePartnerUnregister> getEmailsUpdate() {
        return emailsUpdate;
    }

    public void setEmailsUpdate(List<PeopleInChargePartnerUnregister> emailsUpdate) {
        this.emailsUpdate = emailsUpdate;
    }

    public List<PeopleInChargePartnerUnregister> getEmailsDelete() {
        return emailsDelete;
    }

    public void setEmailsDelete(List<PeopleInChargePartnerUnregister> emailsDelete) {
        this.emailsDelete = emailsDelete;
    }
}
