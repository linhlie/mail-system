package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.DetailMailDTO;
import io.owslab.mailreceiver.dto.EmailInboxDTO;
import io.owslab.mailreceiver.dto.FileDTO;
import io.owslab.mailreceiver.dto.InboxDTO;
import io.owslab.mailreceiver.form.InboxForm;
import io.owslab.mailreceiver.form.TrashBoxForm;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.RelativeSentAtEmail;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.response.MailHistoryResponseBody;
import io.owslab.mailreceiver.service.file.AttachmentFileService;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.matching.MatchingConditionService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.utils.PageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by khanhlvb on 1/26/18.
 */
@Controller
public class MailBoxController {
    private static final Logger logger = LoggerFactory.getLogger(MailBoxController.class);
    public static final int PAGE_SIZE = 15;
    public static final int ERROR_PAGE_SIZE = 10;

    @Autowired
    private MailBoxService mailBoxService;

    @Autowired
    private MatchingConditionService conditionService;

    @Autowired
    private MailAccountsService mailAccountsService;

    @Autowired
    private AttachmentFileService fileService;

    @Autowired
    NumberTreatmentService numberTreatmentService;

    @RequestMapping(value = "/admin/mailbox", method = RequestMethod.GET)
    public String getMailBox(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            Model model) {
        page = page - 1;
        List<RelativeSentAtEmail> relativeSentAtEmailList = new ArrayList<RelativeSentAtEmail>();
        PageRequest pageRequest = new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "sentAt");
        Page<Email> pages = search == null || search.length() == 0? mailBoxService.list(pageRequest) : mailBoxService.searchContent(search, pageRequest);
        List<Email> list = pages.getContent();
        int rowsInPage = list.size();
        PageWrapper<Email> pageWrapper = new PageWrapper<Email>(pages, "/admin/mailbox");
        for(int i = 0; i < rowsInPage; i++){
            Email email = list.get(i);
            RelativeSentAtEmail relativeSentAtEmail = new RelativeSentAtEmail(email);
            List<EmailAccount> listAccount = mailAccountsService.findById(email.getAccountId());
            EmailAccount emailAccount = listAccount.size() > 0 ? listAccount.get(0) : null;
            String emailAccountAddress = emailAccount != null ? emailAccount.getAccount() : "Unknown";
            relativeSentAtEmail.setAccount(emailAccountAddress);
            relativeSentAtEmailList.add(relativeSentAtEmail);
        }
        if(search != null && search.length() > 0){
            model.addAttribute("search", search);
        }
        int fromEntry = rowsInPage == 0 ? 0 : page * PAGE_SIZE + 1;
        int toEntry = rowsInPage == 0 ? 0 : fromEntry + rowsInPage - 1;
        model.addAttribute("list", relativeSentAtEmailList);
        model.addAttribute("page", pageWrapper);
        model.addAttribute("fromEntry", fromEntry);
        model.addAttribute("toEntry", toEntry);
        return "admin/mailbox/list";
    }

    @RequestMapping(value = "/admin/mailboxError", method = RequestMethod.GET)
    public String getMailBoxError(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            Model model) {
        page = page - 1;
        List<RelativeSentAtEmail> relativeSentAtEmailList = new ArrayList<RelativeSentAtEmail>();
        PageRequest pageRequest = new PageRequest(page, ERROR_PAGE_SIZE, Sort.Direction.DESC, "sentAt");
        Page<Email> pages = search == null || search.length() == 0? mailBoxService.listError(pageRequest) : mailBoxService.searchError(search, pageRequest);
        List<Email> list = pages.getContent();
        int rowsInPage = list.size();
        PageWrapper<Email> pageWrapper = new PageWrapper<Email>(pages, "/admin/mailboxError");
        for(int i = 0; i < rowsInPage; i++){
            Email email = list.get(i);
            relativeSentAtEmailList.add(new RelativeSentAtEmail(email));
        }
        if(search != null && search.length() > 0){
            model.addAttribute("search", search);
        }
        int fromEntry = rowsInPage == 0 ? 0 : page * ERROR_PAGE_SIZE + 1;
        int toEntry = rowsInPage == 0 ? 0 : fromEntry + rowsInPage - 1;
        model.addAttribute("list", relativeSentAtEmailList);
        model.addAttribute("page", pageWrapper);
        model.addAttribute("fromEntry", fromEntry);
        model.addAttribute("toEntry", toEntry);
        return "admin/mailbox/listError";
    }

    @RequestMapping(value="/admin/retry", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getRetryEmail (@RequestParam(value = "messageId") String messageId){
        AjaxResponseBody result = new AjaxResponseBody();
        mailBoxService.retry(messageId);
        result.setMsg("done");
        result.setStatus(true);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/admin/trashbox", method = RequestMethod.GET)
    public String getTrashBox(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            Model model) {
        page = page - 1;
        List<RelativeSentAtEmail> relativeSentAtEmailList = new ArrayList<RelativeSentAtEmail>();
        PageRequest pageRequest = new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "sentAt");
        Page<Email> pages = search == null || search.length() == 0? mailBoxService.listTrash(pageRequest) : mailBoxService.searchTrash(search, pageRequest);
        List<Email> list = pages.getContent();
        int rowsInPage = list.size();
        PageWrapper<Email> pageWrapper = new PageWrapper<Email>(pages, "/admin/trashbox");
        for(int i = 0; i < rowsInPage; i++){
            Email email = list.get(i);
            RelativeSentAtEmail relativeSentAtEmail = new RelativeSentAtEmail(email);
            List<EmailAccount> listAccount = mailAccountsService.findById(email.getAccountId());
            EmailAccount emailAccount = listAccount.size() > 0 ? listAccount.get(0) : null;
            String emailAccountAddress = emailAccount != null ? emailAccount.getAccount() : "Unknown";
            relativeSentAtEmail.setAccount(emailAccountAddress);
            relativeSentAtEmailList.add(relativeSentAtEmail);
        }
        if(search != null && search.length() > 0){
            model.addAttribute("search", search);
        }
        int fromEntry = rowsInPage == 0 ? 0 : page * PAGE_SIZE + 1;
        int toEntry = rowsInPage == 0 ? 0 : fromEntry + rowsInPage - 1;
        model.addAttribute("list", relativeSentAtEmailList);
        model.addAttribute("page", pageWrapper);
        model.addAttribute("fromEntry", fromEntry);
        model.addAttribute("toEntry", toEntry);
        return "admin/mailbox/trashbox";
    }

    @RequestMapping(value="/admin/trashbox/empty", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> emptyTrashBox (){
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            mailBoxService.emptyTrashBox();
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping(value="/admin/trashbox/delete")
    @ResponseBody
    ResponseEntity<?> deleteFromTrashBox(Model model, @Valid @RequestBody TrashBoxForm trashBoxForm){
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            mailBoxService.deleteFromTrashBox(trashBoxForm.getMsgIds());
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping(value="/admin/trashbox/moveToInbox")
    @ResponseBody
    ResponseEntity<?> moveToInbox(Model model, @Valid @RequestBody TrashBoxForm trashBoxForm){
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            mailBoxService.moveToInbox(trashBoxForm.getMsgIds());
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping(value="/admin/mailbox/deleteFromInbox")
    @ResponseBody
    ResponseEntity<?> deleteFromInBox(Model model, @Valid @RequestBody TrashBoxForm trashBoxForm){
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            mailBoxService.deleteFromInBox(trashBoxForm.getMsgIds());
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping(value="/admin/mailbox/deleteFromErrorbox")
    @ResponseBody
    ResponseEntity<?> deleteFromErrorbox(Model model, @Valid @RequestBody TrashBoxForm trashBoxForm){
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            mailBoxService.deleteFromErrorBox(trashBoxForm.getMsgIds());
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value="/admin/mailbox/email", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getEmailJsonAdmin (
            @RequestParam(value = "messageId", required = true) String messageId
    ){
        AjaxResponseBody result = new AjaxResponseBody();
        List<DetailMailDTO> mailDetail = mailBoxService.getMailDetail(messageId);
        result.setMsg("done");
        result.setStatus(true);
        result.setList(mailDetail);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user/inbox", method = RequestMethod.GET)
    public String showAllEmail(Model model) {
        List<String> numberConditionSetting = numberTreatmentService.getNumberSetting();
        model.addAttribute("ruleNumber",numberConditionSetting.get(0));
        model.addAttribute("ruleNumberDownRate",numberConditionSetting.get(1));
        model.addAttribute("ruleNumberUpRate",numberConditionSetting.get(2));
        return "user/inbox/inbox";
    }

    @PostMapping("/user/inbox/filter")
    @ResponseBody
    public ResponseEntity<?> filterInbox(
            Model model,
            @Valid @RequestBody InboxForm form, BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<InboxDTO> listInboxDTO = conditionService.filterInbox(form);
            result.setMsg("done");
            result.setStatus(true);
            result.setList(listInboxDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/user/mailbox/getFileAttach" , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getFileAttach(@RequestParam(value = "messageId") String messageId) {
        MailHistoryResponseBody result = new MailHistoryResponseBody();
        try {
            List<FileDTO> listFileAttach = fileService.getFileByMessageId(messageId);
            result.setList(listFileAttach);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("detail: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }
}
