package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.utils.FilterRule;


public class StatisticConditionForm {

    private String matchingWords;
    private FilterRule statisticConditionData;
    private boolean statisticByDay;
    private boolean statisticByHour;
    private boolean statisticByDomain;
    private boolean statisticByWord;

    public StatisticConditionForm() {
    }

    public String getMatchingWords() {
        return matchingWords;
    }

    public void setMatchingWords(String matchingWords) {
        this.matchingWords = matchingWords;
    }

    public FilterRule getStatisticConditionData() {
        return statisticConditionData;
    }

    public void setStatisticConditionData(FilterRule statisticConditionData) {
        this.statisticConditionData = statisticConditionData;
    }

    public boolean isStatisticByDay() {
        return statisticByDay;
    }

    public void setStatisticByDay(boolean statisticByDay) {
        this.statisticByDay = statisticByDay;
    }

    public boolean isStatisticByHour() {
        return statisticByHour;
    }

    public void setStatisticByHour(boolean statisticByHour) {
        this.statisticByHour = statisticByHour;
    }

    public boolean isStatisticByDomain() {
        return statisticByDomain;
    }

    public void setStatisticByDomain(boolean statisticByDomain) {
        this.statisticByDomain = statisticByDomain;
    }

    public boolean isStatisticByWord() {
        return statisticByWord;
    }

    public void setStatisticByWord(boolean statisticByWord) {
        this.statisticByWord = statisticByWord;
    }
}
