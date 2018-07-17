package io.owslab.mailreceiver.service.settings;

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
}
