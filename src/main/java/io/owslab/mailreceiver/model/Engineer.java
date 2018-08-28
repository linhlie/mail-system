package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

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
    private String name;

    @NotNull
    private String kanaName;

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

    public Engineer() {
    }

    public Engineer(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKanaName() {
        return kanaName;
    }

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

    public boolean isAutoExtend() {
        return autoExtend;
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public void setKanaName(String kanaName) {
        this.kanaName = kanaName;
    }

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
}
