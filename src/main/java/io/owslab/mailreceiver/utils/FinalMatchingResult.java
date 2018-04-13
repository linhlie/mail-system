package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.dto.PreviewMailDTO;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by khanhlvb on 4/13/18.
 */
public class FinalMatchingResult {
    private List<MatchingResult> matchingResultList;
    private Map<String, PreviewMailDTO> mailList;

    public FinalMatchingResult(List<MatchingResult> matchingResultList, Map<String, PreviewMailDTO> mailList) {
        this.matchingResultList = matchingResultList;
        this.mailList = mailList;
    }

    public List<MatchingResult> getMatchingResultList() {
        return matchingResultList;
    }

    public void setMatchingResultList(List<MatchingResult> matchingResultList) {
        this.matchingResultList = matchingResultList;
    }

    public Map<String, PreviewMailDTO> getMailList() {
        return mailList;
    }

    public void setMailList(Map<String, PreviewMailDTO> mailList) {
        this.mailList = mailList;
    }
}
