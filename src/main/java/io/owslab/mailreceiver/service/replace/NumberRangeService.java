package io.owslab.mailreceiver.service.replace;

import com.mariten.kanatools.KanaConverter;
import io.owslab.mailreceiver.dao.NumberRangeDAO;
import io.owslab.mailreceiver.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by khanhlvb on 2/23/18.
 */
@Service
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

    public List<NumberRange> getList(){
        return (List<NumberRange>) numberRangeDAO.findAll();
    }

    public void buildNumberRangeForEmail(Email email){
        if(email == null) return;
        String input = email.getOptimizedBody();
        if(input == null || input.length() == 0) return;

        List<ReplaceLetter> bfReplaceLetters = replaceLetterService.getSignificantList(true);
        List<ReplaceLetter> afReplaceLetters = replaceLetterService.getSignificantList(false);
        if(bfReplaceLetters.size() == 0 && afReplaceLetters.size() == 0) return;
        NumberTreatment numberTreatment = numberTreatmentService.getFirst();
        List<ReplaceNumber> replaceNumbers = replaceNumberService.getList();
        List<ReplaceUnit> replaceUnits = replaceUnitService.getList();

        String optimizedInput = optimizeText(input);

        List<NumberElement> numberElements = buildListNumber(optimizedInput);
        for(NumberElement numberElement : numberElements){
            Double number = numberElement.getValue();
            if(number == null) continue;
            NumberResult realNumberResult = findRealNumber(replaceNumbers, optimizedInput, numberElement);
            if(!isValidNumber(numberTreatment, realNumberResult.getValue())) continue;
            ReplaceLetterResult bfNumberLetterResult = findMatchReplaceLetter(bfReplaceLetters, optimizedInput,
                    numberElement.getStartAt(), true);
            ReplaceUnit afNumberUnit = findMatchReplaceUnit(replaceUnits, optimizedInput, realNumberResult.getEndAt());
            int afEndAt = afNumberUnit != null ? realNumberResult.getEndAt() + afNumberUnit.getUnit().length() : realNumberResult.getEndAt();
            ReplaceLetterResult afNumberLetterResult = findMatchReplaceLetter(afReplaceLetters, optimizedInput,
                    afEndAt, false);
            if(bfNumberLetterResult != null){
                ReplaceLetter bfNumberLetter = bfNumberLetterResult.getLetter();
                System.out.println(bfNumberLetter.getLetter() + " " + realNumberResult.getValue() + " " + (bfNumberLetterResult.getStartAt() - bfNumberLetter.getLetter().length()));
            }
            if(afNumberLetterResult != null){
                ReplaceLetter afNumberLetter = afNumberLetterResult.getLetter();
                System.out.println(afNumberLetter.getLetter() + " " + realNumberResult.getValue() + " " + afNumberLetterResult.getStartAt());
            }
        }
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
        Pattern p = Pattern.compile("-?([0-9]*[.])?[0-9]+");
        Matcher m = p.matcher(content);
        while (m.find()) {
            NumberElement numberElement = new NumberElement(m.group(), m.start());
            result.add(numberElement);
        }
        return result;
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
            result = new NumberResult(realNumber, realStartAt, realEndAt);
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

        public NumberResult(Double value, int startAt, int endAt) {
            this.value = value;
            this.startAt = startAt;
            this.endAt = endAt;
        }

        public NumberResult(NumberElement numberElement) {
            this.value = numberElement.getValue();
            this.startAt = numberElement.getStartAt();
            this.endAt = numberElement.getEndAt();
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
}
