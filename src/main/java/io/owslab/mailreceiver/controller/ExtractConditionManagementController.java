package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.model.MatchingConditionSaved;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.condition.MatchingConditionSavedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by khanhlvb on 7/5/18.
 */
@Controller
@RequestMapping("/user/")
public class ExtractConditionManagementController {
    private static final Logger logger = LoggerFactory.getLogger(ExtractConditionManagementController.class);

    @Autowired
    MatchingConditionSavedService conditionSavedService;

    @RequestMapping(value = { "/extractConditionManagement" }, method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        return "user/extractConditionManagement";
    }

    @RequestMapping(value = { "/extractConditionManagement/getListConditionSaved" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getListConditionSaved() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<MatchingConditionSaved> list = conditionSavedService.getListConditionSaved();
            result.setList(list);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getListConditionSaved: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/extractConditionManagement/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addListConditionSaved(@Valid @RequestBody MatchingConditionSaved form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            conditionSavedService.saveConditionSaved(form);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("addListConditionSaved: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/extractConditionManagement/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        try {
            conditionSavedService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/extractConditionManagement/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(@RequestParam("conditionType") int conditionType){
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<MatchingConditionSaved> list = conditionSavedService.findByConditionTypeAndAccountCreatedId(conditionType);
            result.setList(list);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getListConditionSaved: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }
}
