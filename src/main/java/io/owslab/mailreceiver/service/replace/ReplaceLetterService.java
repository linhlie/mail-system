package io.owslab.mailreceiver.service.replace;

import io.owslab.mailreceiver.dao.ReplaceLetterDAO;
import io.owslab.mailreceiver.model.NumberTreatment;
import io.owslab.mailreceiver.model.ReplaceLetter;
import io.owslab.mailreceiver.types.ReplaceLetterType;
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
    @Autowired
    private NumberTreatmentService numberTreatmentService;

    public List<ReplaceLetter> getList(){
        return replaceLetterDAO.findByHidden(false);
    }

    public List<ReplaceLetter> getSignificantList(Boolean beforeNumber){
        int position = beforeNumber ? ReplaceLetter.Position.BF : ReplaceLetter.Position.AF;
        NumberTreatment numberTreatment = numberTreatmentService.getFirst();
        if(numberTreatment != null && numberTreatment.isEnableReplaceLetter()){
            return replaceLetterDAO.findByReplaceNotAndPosition(ReplaceLetter.Replace.NONE, position);
        }
        return replaceLetterDAO.findByReplaceNotAndPositionAndHidden(ReplaceLetter.Replace.NONE, position, ReplaceLetter.Hidden.TRUE);
    }

    public void saveList(List<ReplaceLetter> replaceLetters){
        //TODO: Must be transaction
        for(ReplaceLetter replaceLetter : replaceLetters){
            //TODO: character can not be '.' ...
            ReplaceLetter existReplaceLetter = findOne(replaceLetter.getLetter(), replaceLetter.getPosition());
            if(existReplaceLetter != null){
                if(!existReplaceLetter.isHidden() && replaceLetter.getRemove() == 1){
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
