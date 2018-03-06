package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.MatchingConditionForm;
import io.owslab.mailreceiver.model.MatchingCondition;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.matching.MatchingConditionService;
import io.owslab.mailreceiver.utils.SelectOption;
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
 * Created by khanhlvb on 3/5/18.
 */
@Controller
@RequestMapping("/user/")
public class MatchingSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(MatchingSettingsController.class);
    @Autowired
    private List<SelectOption> combineOptions;

    @Autowired
    private List<SelectOption> conditionOptions;

    @Autowired
    private List<SelectOption> mailItemOptions;

    @Autowired
    private List<SelectOption> matchingItemOptions;

    @Autowired
    private MatchingConditionService matchingConditionService;

    @RequestMapping(value = "/matchingSettings", method = RequestMethod.GET)
    public String getMatchingSettings(Model model) {
        model.addAttribute("combineOptions", combineOptions);
        model.addAttribute("conditionOptions", conditionOptions);
        model.addAttribute("mailItemOptions", mailItemOptions);
        model.addAttribute("matchingItemOptions", matchingItemOptions);
        return "user/matching/settings";
    }

    @RequestMapping(value="/matchingSettings/source", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getSourceInJSON (){
        AjaxResponseBody result = new AjaxResponseBody();

        result.setMsg("done");
        result.setStatus(true);
        result.setList(matchingConditionService.getSourceConditionList());
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value="/matchingSettings/destination", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getDestinationInJSON (){
        AjaxResponseBody result = new AjaxResponseBody();

        result.setMsg("done");
        result.setStatus(true);
        result.setList(matchingConditionService.getDestinationConditionList());
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value="/matchingSettings/matching", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getMatchingInJSON (){
        AjaxResponseBody result = new AjaxResponseBody();

        result.setMsg("done");
        result.setStatus(true);
        result.setList(matchingConditionService.getMatchingConditionList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/matchingSettings/saveSource")
    @ResponseBody
    public ResponseEntity<?> saveSourceConditionList(
            Model model,
            @Valid @RequestBody MatchingConditionForm matchingConditionForm, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<MatchingCondition> sourceConditionList = matchingConditionForm.getSourceConditionList();
            matchingConditionService.saveList(sourceConditionList, MatchingCondition.Type.SOURCE);
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

    @PostMapping("/matchingSettings/saveDestination")
    @ResponseBody
    public ResponseEntity<?> saveDestinationConditionList(
            Model model,
            @Valid @RequestBody MatchingConditionForm matchingConditionForm, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<MatchingCondition> destinationConditionList = matchingConditionForm.getDestinationConditionList();
            matchingConditionService.saveList(destinationConditionList, MatchingCondition.Type.DESTINATION);
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

    @PostMapping("/matchingSettings/submitForm")
    @ResponseBody
    public ResponseEntity<?> submitForm(
            Model model,
            @Valid @RequestBody MatchingConditionForm matchingConditionForm, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            matchingConditionService.matching(matchingConditionForm);
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
