package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.exception.EngineerFieldValidationException;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Engineer;
import io.owslab.mailreceiver.utils.Utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

/**
 * Created by khanhlvb on 9/5/18.
 */
public class CSVEngineerDTO {
    private String lastName;
    private String firstName;
    private String kanaLastName;
    private String kanaFirstName;
    private String initial;
    private String mailAddress;
    private String employmentStatus;
    private String partnerCode;
    private String projectPeriodStart;
    private String projectPeriodEnd;
    private String autoExtend;
    private String extendMonth;
    private String matchingWord;
    private String notGoodWord;
    private String monetaryMoney;
    private String stationLine;
    private String stationNearest;
    private String commutingTime;
    private String skillSheet;
    private String introduction;

    public CSVEngineerDTO(){
    }

    public CSVEngineerDTO(Engineer engineer, BusinessPartner partner) {
        this.setLastName(engineer.getLastName());
        this.setFirstName(engineer.getFirstName());
        this.setKanaLastName(engineer.getKanaLastName());
        this.setKanaFirstName(engineer.getKanaFirstName());
        this.setInitial(engineer.getInitial());
        this.setMailAddress(engineer.getMailAddress());
        this.setEmploymentStatus(Integer.toString(engineer.getEmploymentStatus()));
        this.setPartnerCode(partner.getPartnerCode());
        if(engineer.getProjectPeriodStart()==0){
        	this.setProjectPeriodStart("");
        }else{
        	this.setProjectPeriodStart(Utils.formatTimestamp(Utils.DATE_FORMAT_2, engineer.getProjectPeriodStart()));
        }
        if(engineer.getProjectPeriodEnd()==0){
            this.setProjectPeriodEnd("");
        }else{
            this.setProjectPeriodEnd(Utils.formatTimestamp(Utils.DATE_FORMAT_2, engineer.getProjectPeriodEnd()));
        }      
        this.setAutoExtend(Boolean.toString(engineer.isAutoExtend()).toUpperCase());
        this.setExtendMonth(Objects.toString(engineer.getExtendMonth(), null));
        this.setMatchingWord(engineer.getMatchingWord());
        this.setNotGoodWord(engineer.getNotGoodWord());
        this.setMonetaryMoney(engineer.getMonetaryMoney());
        this.setStationLine(engineer.getStationLine());
        this.setStationNearest(engineer.getStationNearest());
        this.setCommutingTime(Objects.toString(engineer.getCommutingTime(), null));
        this.setSkillSheet(engineer.getSkillSheet());
        this.setIntroduction(engineer.getIntroduction());
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

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
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

    public String getAutoExtend() {
        return autoExtend;
    }

    public void setAutoExtend(String autoExtend) {
        this.autoExtend = autoExtend;
    }

    public String getExtendMonth() {
        return extendMonth;
    }

    public void setExtendMonth(String extendMonth) {
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

    public String getCommutingTime() {
        return commutingTime;
    }

    public void setCommutingTime(String commutingTime) {
        this.commutingTime = commutingTime;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(": ");
        for (Field f : getClass().getDeclaredFields()) {
            sb.append(f.getName());
            sb.append("=");
            try {
                sb.append(f.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append(", ");
        }
        return sb.toString();
    }

    public Engineer build(BusinessPartner partner) throws EngineerFieldValidationException {
        Engineer engineer = new Engineer();
        engineer.setLastName(this.lastName);
        engineer.setFirstName(this.firstName);
        engineer.setKanaLastName(this.kanaLastName);
        engineer.setKanaFirstName(this.kanaFirstName);
        engineer.setInitial(this.initial);
        engineer.setMailAddress(this.mailAddress);
        int employmentStatus;
        try {
            employmentStatus = Integer.parseInt(this.employmentStatus);
        } catch (NumberFormatException nfe) {
            throw new EngineerFieldValidationException("雇用形態の値が一致しません");
        }
        employmentStatus = employmentStatus >= 1 && employmentStatus <= 8 ? employmentStatus : Engineer.EmploymentStatus.BP_UNKNOWN;
        engineer.setEmploymentStatus(employmentStatus);
        engineer.setPartnerId(partner.getId());
        Date from;
        Date to;
        if(this.projectPeriodStart != null && !this.projectPeriodStart.equals("") && !this.projectPeriodStart.equals("0")){
            try {
                from = Utils.parseDateStr(this.projectPeriodStart);
            } catch (ParseException pe) {
                throw new EngineerFieldValidationException("案件期間「開始」の値が一致しません");
            }
            engineer.setProjectPeriodStart(Utils.atStartOfDay(from).getTime());
        }

        if(this.projectPeriodEnd != null && !this.projectPeriodEnd.equals("") && !this.projectPeriodEnd.equals("0")){
        	try {
                to = Utils.parseDateStr(this.projectPeriodEnd);
            } catch (ParseException pe) {
                throw new EngineerFieldValidationException("案件期間「終了」の値が一致しません");
            }
            engineer.setProjectPeriodEnd(Utils.atEndOfDay(to).getTime());
        }
        boolean autoExtend = this.autoExtend.equalsIgnoreCase("TRUE");
        engineer.setAutoExtend(autoExtend);
        if(autoExtend) {
            try {
                int extendMonth = Integer.parseInt(this.extendMonth);
                engineer.setExtendMonth(extendMonth);
            } catch (NumberFormatException nfe) {
                throw new EngineerFieldValidationException("延長期間の値が一致しません");
            }
        }
        engineer.setMatchingWord(this.matchingWord);
        engineer.setNotGoodWord(this.notGoodWord);
        if(this.monetaryMoney != null) {
            boolean match = Utils.matchingNumber(this.monetaryMoney);
            if(match) {
                engineer.setMonetaryMoney(this.monetaryMoney);
            } else {
                throw new EngineerFieldValidationException("単金の値が一致しません");
            }
        }
        engineer.setStationLine(this.stationLine);
        engineer.setStationNearest(this.stationNearest);
        if(this.commutingTime != null) {
            try {
                double commutingTime = Double.parseDouble(this.commutingTime);
                engineer.setCommutingTime(commutingTime);
            } catch (NumberFormatException nfe) {
                throw new EngineerFieldValidationException("通勤時間の値が一致しません");
            }
        }
        engineer.setDormant(false);
        engineer.setSkillSheet(this.skillSheet);
        engineer.setIntroduction(this.introduction);
        return engineer;
    }
}
