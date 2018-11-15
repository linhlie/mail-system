package io.owslab.mailreceiver.service.settings;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.ReceiveRuleDAO;
import io.owslab.mailreceiver.dto.ReceiveRuleDTO;
import io.owslab.mailreceiver.form.MarkReflectionScopeBundleForm;
import io.owslab.mailreceiver.form.ReceiveRuleBundleForm;
import io.owslab.mailreceiver.form.ReceiveRuleForm;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.ReceiveRule;
import io.owslab.mailreceiver.response.JsonStringResponseBody;
import io.owslab.mailreceiver.service.expansion.DomainService;
import io.owslab.mailreceiver.service.expansion.PeopleInChargePartnerUnregisterService;
import io.owslab.mailreceiver.service.matching.MatchingConditionService;
import io.owslab.mailreceiver.utils.FilterRule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by khanhlvb on 7/17/18.
 */
@Service
public class MailReceiveRuleService {
    public static class ReceiveType {
        public static final String ALL = "1";
        public static final String FILTER = "2";
    }

    public static class MarkReflectionScopeType {
        public static final String FROM_NEXT = "1";
        public static final String INCLUDE_PAST = "2";
    }

    public static class SaveTrashBoxType {
        public static final String NONE = "0";
        public static final String ALL = "1";
    }

    @Autowired
    private EnviromentSettingService ess;

    @Autowired
    private DomainService domainService;
    
    @Autowired
    private EnviromentSettingService enviromentSettingService;
    
    @Autowired
    private MatchingConditionService mcs;

    @Autowired
    private ReceiveRuleDAO receiveRuleDAO;

    @Autowired ProcessService processService;

    @Autowired
    private EmailDAO emailDAO;

    @Autowired
    private PeopleInChargePartnerUnregisterService peopleInChargeUnregisterService;

    public JSONObject getReceiveRuleSettings() throws JSONException {
        JSONObject obj = new JSONObject();

        String receiveMailType = ess.getReceiveMailType();
        String receiveMailRule = ess.getReceiveMailRule();
        String saveToTrashBox = ess.getSaveToTrashBox();
        String markAConditions = ess.getMarkAConditions();
        String markBConditions = ess.getMarkBConditions();
        String markReflectionScope = ess.getMarkReflectionScope();

        obj.put("receiveMailType", receiveMailType);
        obj.put("receiveMailRule", receiveMailRule);
        obj.put("saveToTrashBox", saveToTrashBox);
        obj.put("markAConditions", markAConditions);
        obj.put("markBConditions", markBConditions);
        obj.put("markReflectionScope", markReflectionScope);
        return obj;
    }

    public void saveReceiveRuleBundle(ReceiveRuleBundleForm form) {
        ess.set(EnviromentSettingService.RECEIVE_MAIL_TYPE_KEY, form.getReceiveMailType());
        ess.set(EnviromentSettingService.RECEIVE_MAIL_RULE_KEY, form.getReceiveMailRule());
        ess.set(EnviromentSettingService.SAVE_TO_TRASH_BOX_KEY, form.getSaveToTrashBox());
    }

    public void saveMarkReflectionScopeBundle(MarkReflectionScopeBundleForm form) {
        String markType = form.getMarkReflectionScope();
        ess.set(EnviromentSettingService.MARK_REFLECTION_SCOPE_KEY, markType);
        ess.set(EnviromentSettingService.MARK_A_CONDITIONS_KEY, form.getMarkAConditions());
        ess.set(EnviromentSettingService.MARK_B_CONDITIONS_KEY, form.getMarkBConditions());
        if(markType.equals(MarkReflectionScopeType.INCLUDE_PAST)) {
            processService.markPast();
        }
    }

    public void markPast() {
        List<Email> emailList = emailDAO.findByStatus(Email.Status.DONE);
        if(emailList.size() == 0) return;
        String markAConditions = ess.getMarkAConditions();
        String markBConditions = ess.getMarkBConditions();
        FilterRule markAFilterRule = getFilterRule(markAConditions);
        FilterRule markBFilterRule = getFilterRule(markBConditions);
        List<String> markAIdList = mcs.filter(emailList, markAFilterRule);
        List<String> markBIdList = mcs.filter(emailList, markBFilterRule);
        for(Email email : emailList) {
            String mark = getMark(markAIdList, markBIdList, email);
            email.setMark(mark);
        }
        emailDAO.save(emailList);
    }

    public List<ReceiveRuleDTO> getRuleNameList() {
        List<ReceiveRule> ruleList = getRuleList();
        List<ReceiveRuleDTO> ruleNameList = new ArrayList<>();
        for(ReceiveRule rule : ruleList) {
            ruleNameList.add(new ReceiveRuleDTO(rule));
        }
        return ruleNameList;
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

    public List<ReceiveRule> getRulesByForm(ReceiveRuleForm form) {
        List<ReceiveRule> list = new ArrayList<ReceiveRule>();
        ReceiveRule existRule = findRuleByNameAndType(form.getName(), form.getType());
        if(existRule != null) list.add(existRule);
        return list;
    }

    private ReceiveRule findRuleByNameAndType(String name, int type) {
        List<ReceiveRule> rules = receiveRuleDAO.findByNameAndTypeOrderByLastUpdateDesc(name, type);
        return rules.size() > 0 ? rules.get(0) : null;
    }

    public synchronized void checkMailStatus() {
        List<Email> emailList = emailDAO.findByStatus(Email.Status.NEW);
        List<Email> listEmailToCheckDomain = new ArrayList<Email>();
        if(emailList.size() == 0) return;
        String receiveMailType = ess.getReceiveMailType();
        String receiveMailRule = ess.getReceiveMailRule();
        String markAConditions = ess.getMarkAConditions();
        String markBConditions = ess.getMarkBConditions();
        String saveTrashBoxType = ess.getSaveToTrashBox();
        int skipStatus = saveTrashBoxType.equals(SaveTrashBoxType.ALL) ? Email.Status.SKIPPED : Email.Status.DELETED;
        FilterRule receiveMailFilterRule = getFilterRule(receiveMailRule);
        FilterRule markAFilterRule = getFilterRule(markAConditions);
        FilterRule markBFilterRule = getFilterRule(markBConditions);
        List<String> markAIdList = mcs.filter(emailList, markAFilterRule);
        List<String> markBIdList = mcs.filter(emailList, markBFilterRule);
        if(receiveMailType.equals(ReceiveType.FILTER) && receiveMailFilterRule != null) {
            List<String> matchIdList = mcs.filter(emailList, receiveMailFilterRule);
            for(Email email : emailList) {
                if(matchIdList.contains(email.getMessageId())) {
                    email.setStatus(Email.Status.DONE);
                    listEmailToCheckDomain.add(email);
                    String mark = getMark(markAIdList, markBIdList, email);
                    email.setMark(mark);
                } else {
                    email.setStatus(skipStatus);
                }
            }
        } else {
            for(Email email : emailList) {
                email.setStatus(Email.Status.DONE);
                listEmailToCheckDomain.add(email);
                String mark = getMark(markAIdList, markBIdList, email);
                email.setMark(mark);
            }
        }
        emailDAO.save(emailList);
        boolean addNewDomainUnregister = enviromentSettingService.getAddNewDomainUnregister();
        if(addNewDomainUnregister){
            domainService.saveDomainUnregistered(listEmailToCheckDomain);
        }

//        boolean isAddNewPeopleInChargeUnregister = enviromentSettingService.getAddPeopleInChargePartnerUnregister();
        if(true){
            peopleInChargeUnregisterService.savePeopleInChargeUnregistered(listEmailToCheckDomain);
        }
    }

    public FilterRule getFilterRule(String ruleStr) {
        FilterRule filterRule = null;
        try {
            JSONObject raw = new JSONObject(ruleStr);
            JSONObject rule = buildGroupDataFromRaw(raw);
            ObjectMapper mapper = new ObjectMapper();
            filterRule = mapper.readValue(rule.toString(), FilterRule.class);
        } catch (JSONException e) {

        } catch (IOException e) {

        } catch (Exception e) {

        }
        return filterRule;
    }

    private JSONObject buildGroupDataFromRaw(JSONObject data) throws JSONException {
        JSONObject result = new JSONObject();
        result.put("condition", data.get("condition"));
        result.put("rules", buildRulesDataFromRaw(data));
        return result;
    }

    private JSONArray buildRulesDataFromRaw(JSONObject data) throws JSONException {
        JSONArray result = new JSONArray();
        JSONArray rules = data.getJSONArray("rules");
        for(int i = 0 ; i < rules.length(); i++){
            JSONObject rawRule = (JSONObject) rules.get(i);
            if(rawRule.has("id")){
                JSONObject rule = new JSONObject();
                rule.put("id", rawRule.get("id"));
                rule.put("operator", rawRule.get("operator"));
                rule.put("type", rawRule.get("type"));
                rule.put("value", rawRule.get("value"));
                result.put(rule);
            } else if (rawRule.has("condition")) {
                JSONObject rule = buildGroupDataFromRaw(rawRule);
                result.put(rule);
            }
        }
        return result;
    }

    private String getMark(List<String> markAIdList, List<String> markBIdList, Email email) {
        String msgId = email.getMessageId();
        if(markAIdList.contains(msgId)) {
            return Email.Mark.A;
        } else if(markBIdList.contains(msgId)) {
            return Email.Mark.B;
        } else {
            return Email.Mark.NONE;
        }
    }
}
