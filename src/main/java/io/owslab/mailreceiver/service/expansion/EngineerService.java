package io.owslab.mailreceiver.service.expansion;

import io.owslab.mailreceiver.dao.EngineerDAO;
import io.owslab.mailreceiver.dto.EngineerListItemDTO;
import io.owslab.mailreceiver.exception.EngineerNotFoundException;
import io.owslab.mailreceiver.exception.PartnerNotFoundException;
import io.owslab.mailreceiver.form.EngineerFilterForm;
import io.owslab.mailreceiver.form.EngineerForm;
import io.owslab.mailreceiver.model.BusinessPartner;
import io.owslab.mailreceiver.model.Engineer;
import io.owslab.mailreceiver.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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

    public void delete(long id) {
        engineerDAO.delete(id);
    }

    public void add(EngineerForm form) throws PartnerNotFoundException, ParseException {
        BusinessPartner existPartner = partnerService.findOne(form.getPartnerId());
        if (existPartner == null) throw new PartnerNotFoundException("取引先が存在しません。");
        Engineer engineer = form.build();
        engineerDAO.save(engineer);
    }

    public void update(EngineerForm form, long id) throws EngineerNotFoundException, PartnerNotFoundException, ParseException {
        Engineer existEngineer = engineerDAO.findOne(id);
        if (existEngineer == null) throw new EngineerNotFoundException("技術者が存在しません");
        BusinessPartner existPartner = partnerService.findOne(form.getPartnerId());
        if (existPartner == null) throw new PartnerNotFoundException("取引先が存在しません。");
        Engineer engineer = form.build();
        engineer.setId(id);
        engineerDAO.save(engineer);
    }

    public List<EngineerListItemDTO> filter(EngineerFilterForm form, Timestamp now) {
        switch (form.getFilterType()) {
            case EngineerFilterForm.FilterType.ACTIVE:
                return getActive(now);
            case EngineerFilterForm.FilterType.INACTIVE:
                return getInactive(now);
            case EngineerFilterForm.FilterType.LAST_MONTH:
                return getByLastMonth(form, now);
            case EngineerFilterForm.FilterType.ALL:
            default:
                return getAll(now);
        }
    }

    public List<EngineerListItemDTO> getActive(Timestamp now) {
        List<EngineerListItemDTO> listItemDTOS = getAll(now);
        List<EngineerListItemDTO> result = new ArrayList<>();
        for (EngineerListItemDTO item : listItemDTOS) {
            if (item.isActive()) {
                result.add(item);
            }
        }
        return result;
    }

    public List<EngineerListItemDTO> getInactive(Timestamp now) {
        List<EngineerListItemDTO> listItemDTOS = getAll(now);
        List<EngineerListItemDTO> result = new ArrayList<>();
        for (EngineerListItemDTO item : listItemDTOS) {
            if (!item.isActive()) {
                result.add(item);
            }
        }
        return result;
    }

    public List<EngineerListItemDTO> getAll(Timestamp now) {
        List<Engineer> engineers = (List<Engineer>) engineerDAO.findAll();
        return build(engineers, now);
    }

    public List<EngineerListItemDTO> getByLastMonth(EngineerFilterForm form, Timestamp now) {
        Date startDate = new Date(form.getFilterDate());
        startDate = Utils.atStartOfDay(startDate);
        Date endDate = Utils.addMonthsToDate(startDate, 1);
        endDate = Utils.addDayToDate(endDate, -1);
        endDate = Utils.atEndOfDay(endDate);
        List<Engineer> engineers = engineerDAO.findByProjectPeriodEndBetween(startDate.getTime(), endDate.getTime());
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

    public List<EngineerForm> getById(long id) {
        List<EngineerForm> result = new ArrayList<>();
        Engineer engineer = engineerDAO.findOne(id);
        if(engineer != null) {
            result.add(new EngineerForm(engineer));
        }
        return result;
    }

    public void autoExtend() {
        List<Engineer> autoExtends = engineerDAO.findByAutoExtend(true);
        long current = System.currentTimeMillis();
        for(Engineer engineer: autoExtends) {
            long projectPeriodEnd = engineer.getProjectPeriodEnd();
            if(projectPeriodEnd < current) {
                Date startDate = new Date(projectPeriodEnd);
                startDate = Utils.addDayToDate(startDate, 1);
                startDate = Utils.atStartOfDay(startDate);
                Date endDate = Utils.addMonthsToDate(startDate, engineer.getExtendMonth());
                endDate = Utils.addDayToDate(endDate, -1);
                endDate = Utils.atEndOfDay(endDate);
                engineer.setProjectPeriodStart(startDate.getTime());
                engineer.setProjectPeriodEnd(endDate.getTime());
                engineerDAO.save(engineer);
            }
        }
    }
}
