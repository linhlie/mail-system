package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.exception.PartnerCodeException;
import io.owslab.mailreceiver.form.DomainAvoidRegisterForm;
import io.owslab.mailreceiver.form.PartnerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.BusinessPartnerGroup;
import io.owslab.mailreceiver.model.DomainUnregister;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
import io.owslab.mailreceiver.service.expansion.DomainService;

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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by khanhlvb on 7/25/18.
 */
@Controller
@RequestMapping("/expansion/")
public class BusinessPartnerRegistController {
    private static final Logger logger = LoggerFactory.getLogger(BusinessPartnerRegistController.class);

    @Autowired
    private BusinessPartnerService partnerService;
    
    @Autowired
    private DomainService domainService;

    @RequestMapping(value = { "/businessPartnerRegist" }, method = RequestMethod.GET)
    public String getBusinessPartnerRegist(Model model, HttpServletRequest request) {
        return "expansion/businessPartnerRegist";
    }

    @RequestMapping(value = { "/businessPartner/list" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getPartners() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<BusinessPartner> partners = partnerService.getAll();
            Collections.sort(partners, new BusinessPartnerComparator());
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

    @RequestMapping(value = "/businessPartner/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addPartner(
            @Valid @RequestBody PartnerForm form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            partnerService.add(form);
            domainService.deleteDomainByDomain(form.getBuilder().getDomain1(), form.getBuilder().getDomain2(), form.getBuilder().getDomain3());
            result.setMsg("done");
            result.setStatus(true);
        } catch (PartnerCodeException dpce) {
            result.setMsg(dpce.getMessage());
            result.setStatus(false);
        } catch (Exception e) {
            logger.error("addPartner: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/businessPartner/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updatePartner(
            @Valid @RequestBody PartnerForm form, @PathVariable("id") long id, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            partnerService.update(form, id);
            domainService.deleteDomainByDomain(form.getBuilder().getDomain1(), form.getBuilder().getDomain2(), form.getBuilder().getDomain3());
            result.setMsg("done");
            result.setStatus(true);
        } catch (PartnerCodeException dpce) {
            result.setMsg(dpce.getMessage());
            result.setStatus(false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("updatePartner: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/businessPartner/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        try {
            partnerService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = { "/businessPartner/group/list/{id}" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getGroupPartners(@PathVariable("id") long id) {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<BusinessPartnerGroup> partners = partnerService.findByPartner(id);
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
    
    @RequestMapping(value = { "/domain/list" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDomainUnregister() {AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<DomainUnregister> listDomain = domainService.getDomainsByStatus(DomainUnregister.Status.ALLOW_REGISTER);
            result.setList(listDomain);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getDomains: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }
    
    @RequestMapping(value = "/domain/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteDomain(@PathVariable("id") long id) {
        try {
            domainService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @RequestMapping(value = "/domain/avoidRegister/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> avoidRegister(@PathVariable("id") long id) {
        try {
            domainService.changeFromAllowToAvoid(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @RequestMapping(value = { "/domainAvoidRegister" }, method = RequestMethod.GET)
    public String getAvoidRegisterDomain(Model model, HttpServletRequest request) {
        return "expansion/domainAvoidRegister";
    }
    
    @RequestMapping(value = { "/domainAvoidRegister/list" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDomainAvoidRegister() {AjaxResponseBody result = new AjaxResponseBody();
        try {
        	System.out.println("getDomainAvoidRegister");
            List<DomainUnregister> listDomain = domainService.getDomainsByStatus(DomainUnregister.Status.AVOID_REGISTER);
            result.setList(listDomain);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getDomains: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }
    
    @RequestMapping(value = "/domainAvoidRegister/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveDomainAvoidRegister(
            @Valid @RequestBody DomainAvoidRegisterForm form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            domainService.saveDomainAvoidRegister(form);
            result.setMsg("done");
            result.setStatus(true);
        }  catch (Exception e) {
            e.printStackTrace();
            logger.error("updateDomainAvoidRegister: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    public class BusinessPartnerComparator implements Comparator<BusinessPartner> {
        public int compare(BusinessPartner o1, BusinessPartner o2) {
            if(o1.isOurCompany() == o2.isOurCompany()) {
                int value1 = o1.getKanaName().compareTo(o2.getKanaName());
                if (value1 == 0) {
                    int value2 = o1.getName().compareTo(o2.getName());
                    return value2;
                }
                return value1;
            } else if (o1.isOurCompany() && !o2.isOurCompany()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
