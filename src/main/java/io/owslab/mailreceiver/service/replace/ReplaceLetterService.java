package io.owslab.mailreceiver.service.replace;

import io.owslab.mailreceiver.dao.ReplaceLetterDAO;
import io.owslab.mailreceiver.model.ReplaceLetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 2/22/18.
 */
@Service
public class ReplaceLetterService {
    @Autowired
    private ReplaceLetterDAO replaceLetterDAO;

    public List<ReplaceLetter> getList(){
        return (List<ReplaceLetter>) replaceLetterDAO.findAll();
    }

    public void saveList(List<ReplaceLetter> replaceLetters){
        //TODO: Must be transaction
        for(ReplaceLetter replaceLetter : replaceLetters){
            ReplaceLetter existReplaceLetter = findOne(replaceLetter.getLetter(), replaceLetter.getPosition());
            if(existReplaceLetter != null){
                if(replaceLetter.getRemove() == 1){
                    replaceLetterDAO.delete(existReplaceLetter.getId());
                }
            } else {
                replaceLetterDAO.save(replaceLetter);
            }
        }
    }

    public ReplaceLetter findOne(String letter, int position){
        List<ReplaceLetter> replaceLetters = replaceLetterDAO.findByLetterAndPosition(letter, position);
        return replaceLetters.size() > 0 ? replaceLetters.get(0) : null;
    }
}
