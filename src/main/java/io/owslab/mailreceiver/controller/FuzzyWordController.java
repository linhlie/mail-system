package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.model.FuzzyWord;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.service.word.FuzzyWordService;
import io.owslab.mailreceiver.service.word.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Created by khanhlvb on 2/8/18.
 */
@Controller
public class FuzzyWordController {
    @Autowired
    private WordService wordService;

    @Autowired
    private FuzzyWordService fuzzyWordService;

    @RequestMapping(value = "/fuzzyWord", method = RequestMethod.GET)
    public String getFuzzyWord(@RequestParam(value = "search", required = false) String search, Model model) {
        int totalFuzzyWord = 0;
        if(search != null && search.length() > 0){
            model.addAttribute("search", search);
            Word word = wordService.findOne(search);
            if(word != null){
                Set<FuzzyWord> originalWords = word.getOriginalWords();
                Set<FuzzyWord> associatedWords = word.getAssociatedWords();
                model.addAttribute("word", word);
                model.addAttribute("originalList", originalWords);
                model.addAttribute("associatedToList", associatedWords);
                totalFuzzyWord = originalWords.size() + associatedWords.size();
            }
        }
        model.addAttribute("totalFuzzyWord", totalFuzzyWord);
        return "fuzzyword/list";
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
        model.addAttribute("original", original);
        model.addAttribute("api", "/addFuzzyWord");
        return "fuzzyword/form";
    }
}
