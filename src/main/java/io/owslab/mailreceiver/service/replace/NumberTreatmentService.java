package io.owslab.mailreceiver.service.replace;

import io.owslab.mailreceiver.dao.NumberTreatmentDAO;
import io.owslab.mailreceiver.form.NumberTreatmentForm;
import io.owslab.mailreceiver.model.NumberTreatment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 2/23/18.
 */
@Service
@CacheConfig(cacheNames = "short_term_data")
public class NumberTreatmentService {

    @Autowired
    private NumberTreatmentDAO numberTreatmentDAO;

    @Cacheable(key="\"NumberTreatmentService:getFirst\"")
    public NumberTreatment getFirst(){
        List<NumberTreatment> numberTreatments = (List<NumberTreatment>) numberTreatmentDAO.findAll();
        return numberTreatments.size() > 0 ? numberTreatments.get(0) : null;
    }

    @CacheEvict(allEntries = true)
    public void saveForm(NumberTreatmentForm form){
        NumberTreatment numberTreatment = new NumberTreatment(form);
        NumberTreatment existNumberTreatment = getFirst();
        if(existNumberTreatment != null){
            numberTreatment.setId(existNumberTreatment.getId());
        }
        numberTreatmentDAO.save(numberTreatment);
    }

    public List<String> getNumberSetting(){
        List<String> result = new ArrayList<>();
        NumberTreatment numberTreatment = getFirst();
        if(numberTreatment != null){
            String settingNumberName = numberTreatment.getName();
            if(settingNumberName == null || settingNumberName.equalsIgnoreCase("")){
                result.add("");
            }else{
                result.add(settingNumberName);
            }

            String settingNumberUpRateName = numberTreatment.getUpperLimitName();
            if(settingNumberUpRateName == null || settingNumberUpRateName.equalsIgnoreCase("")){
                result.add("");
            }else{
                result.add(settingNumberUpRateName);
            }

            String settingNumberDownRateName = numberTreatment.getLowerLimitName();
            if(settingNumberDownRateName == null || settingNumberDownRateName.equalsIgnoreCase("")){
                result.add("");
            }else{
                result.add(settingNumberDownRateName);
            }
        }else{
            result.add("");
            result.add("");
            result.add("");
        }
        return result;
    }

    @CacheEvict(allEntries = true)
    public void deleteAll(){
        numberTreatmentDAO.deleteAll();
    }
}
