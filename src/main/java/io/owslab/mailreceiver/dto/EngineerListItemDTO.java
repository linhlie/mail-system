package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Engineer;

import java.sql.Timestamp;

/**
 * Created by khanhlvb on 8/17/18.
 */
public class EngineerListItemDTO {
    private long id;
    private String name;
    private String partnerName;
    private boolean active;
    private boolean autoExtend;
    private boolean dormant;
    private String skillSheet;

    public EngineerListItemDTO(Engineer engineer, String partnerName, Timestamp now) {
        this.setId(engineer.getId());
        this.setName(engineer.getName());
        this.setAutoExtend(engineer.isAutoExtend());
        this.setDormant(engineer.isDormant());
        this.setSkillSheet(engineer.getSkillSheet());
        this.setPartnerName(partnerName);
        boolean dormant = engineer.isDormant();
        Timestamp from = new Timestamp(engineer.getProjectPeriodStart());
        Timestamp to = new Timestamp(engineer.getProjectPeriodEnd());
        boolean active = !dormant && (!now.before(from) && !now.after(to));
        this.setActive(active);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAutoExtend() {
        return autoExtend;
    }

    public void setAutoExtend(boolean autoExtend) {
        this.autoExtend = autoExtend;
    }

    public boolean isDormant() {
        return dormant;
    }

    public void setDormant(boolean dormant) {
        this.dormant = dormant;
    }

	public String getSkillSheet() {
		return skillSheet;
	}

	public void setSkillSheet(String skillSheet) {
		this.skillSheet = skillSheet;
	}
}
