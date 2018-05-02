package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.enums.CombineOption;
import io.owslab.mailreceiver.enums.ConditionOption;
import io.owslab.mailreceiver.enums.MailItemOption;
import io.owslab.mailreceiver.model.Email;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 5/2/18.
 */
public class FilterRule {
    private String condition;
    private List<FilterRule> rules;
    private String id;
    private String operator;
    private String type;
    private String value;
    private List<Email> emails;
    private MailItemOption mailItemOption;
    private ConditionOption conditionOption;

    public FilterRule(String condition, List<FilterRule> rules) {
        this.condition = condition;
        this.rules = rules;
        this.emails = new ArrayList<>();
    }

    public FilterRule(String id, String operator, String type, String value) {
        this.id = id;
        this.operator = operator;
        this.type = type;
        this.value = value;
        this.rules = new ArrayList<>();
        this.emails = new ArrayList<>();
    }

    public FilterRule(String id, String operator, String value) {
        this(id, operator, "string", value);
    }

    public FilterRule() {
        this.rules = new ArrayList<>();
        this.emails = new ArrayList<>();
    }

    public FilterRule(FilterRule other) {
        this.rules = new ArrayList<>();
        this.emails = new ArrayList<>();
        if(other.isGroup()){
            this.setCondition(other.getCondition());
            for(FilterRule rule : other.getRules()) {
                this.addRule(new FilterRule(rule));
            }
        } else {
            this.setId(other.getId());
            this.setOperator(other.getOperator());
            this.setType(other.getType());
            this.setValue(other.getValue());
        }
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<FilterRule> getRules() {
        return rules;
    }

    public void setRules(List<FilterRule> rules) {
        this.rules = rules;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public MailItemOption getMailItemOption() {
        if(mailItemOption == null)
            mailItemOption = MailItemOption.fromValue(Integer.parseInt(this.getId()));
        return mailItemOption;
    }

    public void setMailItemOption(MailItemOption mailItemOption) {
        this.mailItemOption = mailItemOption;
    }

    public ConditionOption getConditionOption() {
        if(conditionOption == null)
            conditionOption = ConditionOption.fromOperator(this.getOperator());
        return conditionOption;
    }

    public void setConditionOption(ConditionOption conditionOption) {
        this.conditionOption = conditionOption;
    }

    public boolean isGroup(){
        return this.getCondition() != null;
    }

    public boolean add(Email email){
        return this.emails.add(email);
    }

    public FilterRule addRule(FilterRule rule) {
        if(this.isGroup()){
            this.rules.add(rule);
        }
        return this;
    }

    public String toString() {
        String result = "{";
        if(this.isGroup()){
            result = result + "condition: " + this.getCondition() + ",\n" + "rules: [";
            for(FilterRule rule : this.getRules()) {
                result = result + "\n" + rule.toString();
            }
            result = result + "\n]";
        } else {
            result = result + "id: " + this.getId() + ",\n";
            result = result + "operator: " + this.getOperator() + ",\n";
            result = result + "type: " + this.getType() + ",\n";
            result = result + "value: " + this.getValue();
        }
        result = result + "\n},";
        return result;
    }

    public List<Email> getMatchEmails(){
        if(this.isGroup()){
            return mergeMatchMails(this.getRules());
        } else {
            return this.getEmails();
        }
    }

    private List<Email> mergeMatchMails(List<FilterRule> rules){
        List<Email> result = new ArrayList<>();
        CombineOption option = this.getCombineOption();
        for(FilterRule rule : rules){
            List<Email> emailList;
            if(rules.indexOf(rule) == 0){
                emailList = rule.getMatchEmails();
                result = mergeWithoutDuplicate(result, emailList);
            } else {
                switch (option){
                    case NONE:
                        break;
                    case AND:
                        emailList = rule.getMatchEmails();
                        result = findDuplicateList(result, emailList);
                        break;
                    case OR:
                        emailList = rule.getMatchEmails();
                        result = mergeWithoutDuplicate(result, emailList);
                        break;
                }
            }
        }
        return result;
    }

    private List<Email> mergeWithoutDuplicate(List<Email> list1, List<Email> list2){
        List<Email> list1Copy = new ArrayList<>(list1);
        List<Email> list2Copy = new ArrayList<>(list2);
        list2Copy.removeAll(list1Copy);
        list1Copy.addAll(list2Copy);
        return list1Copy;
    }

    private List<Email> findDuplicateList(List<Email> list1, List<Email> list2){
        List<Email> list = list1.size() >= list2.size() ? list2 : list1;
        List<Email> remainList = list1.size() >= list2.size() ? list1 : list2;
        List<Email> result = new ArrayList<>();
        for(Email email: list){
            if(remainList.contains(email)){
                result.add(email);
            }
        }
        return result;
    }

    public CombineOption getCombineOption(){
        if(this.isGroup()){
            switch (this.getCondition()) {
                case "AND":
                    return CombineOption.AND;
                case "OR":
                    return CombineOption.OR;
            }
        }
        return CombineOption.NONE;
    }
}
