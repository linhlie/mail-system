package io.owslab.mailreceiver.service.replace;

import io.owslab.mailreceiver.dao.NumberTreatmentDAO;
import io.owslab.mailreceiver.form.NumberTreatmentForm;
import io.owslab.mailreceiver.model.NumberTreatment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
}
