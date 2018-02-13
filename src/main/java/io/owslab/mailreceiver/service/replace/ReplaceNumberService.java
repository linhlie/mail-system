package io.owslab.mailreceiver.service.replace;

import io.owslab.mailreceiver.dao.ReplaceNumberDAO;
import io.owslab.mailreceiver.model.ReplaceNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 2/13/18.
 */
@Service
public class ReplaceNumberService {

    @Autowired
    private ReplaceNumberDAO replaceNumberDAO;

    public List<ReplaceNumber> getList(){
        return (List<ReplaceNumber>) replaceNumberDAO.findAll();
    }
}


