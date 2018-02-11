package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.NumberTreatmentForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by khanhlvb on 2/11/18.
 */
@Controller
public class NumberTreatmentController {
    @RequestMapping(value = "/numberTreatment", method = RequestMethod.GET)
    public String getNumberTreatmentSettings(Model model) {
        NumberTreatmentForm form = new NumberTreatmentForm();
        model.addAttribute("numberTreatmentForm", form);
        return "numbertreatment/form";
    }
}
