package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.model.Engineer;

import java.sql.Timestamp;

public class EngineerMatchingFilter {
	   private long id;
	    private String name;
	    private String mailAddress;
	    private Long partnerId;
	    private String partnerName;
	    private String matchingWord;
	    private String notGoodWord;
	    private String monetaryMoney;
	    private boolean active;
	    private boolean autoExtend;
	    private boolean dormant;
	    private long projectPeriodStart;
	    private long projectPeriodEnd;
	    
	    FilterRule moneyCondition;
	    public EngineerMatchingFilter(){
	    	
	    }
	    
	    public EngineerMatchingFilter(Engineer engineer){
	    	this.id = engineer.getId();
	    	this.name =engineer.getName();
	    	this.partnerName = "Partner Name";
	    }
	    
	    public EngineerMatchingFilter(Engineer engineer, String partnerName, Timestamp now) {
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
	        this.setPartnerId(engineer.getPartnerId());
	        this.setMonetaryMoney(engineer.getMonetaryMoney());
	        this.setMailAddress(engineer.getMailAddress());
	        this.setMatchingWord(engineer.getMatchingWord());
	        this.setNotGoodWord(engineer.getNotGoodWord());
	        this.setProjectPeriodStart(engineer.getProjectPeriodStart());
	        this.setProjectPeriodEnd(engineer.getProjectPeriodEnd());
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

		public String getMailAddress() {
			return mailAddress;
		}

		public void setMailAddress(String mailAddress) {
			this.mailAddress = mailAddress;
		}

		public long getPartnerId() {
			return partnerId;
		}

		public void setPartnerId(long partnerId) {
			this.partnerId = partnerId;
		}

		public String getMatchingWord() {
			return matchingWord;
		}

		public void setMatchingWord(String matchingWord) {
			this.matchingWord = matchingWord;
		}

		public String getNotGoodWord() {
			return notGoodWord;
		}

		public void setNotGoodWord(String notGoodWord) {
			this.notGoodWord = notGoodWord;
		}

		public String getMonetaryMoney() {
			return monetaryMoney;
		}

		public void setMonetaryMoney(String monetaryMoney) {
			this.monetaryMoney = monetaryMoney;
		}

		public FilterRule getMoneyCondition() {
			return moneyCondition;
		}

		public void setMoneyCondition(FilterRule moneyCondition) {
			this.moneyCondition = moneyCondition;
		}

		public void setPartnerId(Long partnerId) {
			this.partnerId = partnerId;
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

}
