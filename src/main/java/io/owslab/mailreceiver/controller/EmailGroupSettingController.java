package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.EmailAccountToSendMailDTO;
import io.owslab.mailreceiver.dto.EmailsAddressInGroupDTO;
import io.owslab.mailreceiver.form.EmailsAddressInGroupForm;
import io.owslab.mailreceiver.form.IdsForm;
import io.owslab.mailreceiver.form.SchedulerSendEmailForm;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.response.EmailGroupResponseBody;
import io.owslab.mailreceiver.service.expansion.PeopleInChargePartnerService;
import io.owslab.mailreceiver.service.mail.EmailAddressGroupService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
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

    @Autowired
    PeopleInChargePartnerService peopleInChargePartnerService;

    @Autowired
    MailAccountsService mailAccountsService;

    @RequestMapping(value = "/emailGroupSetting", method = RequestMethod.GET)
    public String emailGroupSetting(Model model) {
        return "user/emailGroupManage/emailGroupSetting";
    }

    @RequestMapping(value = { "/emailGroupSetting/getListEmailAddressGroup" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getListGroup(@RequestParam(value = "groupName", required = false) String groupName) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<EmailAddressGroup> listGroup = new ArrayList<>();
            if(groupName==null || groupName.equals("")){
                listGroup = emailAddressGroupService.getGroupList();
            }else{
                listGroup = emailAddressGroupService.searchGroup(groupName);
            }
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
            emailAddressGroupService.deleteGroup(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = { "/emailGroupSetting/getListEmailAddressList" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getEmailList(@RequestParam(value = "id", required = true) long id, @RequestParam(value = "search", required = false)  String search) {
        EmailGroupResponseBody result = new EmailGroupResponseBody();
        try {
            List<EmailsAddressInGroupDTO> listEmail = new ArrayList<>();
            if(search==null || search.equals("")){
                listEmail = emailAddressGroupService.getEmailList(id);
            }else{
                listEmail = emailAddressGroupService.searchEmailList(id, search);
            }
            List<PeopleInChargePartner> listPeople = peopleInChargePartnerService.getAll();
            result.setList(listEmail);
            result.setListPeople(listPeople);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getListEmailAddressList: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/emailGroupSetting/addEmailAddressToList")
    @ResponseBody
    public ResponseEntity<?> addEmailAddressToList(@Valid @RequestBody EmailsAddressInGroupForm form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            emailAddressGroupService.addListEmailAddress(form);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/emailGroupSetting/deleteEmailAddressInGroup/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteEmailInGroup(@PathVariable("id") long id) {
        try {
            emailAddressGroupService.deleteEmail(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @RequestMapping(value = "/schedulerSendEmail", method = RequestMethod.GET)
    public String schedulerSendEmail(Model model) {
        return "user/emailGroupManage/schedulerSendEmail";
    }

    @RequestMapping(value = { "/schedulerSendEmail/getListEmailSender" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getListEmailSender() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<EmailAccountToSendMailDTO> listEmailAccount = mailAccountsService.getListEmailAccountToSendMail();
            result.setList(listEmailAccount);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getListEmailSender: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/schedulerSendEmail/getEmailReceivers")
    @ResponseBody
    public ResponseEntity<?> getEmailReceivers(@Valid @RequestBody IdsForm ids, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<String> email = emailAddressGroupService.getEmailReceivers(ids);
            result.setMsg("done");
            result.setList(email);
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = { "/emailGroupSetting/getListEmailAddressAndGroup" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getListEmailAndGroup() {
        EmailGroupResponseBody result = new EmailGroupResponseBody();
        try {
            List<EmailAddressGroup> listGroup = emailAddressGroupService.getGroupList();
            List<PeopleInChargePartner> listPeople = peopleInChargePartnerService.getAll();
            result.setList(listGroup);
            result.setListPeople(listPeople);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getListGroup: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/schedulerSendEmail/createSchedulerSendEmail")
    @ResponseBody
    public ResponseEntity<?> createSchedulerSendEmail(@Valid @RequestBody SchedulerSendEmailForm scheduler, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            emailAddressGroupService.createScheduler(scheduler);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/showSchedulerSendEmail", method = RequestMethod.GET)
    public String showSchedulerSendEmail(Model model) {
        return "user/emailGroupManage/showSchedulerSendEmail";
    }

    @RequestMapping(value = { "schedulerSendEmail/getListSchedulerData" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getListSchedulerData() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<SchedulerSendEmail> list = emailAddressGroupService.getAllScheduler();
            result.setList(list);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getListSchedulerData: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = { "schedulerSendEmail/getSchedulerData/{id}" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getSchedulerData(@PathVariable("id") long id) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<SchedulerSendEmail> list = emailAddressGroupService.getScheduler(id);
            result.setList(list);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getSchedulerData: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/schedulerSendEmail/deleteSchedulerSendEmail/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteSchedulerSendEmail(@PathVariable("id") long id) {
        try {
            emailAddressGroupService.deleteSchedulerSendEmail(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/schedulerSendEmail/changeStatusSchedulerEmail")
    @ResponseBody
    public ResponseEntity<?> changeStatusSchedulerEmail(@Valid @RequestBody SchedulerSendEmail scheduler, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            emailAddressGroupService.changeStatusScheduler(scheduler);
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
