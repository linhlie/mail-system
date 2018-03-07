package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.enums.CombineOption;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.MatchingCondition;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 3/7/18.
 */
public class MatchingConditionResult {
    private MatchingCondition matchingCondition;
    private List<Email> emailList;

    public MatchingConditionResult(MatchingCondition matchingCondition, List<Email> emailList) {
        this.matchingCondition = matchingCondition;
        this.emailList = emailList;
    }

    public MatchingConditionResult(MatchingCondition matchingCondition) {
        this.matchingCondition = matchingCondition;
        this.emailList = new ArrayList<Email>();
    }

    public MatchingCondition getMatchingCondition() {
        return matchingCondition;
    }

    public void setMatchingCondition(MatchingCondition matchingCondition) {
        this.matchingCondition = matchingCondition;
    }

    public List<Email> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<Email> emailList) {
        this.emailList = emailList;
    }

    public boolean add(Email email){
        return this.emailList.add(email);
    }

    public CombineOption getCombineOption(){
        return matchingCondition.getCombineOption();
    }

}
