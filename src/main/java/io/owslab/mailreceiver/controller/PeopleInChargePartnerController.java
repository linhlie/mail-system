package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.PartnerForPeopleInChargeDTO;
import io.owslab.mailreceiver.dto.PeopleInChargePartnerDTO;
import io.owslab.mailreceiver.form.DomainAvoidRegisterForm;
import io.owslab.mailreceiver.form.EmailsAvoidRegisterPeopleInChargeForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.PeopleInChargePartner;
import io.owslab.mailreceiver.model.PeopleInChargePartnerUnregister;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import io.owslab.mailreceiver.service.expansion.PeopleInChargePartnerService;
import io.owslab.mailreceiver.service.expansion.PeopleInChargePartnerUnregisterService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/expansion/")
public class PeopleInChargePartnerController {
    private static final Logger logger = LoggerFactory.getLogger(PeopleInChargePartnerController.class);

    @Autowired
    BusinessPartnerService partnerService;

    @Autowired
    PeopleInChargePartnerService peopleInChargePartnerService;

    @Autowired
    PeopleInChargePartnerUnregisterService peopleInChargePartnerUnregisterService;

    @RequestMapping(value = { "/peopleInChargePartner" }, method = RequestMethod.GET)
    public String getBusinessPartnerRegist(Model model, HttpServletRequest request) {
        return "expansion/peopleInChargePartner";
    }

    @RequestMapping(value = { "/peopleInChargePartner/getPartners" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getPartners() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<PartnerForPeopleInChargeDTO> partners = partnerService.getPartnerForPeopleInCharge();
            result.setList(partners);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getPartners: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/peopleInChargePartner/getPeopleInChargePartners/{partnerId}" , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getPeopleInChargePartners(@PathVariable("partnerId") long partnerId) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<PeopleInChargePartnerDTO> listPeole = peopleInChargePartnerService.getByPartnerId(partnerId);
            result.setList(listPeole);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getDetailPeople: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/peopleInChargePartner/info/{id}" , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDetailPeople(@PathVariable("id") long id) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            PeopleInChargePartner peole = peopleInChargePartnerService.getById(id);
            List<PeopleInChargePartner> list = new ArrayList<>();
            if(peole != null){
                list.add(peole);
            }
            result.setList(list);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getDetailPeople: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/peopleInChargePartner/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addPeopleInChargePartner(
            @Valid @RequestBody PeopleInChargePartner people, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            peopleInChargePartnerService.addPeopleInChargePartner(people);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("addPeopleInChargePartner: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/peopleInChargePartner/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> editPeopleInChargePartner(
            @Valid @RequestBody PeopleInChargePartner people, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            peopleInChargePartnerService.editPeopleInChargePartner(people);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("editPeopleInChargePartner: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/peopleInChargePartner/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        try {
            peopleInChargePartnerService.deletePeople(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = { "/peopleInChargePartnerUnregister/list" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getPeopleInChargePartnerUnregister() {AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<PeopleInChargePartnerUnregister> listPeople = peopleInChargePartnerUnregisterService.getPeopleInChargeUnregisterByStatus(PeopleInChargePartnerUnregister.Status.ALLOW_REGISTER);
            result.setList(listPeople);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getPeopleInChargePartnerUnregister: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }


    @RequestMapping(value = "/peopleInChargePartnerUnregister/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deletePeopleInChargePartnerUnregister(@PathVariable("id") long id) {
        try {
            peopleInChargePartnerUnregisterService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/peopleInChargePartnerUnregister/avoidRegister/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> avoidRegister(@PathVariable("id") long id) {
        try {
            peopleInChargePartnerUnregisterService.changeFromAllowToAvoid(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = { "/emailAvoidRegisterPeopleInCharge" }, method = RequestMethod.GET)
    public String getPeopleInChargeAvoidRegister(Model model, HttpServletRequest request) {
        return "expansion/PeopleInChargePartnerAvoidRegister";
    }

    @RequestMapping(value = { "/emailsAvoidRegisterPeopleInCharge/list" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getEmailsAvoidRegisterPeopleInCharge() {AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<PeopleInChargePartnerUnregister> listPeople = peopleInChargePartnerUnregisterService.getPeopleInChargeUnregisterByStatus(PeopleInChargePartnerUnregister.Status.AVOID_REGISTER);
            result.setList(listPeople);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getPeopleInChargePartnerUnregister: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/emailsAvoidRegisterPeopleInCharge/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveEmailAvoidRegister(
            @Valid @RequestBody EmailsAvoidRegisterPeopleInChargeForm form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            peopleInChargePartnerUnregisterService.saveEmailsAvoidRegisterPeopleInCharge(form);
            result.setMsg("done");
            result.setStatus(true);
        }  catch (Exception e) {
            e.printStackTrace();
            logger.error("updateEmailAvoidRegister: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }
}
