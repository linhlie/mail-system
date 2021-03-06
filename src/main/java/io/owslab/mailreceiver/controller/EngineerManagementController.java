package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.EngineerListItemDTO;
import io.owslab.mailreceiver.dto.PartnerDTO;
import io.owslab.mailreceiver.exception.EngineerException;
import io.owslab.mailreceiver.exception.EngineerNotFoundException;
import io.owslab.mailreceiver.exception.PartnerNotFoundException;
import io.owslab.mailreceiver.form.EngineerFilterForm;
import io.owslab.mailreceiver.form.EngineerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.BusinessPartnerGroup;
import io.owslab.mailreceiver.model.Engineer;
import io.owslab.mailreceiver.model.RelationshipEngineerPartner;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import io.owslab.mailreceiver.service.expansion.EngineerService;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.owslab.mailreceiver.service.transaction.EngineerTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by khanhlvb on 8/15/18.
 */
@Controller
@RequestMapping("/expansion/")
public class EngineerManagementController {
    private static final Logger logger = LoggerFactory.getLogger(EngineerManagementController.class);

    @Autowired
    private EngineerService engineerService;

    @Autowired
    private BusinessPartnerService partnerService;

    @Autowired
    private EngineerTransaction engineerTransaction;

    @RequestMapping(value = { "/engineerManagement" }, method = RequestMethod.GET)
    public String getEngineerManagement(Model model, HttpServletRequest request) {
        return "expansion/engineerManagement";
    }

    @RequestMapping(value = "/engineer/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addEngineer(
            @Valid @RequestBody EngineerForm form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            engineerTransaction.addEngineerAndRelationTransaction(form);
            result.setMsg("done");
            result.setStatus(true);
        } catch (EngineerException pnfe) {
            logger.error("addEngineer PartnerNotFoundException: " + pnfe.getMessage());
            result.setMsg(pnfe.getMessage());
            result.setStatus(false);
        } catch (ParseException pe) {
            logger.error("addEngineer ParseException: " + pe.getMessage());
            result.setMsg("案件期間フォーマットが正しくない");
            result.setStatus(false);
        } catch (Exception e) {
            logger.error("addEngineer: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/engineer/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        try {
            engineerService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/engineer/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateEngineer(
            @Valid @RequestBody EngineerForm form, @PathVariable("id") long id, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            engineerTransaction.updateEngineerAndRelationTransaction(form, id);
            result.setMsg("done");
            result.setStatus(true);
        } catch (EngineerException enfe) {
            logger.error("updateEngineer EngineerNotFoundException: " + enfe.getMessage());
            result.setMsg(enfe.getMessage());
            result.setStatus(false);
        } catch (ParseException pe) {
            logger.error("updateEngineer ParseException: " + pe.getMessage());
            result.setMsg("案件期間フォーマットが正しくない");
            result.setStatus(false);
        } catch (Exception e) {
            logger.error("updateEngineer: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = { "/engineer/list" }, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getEngineers( @Valid @RequestBody EngineerFilterForm form, BindingResult bindingResult) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<EngineerListItemDTO> engineers = engineerService.filter(form, now);
            result.setList(engineers);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getPartners: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/engineer/info/{id}" , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getEngineer(@PathVariable("id") long id) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<Engineer.Builder> engineers = engineerService.getById(id);
            result.setList(engineers);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getEngineer: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = { "/engineer/partnerList" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getPartners() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<BusinessPartner> partners = partnerService.getAll();
            Collections.sort(partners, new BusinessPartner.PartnerComparator());
            List<PartnerDTO> partnerDTOS = new ArrayList<>();
            for(BusinessPartner partner : partners) {
                partnerDTOS.add(new PartnerDTO(partner));
            }
            result.setList(partnerDTOS);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getPartners: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }
    
    @RequestMapping(value = { "/engineer/partnerNotGood/list/{id}" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getPartnersNotGood(@PathVariable("id") long id) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<RelationshipEngineerPartner> relationshipEngineerPartner = engineerService.getRelationshipEngineerPartner(id);
            result.setList(relationshipEngineerPartner);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getPartners: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }
}
