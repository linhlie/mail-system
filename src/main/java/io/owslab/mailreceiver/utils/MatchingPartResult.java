package io.owslab.mailreceiver.utils;

/**
 * Created by khanhlvb on 3/29/18.
 */
public class MatchingPartResult {
    private boolean match;
    private FullNumberRange matchRange;
    private FullNumberRange range;

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
}
