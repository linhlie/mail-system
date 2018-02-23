package io.owslab.mailreceiver.service.replace;

import io.owslab.mailreceiver.dao.NumberTreatmentDAO;
import io.owslab.mailreceiver.form.NumberTreatmentForm;
import io.owslab.mailreceiver.model.NumberTreatment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 2/23/18.
 */
@Service
public class NumberTreatmentService {

    @Autowired
    private NumberTreatmentDAO numberTreatmentDAO;

    public NumberTreatment getFirst(){
        List<NumberTreatment> numberTreatments = (List<NumberTreatment>) numberTreatmentDAO.findAll();
        return numberTreatments.size() > 0 ? numberTreatments.get(0) : null;
    }

    public void saveForm(NumberTreatmentForm form){
        NumberTreatment numberTreatment = new NumberTreatment(form);
        NumberTreatment existNumberTreatment = getFirst();
        if(existNumberTreatment != null){
            numberTreatment.setId(existNumberTreatment.getId());
        }
        numberTreatmentDAO.save(numberTreatment);
    }
}
