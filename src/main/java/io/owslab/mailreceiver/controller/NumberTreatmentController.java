package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.NumberTreatmentForm;
import io.owslab.mailreceiver.model.ReplaceNumber;
import io.owslab.mailreceiver.service.replace.ReplaceNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

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
}
