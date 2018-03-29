package io.owslab.mailreceiver.utils;

/**
 * Created by khanhlvb on 3/29/18.
 */
public class MatchingPartResult {
    private boolean match;
    private FullNumberRange matchRange;

    public MatchingPartResult() {
        this(false);
    }

    public MatchingPartResult(boolean match) {
        this(match, null);
    }

    public MatchingPartResult(FullNumberRange matchRange) {
        this(true, matchRange);
    }

    public MatchingPartResult(boolean match, FullNumberRange matchRange) {
        this.match = match;
        this.matchRange = matchRange;
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
}
