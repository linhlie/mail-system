package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.NumberTreatmentForm;
import io.owslab.mailreceiver.model.ReplaceNumber;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.replace.ReplaceNumberService;
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
public class NumberTreatmentController {
    @Autowired
    private ReplaceNumberService replaceNumberService;

    @RequestMapping(value = "/numberTreatment", method = RequestMethod.GET)
    public String getNumberTreatmentSettings(Model model) {
        NumberTreatmentForm form = new NumberTreatmentForm();
        List<ReplaceNumber> replaceNumbers = replaceNumberService.getList();
        form.setReplaceNumberList(replaceNumbers);
        model.addAttribute("numberTreatmentForm", form);
        return "numbertreatment/form";
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
            //TODO:  do something
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }
}
