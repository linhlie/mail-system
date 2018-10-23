package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.form.FuzzyWordForm;
import io.owslab.mailreceiver.model.FuzzyWord;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.response.AjaxResponseBody;
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
    public String getFuzzyWord(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "selected", required = false) String selected,
                               Model model) {
        int totalFuzzyWord = 0;
        List<Word> wordList = wordService.findAll();
        if(search != null && search.length() > 0){
            model.addAttribute("search", search);
        }
        if(selected != null && selected.length() > 0){
            model.addAttribute("selected", selected);
            Word word = wordService.findOne(selected);
            if(word != null){
                Set<FuzzyWord> originalWords = word.getOriginalWords();
                Set<FuzzyWord> associatedWords = word.getAssociatedWords();
                model.addAttribute("word", word);
                model.addAttribute("originalList", originalWords);
                model.addAttribute("associatedToList", associatedWords);
                totalFuzzyWord = originalWords.size() + associatedWords.size();
            }
        }
        List<KeyWordItem> keyWordItems = new ArrayList<>();
        if(search != null && search.length() > 0){
            Word wordSearch = wordService.findOne(search);
            if(wordSearch!=null){
                keyWordItems = fuzzyWordService.searchWord(wordSearch);
            }
        }else{
            for(Word word : wordList) {
                Word keyWord = fuzzyWordService.findKeyWord(word);
                String keyWordStr = keyWord != null ? keyWord.getWord() : "";
                keyWordItems.add(new KeyWordItem(word.getWord(), keyWordStr));
            }
        }
        model.addAttribute("wordList", keyWordItems);
        model.addAttribute("wordListSize", keyWordItems.size());
        model.addAttribute("totalFuzzyWord", totalFuzzyWord);
        return "user/fuzzyword/list";
    }

    @RequestMapping(value = "/fuzzyWord/{id}/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        try {
            fuzzyWordService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
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

    @PostMapping("/addFuzzyWord")
    @ResponseBody
    public ResponseEntity<?> addFuzzyWord(
            Model model,
            @Valid @RequestBody FuzzyWordForm fuzzyWordForm, BindingResult bindingResult) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (bindingResult.hasErrors()) {
            result.setMsg(bindingResult.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        try {
            String originalWordStr = wordService.normalize(fuzzyWordForm.getOriginal());
            String associatedWordStr = wordService.normalize(fuzzyWordForm.getAssociatedWord());
            if(originalWordStr.equals(associatedWordStr)){
                throw new Exception("同じ単語にすることはできません");
            }
            int fuzzyType = fuzzyWordForm.getFuzzyType();
            Word originalWord = wordService.findOne(originalWordStr);
            Word associatedWord = wordService.findOne(associatedWordStr);
            if(originalWord != null && associatedWord != null) {
                if(originalWord.getId() == associatedWord.getId()){
                    throw new Exception("同じ単語にすることはできません");
                }
                FuzzyWord existFuzzyWord = fuzzyWordService.findOne(originalWord, associatedWord);
                if(existFuzzyWord != null){
                    throw new Exception("データは既に存在します");
                }
            } else {
                if(originalWord == null) {
                    originalWord = new Word();
                    originalWord.setWord(originalWordStr);
                    wordService.save(originalWord);
                }
                if(associatedWord == null) {
                    associatedWord = new Word();
                    associatedWord.setWord(associatedWordStr);
                    wordService.save(associatedWord);
                    //TODO: save failed rollback
                }
            }
            FuzzyWord fuzzyWord = new FuzzyWord(originalWord, associatedWord, fuzzyType);
            fuzzyWordService.save(fuzzyWord);
            //TODO: save failed rollback
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
