package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.exception.ErrorCodeException;
import io.owslab.mailreceiver.form.NumberTreatmentForm;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.model.NumberRange;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.response.CodeResponseBody;
import io.owslab.mailreceiver.service.replace.*;
import io.owslab.mailreceiver.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by khanhlvb on 2/11/18.
 */
@Controller
@RequestMapping("/user/")
public class NumberTreatmentController {

    private static final Logger logger = LoggerFactory.getLogger(NumberTreatmentController.class);

    public static final int ILLLEGIT_REPLACE_NUMBER_ERROR_CODE = 600;
    public static final int ILLLEGIT_REPLACE_UNIT_ERROR_CODE = 601;
    public static final int ILLLEGIT_NUMBER_FORMAT = 602;

    @Autowired
    private ReplaceNumberService replaceNumberService;

    @Autowired
    private ReplaceUnitService replaceUnitService;

    @Autowired
    private ReplaceLetterService replaceLetterService;

    @Autowired
    private NumberTreatmentService numberTreatmentService;

    @Autowired
    private NumberRangeService numberRangeService;

    @RequestMapping(value = "/numberTreatment", method = RequestMethod.GET)
    public String getNumberTreatmentSettings(Model model) {
        NumberTreatment numberTreatment = numberTreatmentService.getFirst();
        NumberTreatmentForm form = numberTreatment != null ?
                new NumberTreatmentForm(numberTreatment) : new NumberTreatmentForm();
        List<ReplaceNumber> replaceNumbers = replaceNumberService.getList();
        form.setReplaceNumberList(replaceNumbers);
        List<ReplaceUnit> replaceUnits = replaceUnitService.getList();
        form.setReplaceUnitList(replaceUnits);
        List<ReplaceLetter> replaceLetters = replaceLetterService.getList();
        form.setReplaceLetterList(replaceLetters);
        model.addAttribute("numberTreatmentForm", form);
        return "user/numbertreatment/form";
    }

    @PostMapping("/numberTreatment")
    @ResponseBody
    public ResponseEntity<?> saveNumberTreatmentSetting(
            Model model,
            @Valid @RequestBody NumberTreatmentForm numberTreatmentForm, BindingResult bindingResult) {
        CodeResponseBody result = new CodeResponseBody();
        try {
            if (bindingResult.hasErrors()) {
                result.setMsg(bindingResult.getAllErrors()
                        .stream().map(x -> x.getDefaultMessage())
                        .collect(Collectors.joining(",")));
                throw new ErrorCodeException(HttpServletResponse.SC_BAD_REQUEST);
            }
            List<ReplaceNumber> replaceNumbers = numberTreatmentForm.getReplaceNumberList();
            List<ReplaceUnit> replaceUnits = numberTreatmentForm.getReplaceUnitList();
            for(ReplaceNumber replaceNumber : replaceNumbers){
                int illegitIndex = StringUtils.indexOfAny(replaceNumber.getCharacter(), new String[]{".", ",", "，"});
                if(illegitIndex >= 0){
                    throw new ErrorCodeException(ILLLEGIT_REPLACE_NUMBER_ERROR_CODE);
                }
            }
            for(ReplaceUnit replaceUnit : replaceUnits){
                int illegitIndex = StringUtils.indexOfAny(replaceUnit.getUnit(), new String[]{".", ",", "，"});
                if(illegitIndex >= 0){
                    throw new ErrorCodeException(ILLLEGIT_REPLACE_UNIT_ERROR_CODE);
                }
                int illegitIndex2 = StringUtils.indexOfAny(replaceUnit.getReplaceUnit(), new String[]{".", ",", "，"});
                if(illegitIndex2 >= 0){
                    throw new ErrorCodeException(ILLLEGIT_REPLACE_UNIT_ERROR_CODE);
                }
            }
            List<ReplaceLetter> replaceLetters = numberTreatmentForm.getReplaceLetterList();
            numberTreatmentService.saveForm(numberTreatmentForm);
            replaceNumberService.saveList(replaceNumbers);
            replaceUnitService.saveList(replaceUnits);
            replaceLetterService.saveList(replaceLetters);
            //Clear cache number range
            numberRangeService.clearFullRangeCache();
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        }
        catch (ErrorCodeException e) {
            logger.error("ErrorCodeException: ", e.getMessage());
            result.setMsg(e.getMessage());
            result.setErrorCode(e.getErrorCode());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
        catch (NumberFormatException e) {
            logger.error("NumberFormatException: ", e.getMessage());
            result.setMsg(e.getMessage());
            result.setErrorCode(ILLLEGIT_NUMBER_FORMAT);
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/numberTreatment/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete() {
        try {
            numberTreatmentService.deleteAll();
            replaceNumberService.deleteAll();
            replaceUnitService.deleteAll();
            replaceLetterService.deleteAll();
            //Clear cache number range
            numberRangeService.clearFullRangeCache();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
