package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.EmailAccountSetting;

/**
 * Created by khanhlvb on 1/24/18.
 */
public class ReceiveAccountForm extends AccountForm {

    public ReceiveAccountForm() {
        super();
        this.setType(EmailAccountSetting.Type.RECEIVE);
    }

    public ReceiveAccountForm(EmailAccountSetting account) {
        super(account);
    }
}
