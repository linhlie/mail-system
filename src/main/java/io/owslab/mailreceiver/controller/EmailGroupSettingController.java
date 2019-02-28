package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dao.VariableDAO;
import io.owslab.mailreceiver.form.NumberTreatmentForm;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.mail.EmailAddressGroupService;
import io.owslab.mailreceiver.service.statistics.EmailStatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * Created by khanhlvb on 3/2/18.
 */
@Controller
@RequestMapping("/user/")
public class EmailGroupSettingController {
    private static final Logger logger = LoggerFactory.getLogger(FuzzyWordController.class);

    @Autowired
    EmailAddressGroupService emailAddressGroupService;

    @RequestMapping(value = "/emailGroupSetting", method = RequestMethod.GET)
    public String emailGroupSetting(Model model) {
        return "user/emailGroupManage/emailGroupSetting";
    }

    @RequestMapping(value = { "/emailGroupSetting/getListEmailAddressGroup" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getListGroup() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<EmailAddressGroup> listGroup = emailAddressGroupService.getList();
            result.setList(listGroup);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getListGroup: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/emailGroupSetting/addEmailAddressGroup")
    @ResponseBody
    public ResponseEntity<?> addEmailAddressGroup(@Valid @RequestBody EmailAddressGroup emailAddressGroup, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            emailAddressGroupService.createGroup(emailAddressGroup);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/emailGroupSetting/editEmailAddressGroup")
    @ResponseBody
    public ResponseEntity<?> editEmailAddressGroup(@Valid @RequestBody EmailAddressGroup emailAddressGroup, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            emailAddressGroupService.updateGroup(emailAddressGroup);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/emailGroupSetting/deleteEmailAddressGroup/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteEmailGroup(@PathVariable("id") long id) {
        try {
            emailAddressGroupService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
