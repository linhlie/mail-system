package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.EngineerDAO;
import io.owslab.mailreceiver.dto.EngineerListItemDTO;
import io.owslab.mailreceiver.exception.EngineerNotFoundException;
import io.owslab.mailreceiver.exception.PartnerNotFoundException;
import io.owslab.mailreceiver.form.EngineerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Engineer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 8/17/18.
 */
@Service
public class EngineerService {

    @Autowired
    private EngineerDAO engineerDAO;

    @Autowired
    private BusinessPartnerService partnerService;

    public void delete(long id){
        engineerDAO.delete(id);
    }

    public void add(EngineerForm form) throws PartnerNotFoundException, ParseException {
        BusinessPartner existPartner = partnerService.findOne(form.getPartnerId());
        if(existPartner != null) throw new PartnerNotFoundException("取引先が存在しません。");
        Engineer engineer = form.build();
        engineerDAO.save(engineer);
    }

    public void update(EngineerForm form, long id) throws EngineerNotFoundException, PartnerNotFoundException, ParseException {
        form.setId(id);
        Engineer existEngineer = engineerDAO.findOne(id);
        if(existEngineer == null) throw new EngineerNotFoundException("技術者が存在しません");
        BusinessPartner existPartner = partnerService.findOne(form.getPartnerId());
        if(existPartner != null) throw new PartnerNotFoundException("取引先が存在しません。");
        Engineer engineer = form.build();
        engineerDAO.save(engineer);
    }

    public List<EngineerListItemDTO> getAll(Timestamp now) {
        List<Engineer> engineers = (List<Engineer>) engineerDAO.findAll();
        return build(engineers, now);
    }

    private List<EngineerListItemDTO> build(List<Engineer> engineers, Timestamp now) {
        List<EngineerListItemDTO> engineerDTOs = new ArrayList<>();
        for(Engineer engineer : engineers){
            BusinessPartner partner = partnerService.findOne(engineer.getPartnerId());
            if(partner != null) {
                engineerDTOs.add(new EngineerListItemDTO(engineer, partner.getName(), now));
            }
        }
        return engineerDTOs;
    }
}
