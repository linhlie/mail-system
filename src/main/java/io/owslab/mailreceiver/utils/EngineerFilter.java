package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.form.EngineerFilterForm;
import io.owslab.mailreceiver.model.Engineer;

import java.sql.Timestamp;
import java.util.Date;

public class EngineerFilter {
	   private long id;
	    private String name;
	    private String partnerName;
	    private boolean active;
	    private boolean autoExtend;
	    private boolean dormant;
	    private long projectPeriodStart;
	    private long projectPeriodEnd;

	    public EngineerFilter(){}
	    
	    public EngineerFilter(Engineer engineer, String partnerName, Timestamp now) {
	        this.setId(engineer.getId());
	        this.setName(engineer.getName());
	        this.setAutoExtend(engineer.isAutoExtend());
	        this.setDormant(engineer.isDormant());
	        this.setPartnerName(partnerName);
	        boolean dormant = engineer.isDormant();
	        Timestamp from = new Timestamp(engineer.getProjectPeriodStart());
	        Timestamp to = new Timestamp(engineer.getProjectPeriodEnd());
	        boolean active = !dormant && (!now.before(from) && !now.after(to));
	        this.setActive(active);
	        this.projectPeriodStart = engineer.getProjectPeriodStart();
	        this.projectPeriodEnd = engineer.getProjectPeriodEnd();
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

		public long getProjectPeriodStart() {
			return projectPeriodStart;
		}

		public void setProjectPeriodStart(long projectPeriodStart) {
			this.projectPeriodStart = projectPeriodStart;
		}

		public long getProjectPeriodEnd() {
			return projectPeriodEnd;
		}

		public void setProjectPeriodEnd(long projectPeriodEnd) {
			this.projectPeriodEnd = projectPeriodEnd;
		}
		
		public boolean FilterEngineerToRemove(Long filterDate, boolean filterTime, boolean filterTimeNull){
	    	long dateStart = 0, dateEnd = 0;
	        if(filterTime){
	            Date startDate = new Date(filterDate);
	            startDate = Utils.atStartOfDay(startDate);
	            Date endDate = Utils.addMonthsToDate(startDate, 1);
	            endDate = Utils.addDayToDate(endDate, -1);
	            endDate = Utils.atEndOfDay(endDate);
	            dateStart = startDate.getTime();
	            dateEnd = endDate.getTime();
	        }	
			if(filterTime && filterTimeNull){
        		if(!checkFilterByTime(this.getProjectPeriodStart(), this.getProjectPeriodEnd(), dateStart, dateEnd) && !checkFilterByTimeNull(this.getProjectPeriodStart(), this.getProjectPeriodEnd())){
        			return true;
        		}
        	}else if(filterTime){
        		if(!checkFilterByTime(this.getProjectPeriodStart(), this.getProjectPeriodEnd(), dateStart, dateEnd)){
        			return true;
        		}
        	}else if(filterTimeNull){
        		if(!checkFilterByTimeNull(this.getProjectPeriodStart(), this.getProjectPeriodEnd())){
        			return true;
        		}
        	}
			return false;
		}
		
	    public boolean checkFilterByTime(long projectPeriodStart, long projectPeriodEnd, long startDate, long endDate){
	    	if(projectPeriodEnd==0 && projectPeriodStart!=0){
	    		if(projectPeriodStart<endDate){
		    		return true;
	    		}else{
	    			return false;
	    		}
	    	}
	    	if(projectPeriodEnd >= startDate && projectPeriodEnd <= endDate){
	    		return true;
	    	}
	    	return false;
	    }
	    
	    public boolean checkFilterByTimeNull(long projectPeriodEnd, long projectPeriodStart){
	    	if(projectPeriodStart == 0 && projectPeriodEnd == 0){
	    		return true;
	    	}
	    	return false;
	    }
}
