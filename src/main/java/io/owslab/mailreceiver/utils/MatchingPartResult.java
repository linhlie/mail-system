package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.model.Email;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 3/29/18.
 */
public class MatchingPartResult {
    private boolean match;
    private Email sourceMail;
    private Email destinationMail;
    private FullNumberRange matchRange;
    private FullNumberRange range;
    private List<String> intersectWords;

    public MatchingPartResult() {
        this(false);
    }

    public MatchingPartResult(boolean match) {
        this(match, null, null);
    }

    public MatchingPartResult(FullNumberRange matchRange, FullNumberRange range) {
        this(true, matchRange, range);
    }

    public MatchingPartResult(boolean match, FullNumberRange matchRange) {
        this(match, matchRange, null);
    }

    public MatchingPartResult(boolean match, FullNumberRange matchRange, FullNumberRange range) {
        this.match = match;
        this.matchRange = matchRange;
        this.range = range;
        this.intersectWords = new ArrayList<>();
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public FullNumberRange getMatchRange() {
        return matchRange;
    }

    public void setMatchRange(FullNumberRange matchRange) {
        this.matchRange = matchRange;
    }

    public FullNumberRange getRange() {
        return range;
    }

    public void setRange(FullNumberRange range) {
        this.range = range;
    }

    public List<String> getIntersectWords() {
        return intersectWords;
    }

    public void setIntersectWords(List<String> intersectWords) {
        this.intersectWords = intersectWords;
    }

    public Email getSourceMail() {
        return sourceMail;
    }

    public void setSourceMail(Email sourceMail) {
        this.sourceMail = sourceMail;
    }

    public Email getDestinationMail() {
        return destinationMail;
    }

    public void setDestinationMail(Email destinationMail) {
        this.destinationMail = destinationMail;
    }
}
