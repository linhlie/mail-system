package io.owslab.mailreceiver.service.replace;

import com.mariten.kanatools.KanaConverter;
import io.owslab.mailreceiver.dao.NumberRangeDAO;
import io.owslab.mailreceiver.enums.NumberCompare;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.utils.FullNumberRange;
import io.owslab.mailreceiver.utils.SimpleNumberRange;
import org.codehaus.groovy.runtime.powerassert.SourceText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by khanhlvb on 2/23/18.
 */
@Service
@CacheConfig(cacheNames = "short_term_data")
public class NumberRangeService {

    @Autowired
    private NumberRangeDAO numberRangeDAO;

    @Autowired
    private NumberTreatmentService numberTreatmentService;

    @Autowired
    private ReplaceNumberService replaceNumberService;

    @Autowired
    private ReplaceUnitService replaceUnitService;

    @Autowired
    private ReplaceLetterService replaceLetterService;

    private HashMap<String, List<FullNumberRange>> fullRangeMap = new HashMap<String, List<FullNumberRange>>();

    @Cacheable(key="\"NumberRangeService:getList\"")
    public List<NumberRange> getList(){
        return (List<NumberRange>) numberRangeDAO.findAll();
    }

    public List<FullNumberRange> buildNumberRangeForInput(String cacheId, String input){
        return buildNumberRangeForInput(cacheId, input, false);
    }
    public List<FullNumberRange> buildNumberRangeForInput(String cacheId, String input, boolean individual){
        return buildNumberRangeForInput(cacheId, input, individual, true);
    }

    public List<FullNumberRange> buildNumberRangeForInput(String cacheId, String input, boolean individual, boolean useCache){
        List<FullNumberRange> result = new ArrayList<>();
        if(input == null || input.length() == 0) return result;
        String keyMap = cacheId + Boolean.toString(individual);
        if(useCache && fullRangeMap.get(keyMap) != null){
            return fullRangeMap.get(keyMap);
        }
        LinkedHashMap<String, ArrayList<SimpleNumberRange>> rangeMap = new LinkedHashMap<>();

        List<ReplaceLetter> bfReplaceLetters = replaceLetterService.getSignificantList(true);
        List<ReplaceLetter> afReplaceLetters = replaceLetterService.getSignificantList(false);
        NumberTreatment numberTreatment = numberTreatmentService.getFirst();
        List<ReplaceNumber> replaceNumbers = replaceNumberService.getList();
        List<ReplaceUnit> replaceUnits = replaceUnitService.getList();

        String optimizedInput = optimizeText(input);
        List<NumberElement> numberElements = buildListNumber(optimizedInput);
        for(NumberElement numberElement : numberElements){
            Double number = numberElement.getValue();
            if(number == null) continue;
            NumberResult realNumberResult = findRealNumber(replaceNumbers, optimizedInput, numberElement);
            ReplaceLetterResult bfNumberLetterResult = findMatchReplaceLetter(bfReplaceLetters, optimizedInput,
                    numberElement.getStartAt(), true);
            ReplaceUnit afNumberUnit = findMatchReplaceUnit(replaceUnits, optimizedInput, realNumberResult.getEndAt());
            int afEndAt = afNumberUnit != null ? realNumberResult.getEndAt() + afNumberUnit.getUnit().length() : realNumberResult.getEndAt();
            String afNumberUnitText = afNumberUnit != null ? afNumberUnit.getUnit() : null;
            ReplaceLetterResult afNumberLetterResult = findMatchReplaceLetter(afReplaceLetters, optimizedInput,
                    afEndAt, false);

            SimpleNumberRange simpleNumberRange;
            String position;
            if(bfNumberLetterResult != null || afNumberLetterResult != null){
                if(bfNumberLetterResult != null){
                    ReplaceLetter bfNumberLetter = bfNumberLetterResult.getLetter();
                    position = Integer.toString((bfNumberLetterResult.getStartAt() - bfNumberLetter.getLetter().length()));
                    simpleNumberRange = new SimpleNumberRange(
                            realNumberResult.getValue(),
                            bfNumberLetter.getReplace(),
                            number,
                            realNumberResult.getReplaceText(),
                            afNumberUnitText,
                            bfNumberLetter.getLetter(),
                            true);
                    simpleNumberRange.setReplaceValue(realNumberResult.getReplaceValue());
                    addToList(rangeMap, position, simpleNumberRange);
                }
                if(afNumberLetterResult != null){
                    ReplaceLetter afNumberLetter = afNumberLetterResult.getLetter();
                    position = Integer.toString(afNumberLetterResult.getStartAt());
                    simpleNumberRange = new SimpleNumberRange(
                            realNumberResult.getValue(),
                            afNumberLetter.getReplace(),
                            number,
                            realNumberResult.getReplaceText(),
                            afNumberUnitText,
                            afNumberLetter.getLetter(),
                            false
                    );
                    simpleNumberRange.setReplaceValue(realNumberResult.getReplaceValue());
                    addToList(rangeMap, position, simpleNumberRange);
                }
            } else {
                position = Integer.toString(realNumberResult.getStartAt());
                simpleNumberRange = new SimpleNumberRange(
                        realNumberResult.getValue(),
                        number,
                        realNumberResult.getReplaceText(),
                        afNumberUnitText,
                        null,
                        false
                );
                simpleNumberRange.setReplaceValue(realNumberResult.getReplaceValue());
                addToList(rangeMap, position, simpleNumberRange);
            }
        }

        for(Map.Entry<String, ArrayList<SimpleNumberRange>> entry : rangeMap.entrySet()) {
            List<SimpleNumberRange> rangeList = entry.getValue();
            if(rangeList != null) {
                if(individual){
                    for(SimpleNumberRange range : rangeList){
                        if(isValidRange(numberTreatment, range)){
                            result.add(new FullNumberRange(range));
                        }
                    }
                } else {
                    if(rangeList.size() == 2){
                        SimpleNumberRange firstRange = rangeList.get(0);
                        SimpleNumberRange secondRange = rangeList.get(1);
                        if(secondRange.getReplaceValue() != 1 && firstRange.getReplaceValue() == 1){
                            firstRange.multiple((double) secondRange.getReplaceValue());
                        } else if (secondRange.getReplaceValue() == 1 && firstRange.getReplaceValue() != 1) {
                            secondRange.multiple((double) firstRange.getReplaceValue());
                        }
                        boolean isFirstRangeValid = isValidRange(numberTreatment, firstRange);
                        boolean isSecondRangeValid = isValidRange(numberTreatment, secondRange);
                        if(isFirstRangeValid && isSecondRangeValid){
                            result.add(new FullNumberRange(firstRange, secondRange));
                        } else if (isFirstRangeValid) {
                            result.add(new FullNumberRange(firstRange));
                        } else if (isSecondRangeValid) {
                            result.add(new FullNumberRange(secondRange));
                        }
                    } else if(rangeList.size() == 1) {
                        if(isValidRange(numberTreatment, rangeList.get(0))){
                            result.add(new FullNumberRange(rangeList.get(0)));
                        }
                    }
                }
            }
        }

//        for(FullNumberRange fullNumberRange : result) {
//            System.out.println("fullNumberRange: " + fullNumberRange.toString());
//        }
        if(useCache){
            fullRangeMap.put(keyMap, result);
        }
        return result;
    }

    private String optimizeText(String raw){
        String result;
        result = raw.replaceAll("　", " ");
        result = result.replaceAll("[\\s&&[^\\n]]+", " "); //compress all non-newline whitespaces to single space
        int conv_op_flags = 0;
        conv_op_flags |= KanaConverter.OP_HAN_KATA_TO_ZEN_KATA;
        conv_op_flags |= KanaConverter.OP_ZEN_ASCII_TO_HAN_ASCII;
        result = KanaConverter.convertKana(result, conv_op_flags);
        return result;
    }

    private ArrayList<NumberElement> buildListNumber(String content){
        //TODO: need better regex handle comma and period
        ArrayList<NumberElement> result = new ArrayList<NumberElement>();
        Pattern p = Pattern.compile("([0-9]*[.])?[0-9]+");
        Matcher m = p.matcher(content);
        while (m.find()) {
            NumberElement numberElement = new NumberElement(m.group(), m.start());
            result.add(numberElement);
        }
        return result;
    }

    private boolean isValidRange(NumberTreatment numberTreatment, SimpleNumberRange range){
        if(numberTreatment == null) return true;
        if(range.getNumberCompare().equals(NumberCompare.AUTOMATCH)){
            return true;
        } else {
            return isValidNumber(numberTreatment, range.getValue());
        }
    }

    private boolean isValidNumber(NumberTreatment numberTreatment, Double number){
        if(numberTreatment == null) return true;
        Double leftBoundaryValue = numberTreatment.getLeftBoundaryValue();
        int leftBoundaryOperator = numberTreatment.getLeftBoundaryOperator();
        int combineOperator = numberTreatment.getCombineOperator();
        Double rightBoundaryValue = numberTreatment.getRightBoundaryValue();
        int rightBoundaryOperator = numberTreatment.getRightBoundaryOperator();

        boolean leftCheck = compare(leftBoundaryValue, number, leftBoundaryOperator);
        boolean rightCheck = compare(rightBoundaryValue, number, rightBoundaryOperator);
        if(combineOperator == NumberTreatment.CombineOperators.AND) {
            return leftCheck && rightCheck;
        } else if (combineOperator == NumberTreatment.CombineOperators.OR){
            return leftCheck || rightCheck;
        }
        return true;
    }

    private boolean compare(Double boundary, Double number, int operator){
        if(boundary == null) return true;
        boolean result;
        switch (operator){
            case NumberTreatment.BoundaryOperators.GE:
                result = number >= boundary;
                break;
            case NumberTreatment.BoundaryOperators.LE:
                result = number <= boundary;
                break;
            case NumberTreatment.BoundaryOperators.LT:
                result = number < boundary;
                break;
            case NumberTreatment.BoundaryOperators.GT:
                result = number > boundary;
                break;
            default:
                result = true;
        }
        return result;
    }

    private NumberResult findRealNumber(List<ReplaceNumber> replaceNumbers, String content, NumberElement numberElement){
        NumberResult result = new NumberResult(numberElement);
        int fromIndex = numberElement.getEndAt();
        ReplaceNumber replaceNumber = findMatchReplaceNumber(replaceNumbers,
                content, fromIndex);
        if(replaceNumber != null){
            double realNumber = numberElement.getValue() * replaceNumber.getReplaceValue();
            int realEndAt = numberElement.getEndAt() + replaceNumber.getCharacter().length();
            int realStartAt = numberElement.getStartAt();
            result = new NumberResult(realNumber, realStartAt, realEndAt, replaceNumber.getReplaceValue(), replaceNumber.getCharacter());
        }
        return result;
    }

    private ReplaceNumber findMatchReplaceNumber(List<ReplaceNumber> replaceNumbers, String content, int fromIndex){
        String findContent = content.substring(fromIndex);
        ReplaceNumber match = null;
        for(ReplaceNumber replaceNumber : replaceNumbers){
            String rnc = replaceNumber.getCharacter();
            if(findContent.indexOf(rnc) == 0){
                match = replaceNumber;
                break;
            }
        }
        return match;
    }

    private ReplaceUnit findMatchReplaceUnit(List<ReplaceUnit> replaceUnits, String content, int fromIndex){
        String findContent = content.substring(fromIndex);
        ReplaceUnit match = null;
        for(ReplaceUnit replaceUnit : replaceUnits){
            String rnc = replaceUnit.getUnit();
            if(findContent.indexOf(rnc) == 0){
                match = replaceUnit;
                break;
            }
        }
        return match;
    }

    private ReplaceLetterResult findMatchReplaceLetter(List<ReplaceLetter> replaceLetters, String content, int fromIndex, boolean isReverse){
        String findContent = isReverse ? content.substring(0, fromIndex) : content.substring(fromIndex);
        findContent = isReverse ? new StringBuilder(findContent).reverse().toString() : findContent;
        ReplaceLetterResult match = null;
        for(ReplaceLetter replaceLetter : replaceLetters){
            String rlc = replaceLetter.getLetter();
            rlc = isReverse ? new StringBuilder(rlc).reverse().toString() : rlc;
            int startAt = fromIndex;
            if(findContent.indexOf(rlc) == 0){
                match = new ReplaceLetterResult(replaceLetter, startAt);
                break;
            } else if(findContent.indexOf(rlc)==1 && findContent.substring(0, 1).equalsIgnoreCase(" ")){
                startAt = isReverse ? startAt - 1 : startAt + 1;
                match = new ReplaceLetterResult(replaceLetter, startAt);;
                break;
            }
        }
        return match;
    }

    private class NumberElement {
        private String raw;
        private int startAt;
        private int endAt;
        private Double value;

        public NumberElement(String raw, int startAt) {
            this.raw = raw;
            this.startAt = startAt;
            this.endAt = startAt + raw.length();
            try {
                this.value = Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        public String getRaw() {
            return raw;
        }

        public void setRaw(String raw) {
            this.raw = raw;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public int getStartAt() {
            return startAt;
        }

        public void setStartAt(int startAt) {
            this.startAt = startAt;
        }

        public int getEndAt() {
            return endAt;
        }

        public void setEndAt(int endAt) {
            this.endAt = endAt;
        }
    }

    final class NumberResult {
        private final Double value;
        private final int startAt;
        private final int endAt;
        private final int replaceValue;
        private final String replaceText;

        public NumberResult(Double value, int startAt, int endAt, int replaceValue, String replaceText) {
            this.value = value;
            this.startAt = startAt;
            this.endAt = endAt;
            this.replaceValue = replaceValue;
            this.replaceText = replaceText;
        }

        public NumberResult(Double value, int startAt, int endAt, int replaceValue) {
            this(value, startAt, endAt, replaceValue, null);
        }

        public NumberResult(NumberElement numberElement) {
            this.value = numberElement.getValue();
            this.startAt = numberElement.getStartAt();
            this.endAt = numberElement.getEndAt();
            this.replaceValue = 1;
            this.replaceText = null;
        }

        public Double getValue() {
            return value;
        }

        public int getEndAt() {
            return endAt;
        }

        public int getStartAt() {
            return startAt;
        }

        public int getReplaceValue() {
            return replaceValue;
        }

        public String getReplaceText() {
            return replaceText;
        }
    }

    final class ReplaceLetterResult {
        private final ReplaceLetter letter;
        private final int startAt;

        public ReplaceLetterResult(ReplaceLetter letter, int startAt) {
            this.letter = letter;
            this.startAt = startAt;
        }

        public ReplaceLetter getLetter() {
            return letter;
        }

        public int getStartAt() {
            return startAt;
        }
    }

    private synchronized void addToList(LinkedHashMap<String, ArrayList<SimpleNumberRange>> map, String mapKey, SimpleNumberRange range) {
        ArrayList<SimpleNumberRange> rangesList = map.get(mapKey);

        // if list does not exist create it
        if(rangesList == null) {
            rangesList = new ArrayList<SimpleNumberRange>();
            rangesList.add(range);
            map.put(mapKey, rangesList);
        } else {
            // add if item is not already in list
            if(!rangesList.contains(range)) rangesList.add(range);
        }
    }

    public void clearFullRangeCache(){
        fullRangeMap.clear();
    }
}
