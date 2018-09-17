package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.enums.CompanyType;
import io.owslab.mailreceiver.enums.StockShareType;
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
    private String name;
    private String kanaName;
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

    public CSVEngineerDTO(){
    }

    public CSVEngineerDTO(String name, String kanaName, String mailAddress, String employmentStatus, String partnerCode,
                          String projectPeriodStart, String projectPeriodEnd, String autoExtend, String extendMonth,
                          String matchingWord, String notGoodWord, String monetaryMoney, String stationLine,
                          String stationNearest, String commutingTime, String skillSheet) {
        this.name = name;
        this.kanaName = kanaName;
        this.mailAddress = mailAddress;
        this.employmentStatus = employmentStatus;
        this.partnerCode = partnerCode;
        this.projectPeriodStart = projectPeriodStart;
        this.projectPeriodEnd = projectPeriodEnd;
        this.autoExtend = autoExtend;
        this.extendMonth = extendMonth;
        this.matchingWord = matchingWord;
        this.notGoodWord = notGoodWord;
        this.monetaryMoney = monetaryMoney;
        this.stationLine = stationLine;
        this.stationNearest = stationNearest;
        this.commutingTime = commutingTime;
        this.skillSheet = skillSheet;
    }

    public CSVEngineerDTO(Engineer engineer, BusinessPartner partner) {
        this.setName(engineer.getName());
        this.setKanaName(engineer.getKanaName());
        this.setMailAddress(engineer.getMailAddress());
        this.setEmploymentStatus(Integer.toString(engineer.getEmploymentStatus()));
        this.setPartnerCode(partner.getPartnerCode());
        this.setProjectPeriodStart(Utils.formatTimestamp(Utils.DATE_FORMAT_2, engineer.getProjectPeriodStart()));
        this.setProjectPeriodEnd(Utils.formatTimestamp(Utils.DATE_FORMAT_2, engineer.getProjectPeriodEnd()));
        this.setAutoExtend(Boolean.toString(engineer.isAutoExtend()).toUpperCase());
        this.setExtendMonth(Objects.toString(engineer.getExtendMonth(), null));
        this.setMatchingWord(engineer.getMatchingWord());
        this.setNotGoodWord(engineer.getNotGoodWord());
        this.setMonetaryMoney(engineer.getMonetaryMoney());
        this.setStationLine(engineer.getStationLine());
        this.setStationNearest(engineer.getStationNearest());
        this.setCommutingTime(Objects.toString(engineer.getCommutingTime(), null));
        this.setSkillSheet(engineer.getSkillSheet());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKanaName() {
        return kanaName;
    }

    public void setKanaName(String kanaName) {
        this.kanaName = kanaName;
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
        engineer.setName(this.name);
        engineer.setKanaName(this.kanaName);
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
        try {
            from = Utils.parseDateStr(this.projectPeriodStart);
        } catch (ParseException pe) {
            throw new EngineerFieldValidationException("案件期間「開始」の値が一致しません");
        }
        try {
            to = Utils.parseDateStr(this.projectPeriodEnd);
        } catch (ParseException pe) {
            throw new EngineerFieldValidationException("案件期間「終了」の値が一致しません");
        }
        engineer.setProjectPeriodStart(Utils.atStartOfDay(from).getTime());
        engineer.setProjectPeriodEnd(Utils.atEndOfDay(to).getTime());
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
        return engineer;
    }
}
