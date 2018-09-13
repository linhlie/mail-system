package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.expansion.DomainService;
import io.owslab.mailreceiver.service.mail.FetchMailsService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateDomainUnregisterScheduler extends AbstractScheduler{
	   private static final Logger logger = LoggerFactory.getLogger(UpdateDomainUnregisterScheduler.class);
	    private static final long CHECK_TIME_TO_UPDATE_DOMAIN_UNREGISTER_INTERVAL_IN_SECEOND = 10L;
	    private static final int UPDATE_DOMAIN_UNREGISTER_INTERVAL_IN_MINUTE = 1;

	    @Autowired
	    private DomainService domainService;
	    
	    @Autowired
	    private EnviromentSettingService enviromentSettingService;

	    private Date lastTimeUpdate;

	    public UpdateDomainUnregisterScheduler() {
	        super(0, CHECK_TIME_TO_UPDATE_DOMAIN_UNREGISTER_INTERVAL_IN_SECEOND);
	    }

	    @Override
	    public void doStuff() {
	        if(isNeedToUpdateDomainUnregister()){
	            startUpdate();
	        }
	    }

	    private boolean isNeedToUpdateDomainUnregister(){
	        Optional<Date> lastTimeUpdateOpt = getLastTimeUpdate();
	        if(lastTimeUpdateOpt.isPresent()){
	            Date now = new Date();
	            Date lastTimeUpdate = lastTimeUpdateOpt.get();
	            int updateMinute = enviromentSettingService.getUpdateDomainTimeInterval();
	            Date nextTimeUpdate = addMinutesToADate(lastTimeUpdate, updateMinute);
	            return nextTimeUpdate.compareTo(now) <= 0;
	        }
	        return true;
	    }

	    private void startUpdate(){
	    	lastTimeUpdate = new Date();
	    	domainService.startUpdateDomainUnregister();
	    }

	    private Optional<Date> getLastTimeUpdate(){
	        //TODO: find last time fetch mail;
	        if(lastTimeUpdate == null){
	            return Optional.empty();
	        }
	        return Optional.of(lastTimeUpdate);
	    }

	    private Date addMinutesToADate(Date date, int minute){
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.add(Calendar.MINUTE, minute);
	        return cal.getTime();
	    }
}
