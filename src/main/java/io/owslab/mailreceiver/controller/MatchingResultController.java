package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.utils.SelectOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by khanhlvb on 3/5/18.
 */
@Controller
@RequestMapping("/user/")
public class MatchingResultController {

    @RequestMapping(value = "/matchingResult", method = RequestMethod.GET)
    public String getMatchingResult(Model model) {
        return "user/matching/result";
    }
}
