package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dao.SentMailHistoryDAO;
import io.owslab.mailreceiver.dto.SentMailHistoryDTO;
import io.owslab.mailreceiver.form.ExtractForm;
import io.owslab.mailreceiver.form.SentMailHistoryForm;
import io.owslab.mailreceiver.model.SentMailHistory;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.mail.SendMailHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by khanhlvb on 6/28/18.
 */

@Controller
@RequestMapping("/user/")
public class SendMailHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(SendMailHistoryController.class);

    @Autowired
    private SendMailHistoryService sendMailHistoryService;


    @RequestMapping(value = { "/sendMailHistory" }, method = RequestMethod.GET)
    public String getHelp(Model model, HttpServletRequest request) {
        return "user/sendMailHistory";
    }

    @PostMapping("/sendMailHistoryData")
    @ResponseBody
    public ResponseEntity<?> getSendMailHistoryData(Model model, @Valid @RequestBody SentMailHistoryForm sentMailHistoryForm, BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<SentMailHistory> rawHistories = sendMailHistoryService.search(sentMailHistoryForm);
            List<SentMailHistoryDTO> histories = new ArrayList<>();
            for(SentMailHistory history : rawHistories) {
                histories.add(new SentMailHistoryDTO(history));
            }
            result.setMsg("done");
            result.setStatus(true);
            result.setList(histories);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("getSendMailHistoryData" + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }
}