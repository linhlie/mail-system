package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.errror.ReportErrorService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Component
public class CheckIpScheduler extends AbstractScheduler{
    private static final Logger logger = LoggerFactory.getLogger(CheckIpScheduler.class);
    private static final long INTERVAL_IN_SECOND = 3600L;
    private static final int DELAY_IN_SECOND = 10;
    private static final String DEFAULT_IP_GLOBAL_VALUE = "";

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    public CheckIpScheduler() {
        super(DELAY_IN_SECOND, INTERVAL_IN_SECOND);
    }

    @Override
    public void doStuff() {
        String currentIp = enviromentSettingService.getIpGlobal();
        String ipGlobal = getGlobalIp();
        if(ipGlobal != null && !ipGlobal.equals(currentIp)){
            enviromentSettingService.set(EnviromentSettingService.IP_GLOBAL_KEY, ipGlobal);
            if(currentIp.equals(DEFAULT_IP_GLOBAL_VALUE)) return;
            ReportErrorService.reportAutoFetchMail("[Warning] : The Ip global address has changed from "+currentIp+" to "+ipGlobal);
        }
    }


    public String getGlobalIp(){
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine(); //you get the IP as a String

            return ip;
        } catch( IOException e ) {
            e.printStackTrace();
        }

        return "";

    }
}
