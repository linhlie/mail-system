package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.FuzzyWordDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by khanhlvb on 2/7/18.
 */

@Service
public class FuzzyWordService {
    @Autowired
    private FuzzyWordDAO fuzzyWordDAO;

    public void delete(long id){
        fuzzyWordDAO.delete(id);
    }
}
