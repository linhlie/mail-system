package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.EmailAccountSetting;

/**
 * Created by khanhlvb on 2/28/18.
 */
public class SendAccountForm extends AccountForm {

    public SendAccountForm() {
        super();
        this.setType(EmailAccountSetting.Type.SEND);
    }

    public SendAccountForm(EmailAccountSetting account) {
        super(account);
    }
}
