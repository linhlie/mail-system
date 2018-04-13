package io.owslab.mailreceiver.service.replace;

import io.owslab.mailreceiver.dao.ReplaceNumberDAO;
import io.owslab.mailreceiver.model.ReplaceNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 2/13/18.
 */
@Service
@CacheConfig(cacheNames = "short_term_data")
public class ReplaceNumberService {

    @Autowired
    private ReplaceNumberDAO replaceNumberDAO;

    @Cacheable(key="\"ReplaceNumberService:getList\"")
    public List<ReplaceNumber> getList(){
        return (List<ReplaceNumber>) replaceNumberDAO.findAll();
    }

    public void saveList(List<ReplaceNumber> replaceNumbers){
        //TODO: Must be transaction
        for(ReplaceNumber replaceNumber : replaceNumbers){
            //TODO: character can not be '.' ...
            ReplaceNumber existReplaceNumber = findOne(replaceNumber.getCharacter());
            if(existReplaceNumber != null){
                if(replaceNumber.getRemove() == 1){
                    replaceNumberDAO.delete(existReplaceNumber.getId());
                }
            } else {
                replaceNumber.setReplaceValue(replaceNumber.getReplaceValueStrFromRaw());
                replaceNumberDAO.save(replaceNumber);
            }
        }
    }

    @Cacheable(key="\"ReplaceNumberService:findOne:\"+#character")
    public ReplaceNumber findOne(String character){
        List<ReplaceNumber> replaceNumbers = replaceNumberDAO.findByCharacter(character);
        return replaceNumbers.size() > 0 ? replaceNumbers.get(0) : null;
    }
}


