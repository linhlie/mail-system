package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.NumberTreatmentForm;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.model.NumberRange;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.replace.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            numberTreatmentService.saveForm(numberTreatmentForm);
            List<ReplaceNumber> replaceNumbers = numberTreatmentForm.getReplaceNumberList();
            replaceNumberService.saveList(replaceNumbers);
            List<ReplaceUnit> replaceUnits = numberTreatmentForm.getReplaceUnitList();
            replaceUnitService.saveList(replaceUnits);
            List<ReplaceLetter> replaceLetters = numberTreatmentForm.getReplaceLetterList();
            replaceLetterService.saveList(replaceLetters);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }
}
