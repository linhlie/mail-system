package io.owslab.mailreceiver.service.settings;

import io.owslab.mailreceiver.form.MarkReflectionScopeForm;
import io.owslab.mailreceiver.form.ReceiveRuleForm;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by khanhlvb on 7/17/18.
 */
@Service
public class MailReceiveRuleService {

    @Autowired
    private EnviromentSettingService ess;

    public JSONObject getReceiveRuleSettings() {
        JSONObject obj = new JSONObject();

        String receiveMailType = ess.getReceiveMailType();
        String receiveMailRule = ess.getReceiveMailRule();
        String markAConditions = ess.getMarkAConditions();
        String markBConditions = ess.getMarkBConditions();
        String markReflectionScope = ess.getMarkReflectionScope();

        obj.put("receiveMailType", receiveMailType);
        obj.put("receiveMailRule", receiveMailRule);
        obj.put("markAConditions", markAConditions);
        obj.put("markBConditions", markBConditions);
        obj.put("markReflectionScope", markReflectionScope);
        return obj;
    }

    public void saveReceiveRule(ReceiveRuleForm form) {
        ess.set(EnviromentSettingService.RECEIVE_MAIL_TYPE_KEY, form.getReceiveMailType());
        ess.set(EnviromentSettingService.RECEIVE_MAIL_RULE_KEY, form.getReceiveMailRule());
    }

    public void saveMarkReflectionScope(MarkReflectionScopeForm form) {
        ess.set(EnviromentSettingService.MARK_REFLECTION_SCOPE_KEY, form.getMarkReflectionScope());
        ess.set(EnviromentSettingService.MARK_A_CONDITIONS_KEY, form.getMarkAConditions());
        ess.set(EnviromentSettingService.MARK_B_CONDITIONS_KEY, form.getMarkBConditions());
    }
}
