package io.owslab.mailreceiver.model;
import org.ocpsoft.prettytime.PrettyTime;
/**
 * Created by khanhlvb on 1/26/18.
 */
public class RelativeSentAtEmail {
    private Email original;
    private String relativeDate;
    private PrettyTime p = new PrettyTime();

    public RelativeSentAtEmail(Email original) {
        this.original = original;
        this.relativeDate = p.format(original.getSentAt());
    }

    public Email getOriginal() {
        return original;
    }

    public void setOriginal(Email original) {
        this.original = original;
    }

    public String getRelativeDate() {
        return relativeDate;
    }

    public void setRelativeDate(String relativeDate) {
        this.relativeDate = relativeDate;
    }
}
