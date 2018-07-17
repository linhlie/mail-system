package io.owslab.mailreceiver.service.settings;

import io.owslab.mailreceiver.dao.ReceiveRuleDAO;
import io.owslab.mailreceiver.form.MarkReflectionScopeBundleForm;
import io.owslab.mailreceiver.form.ReceiveRuleBundleForm;
import io.owslab.mailreceiver.form.ReceiveRuleForm;
import io.owslab.mailreceiver.model.ReceiveRule;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by khanhlvb on 7/17/18.
 */
@Service
public class MailReceiveRuleService {

    @Autowired
    private EnviromentSettingService ess;

    @Autowired
    private ReceiveRuleDAO receiveRuleDAO;

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

    public void saveReceiveRuleBundle(ReceiveRuleBundleForm form) {
        ess.set(EnviromentSettingService.RECEIVE_MAIL_TYPE_KEY, form.getReceiveMailType());
        ess.set(EnviromentSettingService.RECEIVE_MAIL_RULE_KEY, form.getReceiveMailRule());
    }

    public void saveMarkReflectionScopeBundle(MarkReflectionScopeBundleForm form) {
        ess.set(EnviromentSettingService.MARK_REFLECTION_SCOPE_KEY, form.getMarkReflectionScope());
        ess.set(EnviromentSettingService.MARK_A_CONDITIONS_KEY, form.getMarkAConditions());
        ess.set(EnviromentSettingService.MARK_B_CONDITIONS_KEY, form.getMarkBConditions());
    }

    public List<ReceiveRule> getRuleList() {
        return (List<ReceiveRule>) receiveRuleDAO.findAll();
    }

    public void saveRule(ReceiveRuleForm form) {
        ReceiveRule rule = new ReceiveRule(form);
        ReceiveRule existRule = findRuleByNameAndType(form.getName(), form.getType());
        if(existRule != null) {
            rule.setId(existRule.getId());
        }
        rule.setLastUpdate(new Date());
        receiveRuleDAO.save(rule);
    }

    private ReceiveRule findRuleByNameAndType(String name, int type) {
        List<ReceiveRule> rules = receiveRuleDAO.findByNameAndTypeOrderByLastUpdateDesc(name, type);
        return rules.size() > 0 ? rules.get(0) : null;
    }
}
