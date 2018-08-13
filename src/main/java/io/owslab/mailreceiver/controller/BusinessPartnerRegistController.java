package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.exception.PartnerCodeException;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.service.expansion.BusinessPartnerService;
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
            @Valid @RequestBody BusinessPartner.Builder form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            partnerService.add(form);
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
            @Valid @RequestBody BusinessPartner.Builder form, @PathVariable("id") long id, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {

            partnerService.update(form, id);
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
}
