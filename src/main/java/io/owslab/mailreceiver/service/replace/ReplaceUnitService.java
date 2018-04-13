package io.owslab.mailreceiver.service.replace;

import io.owslab.mailreceiver.dao.ReplaceUnitDAO;
import io.owslab.mailreceiver.model.ReplaceUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "short_term_data")
public class ReplaceUnitService {

    @Autowired
    private ReplaceUnitDAO replaceUnitDAO;

    @Cacheable(key="\"ReplaceUnitService:getList\"")
    public List<ReplaceUnit> getList(){
        return (List<ReplaceUnit>) replaceUnitDAO.findAll();
    }

    public void saveList(List<ReplaceUnit> replaceUnits){
        //TODO: Must be transaction
        for(ReplaceUnit replaceUnit : replaceUnits){
            //TODO: character can not be '.' ...
            ReplaceUnit existReplaceUnit = findOne(replaceUnit.getUnit());
            if(existReplaceUnit != null){
                if(replaceUnit.getRemove() == 1){
                    replaceUnitDAO.delete(existReplaceUnit.getId());
                }
            } else {
                replaceUnitDAO.save(replaceUnit);
            }
        }
    }

    @Cacheable(key="\"ReplaceUnitService:findOne:\"+#unit")
    public ReplaceUnit findOne(String unit){
        List<ReplaceUnit> replaceUnits = replaceUnitDAO.findByUnit(unit);
        return replaceUnits.size() > 0 ? replaceUnits.get(0) : null;
    }
}