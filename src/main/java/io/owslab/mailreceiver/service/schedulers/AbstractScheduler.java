package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.BeanUtil;
import io.owslab.mailreceiver.service.errror.ReportErrorService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.annotation.Resource;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by khanhlvb on 2/9/18.
 */
public abstract class AbstractScheduler {
    private int delay;
    private long interval;
    private TimerTask timerTask;
    private Timer timer;
    @Resource(name = "reportErrorService")
    ReportErrorService reportErrorService;

    public AbstractScheduler(int delay, long interval) {
        this.delay = delay;
        this.interval = interval;
    }

    public void start(){
        this.setTimerTask(new TimerTask() {
            public void run() {
                try {
                    doStuff();
                } catch (Exception e) {
                    reportErrorService.sendReportError(ExceptionUtils.getStackTrace(e));
                }
            }
        });
        this.setTimer(new Timer("Timer"));
        this.timer.scheduleAtFixedRate(this.getTimerTask(), this.getDelay(), this.getInterval() * 1000);
    }

    public abstract void doStuff() throws Exception;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public TimerTask getTimerTask() {
        return timerTask;
    }

    public void setTimerTask(TimerTask timerTask) {
        this.timerTask = timerTask;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
