package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.model.MatchingCondition;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.matching.MatchingConditionService;
import io.owslab.mailreceiver.utils.SelectOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by khanhlvb on 3/5/18.
 */
@Controller
@RequestMapping("/user/")
public class MatchingSettingsController {
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
}
