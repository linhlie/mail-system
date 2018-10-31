package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dto.FuzzyWordDTO;
import io.owslab.mailreceiver.form.EditWordForm;
import io.owslab.mailreceiver.form.FuzzyWordForm;
import io.owslab.mailreceiver.model.FuzzyWord;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.response.AjaxResponseBody;
import io.owslab.mailreceiver.response.FuzzyWordResponseBody;
import io.owslab.mailreceiver.service.word.FuzzyWordService;
import io.owslab.mailreceiver.service.word.WordService;
import io.owslab.mailreceiver.utils.KeyWordItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by khanhlvb on 2/8/18.
 */
@Controller
@RequestMapping("/user/")
public class FuzzyWordController {

    private static final Logger logger = LoggerFactory.getLogger(FuzzyWordController.class);

    @Autowired
    private WordService wordService;

    @Autowired
    private FuzzyWordService fuzzyWordService;

    @RequestMapping(value = "/fuzzyWord", method = RequestMethod.GET)
    public String getFuzzyWord(Model model, HttpServletRequest request) {
        return "user/fuzzyword/list";
    }

    @RequestMapping(value = { "/fuzzyWord/getListWord" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getListWord() {
        AjaxResponseBody result = new AjaxResponseBody();
        try {
            List<Word> listWord = wordService.getListWordByGroup();
            result.setList(listWord);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getListWord: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = { "/fuzzyWord/getExclusion" }, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getExclusion( @Valid @RequestBody String groupWord, BindingResult bindingResult) {
        FuzzyWordResponseBody result = new FuzzyWordResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<Word> listWord = wordService.getListWordinGroup(groupWord);
            List<FuzzyWordDTO> listExclusion = fuzzyWordService.getExclusion(listWord);
            result.setList(listExclusion);
            result.setListWord(listWord);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("getExclusion: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = { "/fuzzyWord/searchWord" }, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> searchWord( @Valid @RequestBody String word, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            List<Word> listWord = wordService.searchWord(word);
            result.setList(listWord);
            result.setMsg("done");
            result.setStatus(true);
        } catch (Exception e) {
            logger.error("searchWord: " + e.getMessage());
            result.setMsg(e.getMessage());
            result.setStatus(false);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/fuzzyWord/deleteFuzzyWord/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteFuzzyWord(@PathVariable("id") long id) {
        try {
            fuzzyWordService.deleteFuzzyWord(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/fuzzyWord/deleteGroupWord/{group}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteGroupWord(@PathVariable("group") String group) {
        try {
            wordService.deleteGroupWord(group);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/fuzzyWord/deleteWordInGroup")
    @ResponseBody
    public ResponseEntity<?> deleteWordInGroup(@Valid @RequestBody EditWordForm form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            System.out.println("Delete Word "+form.getOldWord());
            int words = wordService.deleteWordInGroup(form.getOldWord());
            result.setMsg(words+"");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @RequestMapping(value = "/fuzzyWord/add", method = RequestMethod.GET)
    public String getAddFuzzyWord(@RequestParam(value = "original", required = false) String original, Model model) {
        FuzzyWordForm fuzzyWordForm = new FuzzyWordForm();
        original = original == null ? original : original.toLowerCase();
        fuzzyWordForm.setOriginal(original);
        model.addAttribute("fuzzyWordForm", fuzzyWordForm);
        model.addAttribute("original", original);
        model.addAttribute("api", "/user/addFuzzyWord");
        return "user/fuzzyword/form";
    }

    @PostMapping("/fuzzyWord/editWord")
    @ResponseBody
    public ResponseEntity<?> editWord(@Valid @RequestBody EditWordForm form, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            wordService.editWord(form);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/fuzzyWord/addWordToGroup")
    @ResponseBody
    public ResponseEntity<?> addWordToGroup(@Valid @RequestBody Word word, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            wordService.addWord(word);
            result.setMsg("done");
            result.setStatus(true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
            result.setStatus(false);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/fuzzyWord/addFuzzyWord")
    @ResponseBody
    public ResponseEntity<?> addFuzzyWord(@Valid @RequestBody FuzzyWordDTO fuzzyWordDTO, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            fuzzyWordService.addFuzzyWord(fuzzyWordDTO);
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
