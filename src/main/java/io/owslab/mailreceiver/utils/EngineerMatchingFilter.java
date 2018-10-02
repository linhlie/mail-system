package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.model.Engineer;

import java.sql.Timestamp;

public class EngineerMatchingFilter extends EngineerFilter{
	    private String mailAddress;
	    private Long partnerId;
	    private String matchingWord;
	    private String notGoodWord;
	    private String monetaryMoney;	    
	    FilterRule moneyCondition;
	    
	    public EngineerMatchingFilter(){}
	    
	    public EngineerMatchingFilter(Engineer engineer, String partnerName, Timestamp now) {
	        this.setId(engineer.getId());
	        this.setName(engineer.getName());
	        this.setAutoExtend(engineer.isAutoExtend());
	        this.setDormant(engineer.isDormant());
	        this.setPartnerName(partnerName);
	        boolean dormant = engineer.isDormant();
	        boolean active = !dormant && checkActive(engineer.getProjectPeriodStart(), engineer.getProjectPeriodEnd(), now);
	        this.setActive(active);
	        this.setPartnerId(engineer.getPartnerId());
	        this.setMonetaryMoney(engineer.getMonetaryMoney());
	        this.setMailAddress(engineer.getMailAddress());
	        this.setMatchingWord(engineer.getMatchingWord());
	        this.setNotGoodWord(engineer.getNotGoodWord());
	        this.setProjectPeriodStart(engineer.getProjectPeriodStart());
	        this.setProjectPeriodEnd(engineer.getProjectPeriodEnd());
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

}
