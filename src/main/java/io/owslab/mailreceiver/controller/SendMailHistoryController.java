package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dao.SentMailHistoryDAO;
import io.owslab.mailreceiver.dto.SentMailHistoryDTO;
import io.owslab.mailreceiver.model.SentMailHistory;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 6/28/18.
 */

@Controller
@RequestMapping("/user/")
public class SendMailHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(SendMailHistoryController.class);

    @Autowired
    private SentMailHistoryDAO sentMailHistoryDAO;

    @RequestMapping(value = { "/sendMailHistory" }, method = RequestMethod.GET)
    public String getHelp(Model model, HttpServletRequest request) {
        return "user/sendMailHistory";
    }

    @RequestMapping(value = { "/sendMailHistoryData"}, method = RequestMethod.GET)


    @GetMapping("/sendMailHistoryData")
    @ResponseBody
    public ResponseEntity<?> getSendMailHistoryData() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<SentMailHistory> rawHistories = (List<SentMailHistory>)sentMailHistoryDAO.findAll();
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