package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.DetailMailDTO;
import io.owslab.mailreceiver.form.MatchingConditionForm;
import io.owslab.mailreceiver.form.SendMailForm;
import io.owslab.mailreceiver.model.MatchingCondition;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.response.MatchingResponeBody;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.mail.SendMailService;
import io.owslab.mailreceiver.service.matching.MatchingConditionService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.utils.FinalMatchingResult;
import io.owslab.mailreceiver.utils.MatchingResult;
import io.owslab.mailreceiver.utils.MediaTypeUtils;
import io.owslab.mailreceiver.utils.SelectOption;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by khanhlvb on 3/5/18.
 */
@Controller
@RequestMapping("/user/")
public class MatchingSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(MatchingSettingsController.class);
    @Autowired
    private List<SelectOption> combineOptions;

    @Autowired
    private List<SelectOption> conditionOptions;

    @Autowired
    private List<SelectOption> mailItemOptions;

    @Autowired
    private List<SelectOption> matchingItemOptions;

    @Autowired
    private MatchingConditionService matchingConditionService;

    @Autowired
    private MailBoxService mailBoxService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @RequestMapping(value = "/matchingSettings", method = RequestMethod.GET)
    public String getMatchingSettings(Model model) {
        model.addAttribute("combineOptions", combineOptions);
        model.addAttribute("conditionOptions", conditionOptions);
        model.addAttribute("mailItemOptions", mailItemOptions);
        model.addAttribute("matchingItemOptions", matchingItemOptions);
        return "user/matching/settings";
    }

    @RequestMapping(value="/matchingSettings/source", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getSourceInJSON (){
        AjaxResponseBody result = new AjaxResponseBody();

        result.setMsg("done");
        result.setStatus(true);
        result.setList(matchingConditionService.getSourceConditionList());
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value="/matchingSettings/destination", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getDestinationInJSON (){
        AjaxResponseBody result = new AjaxResponseBody();

        result.setMsg("done");
        result.setStatus(true);
        result.setList(matchingConditionService.getDestinationConditionList());
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value="/matchingSettings/matching", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getMatchingInJSON (){
        AjaxResponseBody result = new AjaxResponseBody();

        result.setMsg("done");
        result.setStatus(true);
        result.setList(matchingConditionService.getMatchingConditionList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/matchingSettings/saveSource")
    @ResponseBody
    public ResponseEntity<?> saveSourceConditionList(
            Model model,
            @Valid @RequestBody MatchingConditionForm matchingConditionForm, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<MatchingCondition> sourceConditionList = matchingConditionForm.getSourceConditionList();
            matchingConditionService.saveList(sourceConditionList, MatchingCondition.Type.SOURCE);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/matchingSettings/saveDestination")
    @ResponseBody
    public ResponseEntity<?> saveDestinationConditionList(
            Model model,
            @Valid @RequestBody MatchingConditionForm matchingConditionForm, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<MatchingCondition> destinationConditionList = matchingConditionForm.getDestinationConditionList();
            matchingConditionService.saveList(destinationConditionList, MatchingCondition.Type.DESTINATION);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/matchingSettings/submitForm")
    @ResponseBody
    public ResponseEntity<?> submitForm(
            Model model,
            @Valid @RequestBody MatchingConditionForm matchingConditionForm, BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        MatchingResponeBody result = new MatchingResponeBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            FinalMatchingResult finalMatchingResult = matchingConditionService.matching(matchingConditionForm);
            result.setMsg("done");
            result.setStatus(true);
            result.setList(finalMatchingResult.getMatchingResultList());
            result.setMailList(finalMatchingResult.getMailList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/matchingResult", method = RequestMethod.GET)
    public String getMatchingResult(Model model) {
        return "user/matching/result";
    }

    @RequestMapping(value="/matchingResult/email", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getEmailInJSON (@RequestParam(value = "messageId", required = true) String messageId){
        AjaxResponseBody result = new AjaxResponseBody();
        List<DetailMailDTO> mailDetail = mailBoxService.getMailDetail(messageId);
        result.setMsg("done");
        result.setStatus(true);
        result.setList(mailDetail);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value="/matchingResult/editEmail", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getEditEmailInJSON (@RequestParam(value = "messageId") String messageId,
                                          @RequestParam(value = "range", required = false) String range,
                                          @RequestParam(value = "replaceType", required = false) int replaceType){
        AjaxResponseBody result = new AjaxResponseBody();
        List<DetailMailDTO> mailDetail = mailBoxService.getMailDetailWithReplacedRange(messageId, range, replaceType);
        result.setMsg("done");
        result.setStatus(true);
        result.setList(mailDetail);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/download")
    public void downloadFile3(HttpServletResponse response,
                              @RequestParam(value = "fileName") String fileName, @RequestParam(value = "path") String path) throws IOException {

        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, fileName);
//        System.out.println("fileName: " + path + "/" + fileName);
//        System.out.println("mediaType: " + mediaType);

        File file = new File(path + "/" + fileName);

        // Content-Type
        // application/pdf
        response.setContentType(mediaType.getType());

        // Content-Disposition
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());

        // Content-Length
        response.setContentLength((int) file.length());

        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        outStream.flush();
        inStream.close();
    }

    @PostMapping("/sendRecommendationMail")
    @ResponseBody
    public ResponseEntity<?> sendRecommendationMail(
            Model model,
            @Valid @RequestBody SendMailForm sendMailForm, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            sendMailService.sendMail(sendMailForm);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("sendRecommendationMail: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value="/matchingResult/envSettings", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getEnvSettingsInJSON (){
        AjaxResponseBody result = new AjaxResponseBody();
        JSONObject obj = new JSONObject();
        obj.put("debug_on", enviromentSettingService.getDebugOn());
        obj.put("debug_receive_mail_address", enviromentSettingService.getDebugReceiveMailAddress());
        result.setMsg(obj.toString());
        result.setStatus(true);
        return ResponseEntity.ok(result);
    }
}