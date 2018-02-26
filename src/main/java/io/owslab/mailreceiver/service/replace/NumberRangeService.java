package io.owslab.mailreceiver.service.replace;

import io.owslab.mailreceiver.dao.NumberRangeDAO;
import io.owslab.mailreceiver.model.NumberRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 2/23/18.
 */
@Service
public class NumberRangeService {

    @Autowired
    private NumberRangeDAO numberRangeDAO;

    public List<NumberRange> getList(){
        return (List<NumberRange>) numberRangeDAO.findAll();
    }
}
