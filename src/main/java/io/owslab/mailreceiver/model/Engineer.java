package io.owslab.mailreceiver.model;

import io.owslab.mailreceiver.utils.Utils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by khanhlvb on 8/17/18.
 */
@Entity
@Table(name = "Engineers")
public class Engineer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String lastName;

    @NotNull
    private String firstName;

    @NotNull
    private String kanaLastName;

    @NotNull
    private String kanaFirstName;

    private String mailAddress;

    private int employmentStatus;

    private long partnerId;

    private long projectPeriodStart;

    private long projectPeriodEnd;

    private boolean autoExtend;

    private int extendMonth;

    private String matchingWord;

    private String notGoodWord;

    private String monetaryMoney;

    private String stationLine;

    private String stationNearest;

    private Double commutingTime;

    private boolean dormant;
    
    private String skillSheet;

    private String initial;

    private String introduction;

    public Engineer() {
    }

    public Engineer(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getLastName() { return lastName; }

    public String getFirstName() { return firstName; }

    public String getKanaLastName() { return kanaLastName; }

    public String getKanaFirstName() { return kanaFirstName; }

    public String getMailAddress() {
        return mailAddress;
    }

    public int getEmploymentStatus() {
        return employmentStatus;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public long getProjectPeriodStart() {
        return projectPeriodStart;
    }

    public long getProjectPeriodEnd() {
        return projectPeriodEnd;
    }

    public boolean isAutoExtend() { return autoExtend; }

    public int getExtendMonth() {
        return extendMonth;
    }

    public String getMatchingWord() {
        return matchingWord;
    }

    public String getNotGoodWord() {
        return notGoodWord;
    }

    public String getMonetaryMoney() {
        return monetaryMoney;
    }

    public String getStationLine() {
        return stationLine;
    }

    public String getStationNearest() {
        return stationNearest;
    }

    public Double getCommutingTime() {
        return commutingTime;
    }

    public boolean isDormant() {
        return dormant;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setKanaLastName(String kanaLastName) { this.kanaLastName = kanaLastName; }

    public void setKanaFirstName(String kanaFirstName) { this.kanaFirstName = kanaFirstName; }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public void setEmploymentStatus(int employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }

    public void setProjectPeriodStart(long projectPeriodStart) {
        this.projectPeriodStart = projectPeriodStart;
    }

    public void setProjectPeriodEnd(long projectPeriodEnd) {
        this.projectPeriodEnd = projectPeriodEnd;
    }

    public void setAutoExtend(boolean autoExtend) {
        this.autoExtend = autoExtend;
    }

    public void setExtendMonth(int extendMonth) {
        this.extendMonth = extendMonth;
    }

    public void setMatchingWord(String matchingWord) {
        this.matchingWord = matchingWord;
    }

    public void setNotGoodWord(String notGoodWord) {
        this.notGoodWord = notGoodWord;
    }

    public void setMonetaryMoney(String monetaryMoney) {
        this.monetaryMoney = monetaryMoney;
    }

    public void setStationLine(String stationLine) {
        this.stationLine = stationLine;
    }

    public void setStationNearest(String stationNearest) {
        this.stationNearest = stationNearest;
    }

    public void setCommutingTime(Double commutingTime) {
        this.commutingTime = commutingTime;
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

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public static class EmploymentStatus {
        public static final int REGULAR = 1;
        public static final int CONTRACT = 2;
        public static final int SOLE_PROPROETORSHIP = 3;
        public static final int TEMPORARY_OTHER = 4;
        public static final int BP_REGULAR = 5;
        public static final int BP_CONTRACT = 6;
        public static final int BP_SOLE_PROPROETORSHIP = 7;
        public static final int BP_UNKNOWN = 8;
    }
    
    public static class Builder{
        private String lastName;

        private String firstName;

        private String kanaLastName;

        private String kanaFirstName;

        private String mailAddress;

        private int employmentStatus;

        private long partnerId;

        private String projectPeriodStart;

        private String projectPeriodEnd;

        private boolean autoExtend;

        private int extendMonth;

        private String matchingWord;

        private String notGoodWord;

        private String monetaryMoney;

        private String stationLine;

        private String stationNearest;

        private Double commutingTime;

        private boolean dormant;
        
        private String skillSheet;

        private String initial;

        private String introduction;

        public Builder(Engineer engineer) {
            this.lastName = engineer.getLastName();
            this.firstName = engineer.getFirstName();
            this.kanaLastName = engineer.getKanaLastName();
            this.kanaFirstName = engineer.getKanaFirstName();
            this.mailAddress = engineer.getMailAddress();
            this.employmentStatus = engineer.getEmploymentStatus();
            this.partnerId = engineer.getPartnerId();
            this.autoExtend = engineer.isAutoExtend();
            this.extendMonth = engineer.getExtendMonth();
            this.matchingWord = engineer.getMatchingWord();
            this.notGoodWord = engineer.getNotGoodWord();
            this.monetaryMoney = engineer.getMonetaryMoney();
            this.stationLine = engineer.getStationLine();
            this.stationNearest = engineer.getStationNearest();
            this.commutingTime = engineer.getCommutingTime();
            this.dormant = engineer.isDormant();
            this.skillSheet = engineer.getSkillSheet();
            if(engineer.getProjectPeriodStart()!=0){
                this.projectPeriodStart = Utils.formatTimestamp(Utils.DATE_FORMAT, engineer.getProjectPeriodStart());
            }
            if(engineer.getProjectPeriodEnd()!=0){
            	this.projectPeriodEnd = Utils.formatTimestamp(Utils.DATE_FORMAT, engineer.getProjectPeriodEnd());
            }
            this.initial = engineer.getInitial();
            this.introduction = engineer.getIntroduction();
        }

        public Builder() {
        }

        public Engineer build() throws ParseException {
            Engineer engineer = new Engineer();
            engineer.setLastName(this.getLastName());
            engineer.setFirstName(this.getFirstName());
            engineer.setKanaLastName(this.getKanaLastName());
            engineer.setKanaFirstName(this.getKanaFirstName());
            engineer.setMailAddress(this.getMailAddress());
            engineer.setEmploymentStatus(this.getEmploymentStatus());
            if(this.getProjectPeriodStart() != null && !this.getProjectPeriodStart().equals("")){
                Date from = Utils.parseDateStr(this.getProjectPeriodStart());
                engineer.setProjectPeriodStart(Utils.atStartOfDay(from).getTime());
            }
            if(this.getProjectPeriodEnd()!= null && !this.getProjectPeriodEnd().equals("")){
                Date to = Utils.parseDateStr(this.getProjectPeriodEnd());
                engineer.setProjectPeriodEnd(Utils.atEndOfDay(to).getTime());
            }
            engineer.setPartnerId(this.getPartnerId());
            engineer.setAutoExtend(this.isAutoExtend());
            engineer.setExtendMonth(this.getExtendMonth());
            engineer.setMatchingWord(this.getMatchingWord());
            engineer.setNotGoodWord(this.getNotGoodWord());
            engineer.setMonetaryMoney(this.getMonetaryMoney());
            engineer.setStationLine(this.getStationLine());
            engineer.setStationNearest(this.getStationNearest());
            engineer.setCommutingTime(this.getCommutingTime());
            engineer.setDormant(this.isDormant());
            engineer.setSkillSheet(this.getSkillSheet());
            engineer.setInitial(this.getInitial());
            engineer.setIntroduction(this.getIntroduction());
            return engineer;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getKanaLastName() {
            return kanaLastName;
        }

        public void setKanaLastName(String kanaLastName) {
            this.kanaLastName = kanaLastName;
        }

        public String getKanaFirstName() {
            return kanaFirstName;
        }

        public void setKanaFirstName(String kanaFirstName) {
            this.kanaFirstName = kanaFirstName;
        }

        public String getMailAddress() {
            return mailAddress;
        }

        public void setMailAddress(String mailAddress) {
            this.mailAddress = mailAddress;
        }

        public int getEmploymentStatus() {
            return employmentStatus;
        }

        public void setEmploymentStatus(int employmentStatus) {
            this.employmentStatus = employmentStatus;
        }

        public long getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(long partnerId) {
            this.partnerId = partnerId;
        }

        public String getProjectPeriodStart() {
            return projectPeriodStart;
        }

        public void setProjectPeriodStart(String projectPeriodStart) {
            this.projectPeriodStart = projectPeriodStart;
        }

        public String getProjectPeriodEnd() {
            return projectPeriodEnd;
        }

        public void setProjectPeriodEnd(String projectPeriodEnd) {
            this.projectPeriodEnd = projectPeriodEnd;
        }

        public boolean isAutoExtend() {
            return autoExtend;
        }

        public void setAutoExtend(boolean autoExtend) {
            this.autoExtend = autoExtend;
        }

        public int getExtendMonth() {
            return extendMonth;
        }

        public void setExtendMonth(int extendMonth) {
            this.extendMonth = extendMonth;
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

        public String getStationLine() {
            return stationLine;
        }

        public void setStationLine(String stationLine) {
            this.stationLine = stationLine;
        }

        public String getStationNearest() {
            return stationNearest;
        }

        public void setStationNearest(String stationNearest) {
            this.stationNearest = stationNearest;
        }

        public Double getCommutingTime() {
            return commutingTime;
        }

        public void setCommutingTime(Double commutingTime) {
            this.commutingTime = commutingTime;
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

        public String getInitial() {
            return initial;
        }

        public void setInitial(String initial) {
            this.initial = initial;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }
    }
}
