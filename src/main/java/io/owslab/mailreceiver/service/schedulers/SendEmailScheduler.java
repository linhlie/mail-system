package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.form.SendMailForm;
import io.owslab.mailreceiver.model.SchedulerSendEmail;
import io.owslab.mailreceiver.service.mail.EmailAddressGroupService;
import io.owslab.mailreceiver.service.mail.SendMailService;
import io.owslab.mailreceiver.utils.ConvertDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class SendEmailScheduler extends AbstractScheduler {
    private static final Logger logger = LoggerFactory.getLogger(SendEmailScheduler.class);
    private static final long INTERVAL_IN_SECOND = 60L;
    private static final int DELAY_IN_SECOND = 0;

    @Autowired
    EmailAddressGroupService emailAddressGroupService;

    @Autowired
    SendMailService sendMailService;

    public SendEmailScheduler() {
        super(DELAY_IN_SECOND, INTERVAL_IN_SECOND);
    }

    @Override
    public void doStuff() throws Exception {
        startScheduler();
    }

    private void startScheduler() throws Exception {
        List<SchedulerSendEmail> schedulerSendEmails = emailAddressGroupService.getSchedulerByStatus();
        for(SchedulerSendEmail scheduler : schedulerSendEmails){
            if (needSendEMail(scheduler)){
                logger.info("Need send email: "+ scheduler.getSubject() +"  "+scheduler.getId());
                scheduler.setStatus(SchedulerSendEmail.Status.SENDING);
                emailAddressGroupService.changeStatusScheduler(scheduler);
                List<Long> listFileId = emailAddressGroupService.getListFileUpload(scheduler.getId());
                SendMailForm form  = new SendMailForm(scheduler, listFileId);
                sendMailService.sendMailScheduler(form, scheduler.getAccountId());
                if(scheduler.getTypeSendEmail() == SchedulerSendEmail.Type.SEND_BY_HOUR){
                    scheduler.setStatus(SchedulerSendEmail.Status.INACTIVE);
                }else{
                    scheduler.setStatus(SchedulerSendEmail.Status.ACTIVE);
                }
                scheduler.setSentAt(new Date());
                emailAddressGroupService.changeStatusScheduler(scheduler);
            }
        }
    }

    private boolean needSendEMail(SchedulerSendEmail scheduler) throws Exception {
        switch (scheduler.getTypeSendEmail()){
            case 1:
                return checkTimeWithSendByHour(scheduler);
            case 2:
                return checkTimeWithSendByDay(scheduler);
            case 3:
                return checkTimeWithSendByMonth(scheduler);
        }
        return false;
    }

    private boolean checkTimeWithSendByHour(SchedulerSendEmail scheduler) throws Exception {
        Date timeToSendEmail = ConvertDate.convertDateScheduler(scheduler.getDateSendEmail() + " " + scheduler.getHourSendEmail());
        Date lastSendEmail = scheduler.getSentAt();
        return checkTime(timeToSendEmail, lastSendEmail);
    }

    private boolean checkTimeWithSendByDay(SchedulerSendEmail scheduler) throws Exception {
        String date = scheduler.getDateSendEmail();
        String dateArr[] =  date.split(",");
        int dayOfWeek = getDayOfWeek();
        for(int i=0;i<dateArr.length;i++){
            if(dateArr[i].equals(dayOfWeek+"")){
                Date timeToSendEmail = ConvertDate.convertDateScheduler(ConvertDate.getDateScheduler(new Date()) + " " + scheduler.getHourSendEmail());
                Date lastSendEmail = scheduler.getSentAt();
                return checkTime(timeToSendEmail, lastSendEmail);
            }
        }
        return false;
    }

    private boolean checkTimeWithSendByMonth(SchedulerSendEmail scheduler) throws Exception {
        String date = scheduler.getDateSendEmail();
        int dayOfMonth = getDayOfMonth();
        switch (date){
            case "the-first-day":
                if(dayOfMonth==1){
                    Date timeToSendEmail = ConvertDate.convertDateScheduler(ConvertDate.getDateScheduler(new Date()) + " " + scheduler.getHourSendEmail());
                    Date lastSendEmail = scheduler.getSentAt();
                    return checkTime(timeToSendEmail, lastSendEmail);
                }
                break;
            case "the-last-day":
                if(dayOfMonth == getLastDayOfMonth()){
                    Date timeToSendEmail = ConvertDate.convertDateScheduler(ConvertDate.getDateScheduler(new Date()) + " " + scheduler.getHourSendEmail());
                    Date lastSendEmail = scheduler.getSentAt();
                    return checkTime(timeToSendEmail, lastSendEmail);
                }
                break;
            default:
                if(date.equals(dayOfMonth+"")){
                    Date timeToSendEmail = ConvertDate.convertDateScheduler(ConvertDate.getDateScheduler(new Date()) + " " + scheduler.getHourSendEmail());
                    Date lastSendEmail = scheduler.getSentAt();
                    return checkTime(timeToSendEmail, lastSendEmail);
                }
        }
        return false;
    }

    private boolean checkTime(Date sendEmailScheduler, Date lastSend) throws Exception {
        try {
            Date now = new Date();
            long diffSendEmailScheduler = ConvertDate.compareMinuteOfDate(now, sendEmailScheduler);
            long diffLastSend = ConvertDate.compareMinuteOfDate(now, lastSend);
            if(diffSendEmailScheduler>=0 && diffSendEmailScheduler<5 && diffLastSend>5){
                return true;
            }
            return false;
        }catch (Exception e){
            logger.error("Error convert date");
            return false;
        }
    }

    private int getDayOfWeek(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.get(Calendar.DAY_OF_WEEK);
    }

    private int getDayOfMonth(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.get(Calendar.DAY_OF_MONTH);
    }

    private int getLastDayOfMonth(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}