package io.owslab.mailreceiver.service.receive;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.ReceiveEmailAccountSettingsDAO;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 1/19/18.
 */
@Service
public class FetchMailsService {
    private static final Logger logger = LoggerFactory.getLogger(FetchMailsService.class);
    @Autowired
    private ReceiveEmailAccountSettingsDAO receiveEmailAccountSettingsDAO;

    @Autowired
    private EmailDAO emailDAO;

    public void start(){
        logger.info("Starting fetch mails");
        List<ReceiveEmailAccountSetting> list = receiveEmailAccountSettingsDAO.findByDisabled(false);
        ReceiveEmailAccountSetting receiveEmailAccountSetting = list.get(0);
        System.out.println(receiveEmailAccountSetting.toString());
        List<Email> listEmail = (List<Email>) emailDAO.findAll();
        Email email =  listEmail.get(0);
        System.out.println(email.toString());
    }
}
