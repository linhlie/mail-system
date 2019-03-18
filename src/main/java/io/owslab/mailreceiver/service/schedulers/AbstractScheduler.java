package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.errror.ReportErrorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * Created by khanhlvb on 2/9/18.
 */
public abstract class AbstractScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractScheduler.class);

    private int delay;
    private long interval;
    private int timeout_s;
    private Timer timer;
    private TimerTask timerTask;
    private ExecutorService THREAD_POOL = Executors.newSingleThreadScheduledExecutor();
    private <T> T timedCall(Callable<T> c, long timeout, TimeUnit timeUnit)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        FutureTask<T> task = new FutureTask<T>(c);
        THREAD_POOL.execute(task);
        return task.get(timeout, timeUnit);
    }

    public AbstractScheduler(int delay, long interval, int timeout_s) {
        this.delay = delay;
        this.interval = interval;
        this.timeout_s = timeout_s;
    }

    public AbstractScheduler(int delay, long interval) {
        this.delay = delay;
        this.interval = interval;
        this.timeout_s = -1;
    }

    public void start(){
        this.setTimerTask(new TimerTask() {
            public void run() {
                try {
                    if(timeout_s > 0) {
                        privateDoStuffTimeout(timeout_s);
                    }else {
                        doStuff();
                    }
                } catch (RuntimeException e){
                    logger.error("[AbstractScheduler] RuntimeException: "+ e.toString());
                    e.printStackTrace();
                    return; // Keep working
                }catch (Throwable e){
                    logger.error("[AbstractScheduler] Throwable: "+ e.toString());
                    e.printStackTrace();
                    ReportErrorService.sendReportError(e.toString());
                    return;
                }
            }
        });
        this.setTimer(new Timer("Timer"));
        this.timer.scheduleAtFixedRate(this.getTimerTask(), this.getDelay(), this.getInterval() * 1000);
    }

    private void privateDoStuffTimeout(int timeout_s) throws Exception {
        try {
            logger.info("Will start job with timeout " + timeout_s + " " + this);
            int returnCode = timedCall(new Callable<Integer>() {
                public Integer call() throws Exception {
                    doStuff();
                    return 1;
                }
            }, timeout_s, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            try {
                // Handle timeout here
                logger.warn("Time limit for job " + this);
                THREAD_POOL.shutdownNow();
                THREAD_POOL = Executors.newSingleThreadScheduledExecutor();
                ReportErrorService.report("Warning : [Service auto restarted] Time limit " + timeout_s + " seconds  for job " + this);
                restartService();
            } catch (Error e2){
                ReportErrorService.report("Critical: [Service auto restart failed] Time limit " + timeout_s + " seconds  for job " + this);
                restartService();
            }

        }
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

    protected void setDoStuffTimeout(int timeout_s) {
        this.timeout_s = timeout_s;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public TimerTask getTimerTask() {
        return timerTask;
    }

    public void setTimerTask(TimerTask timerTask) {
        if(this.timerTask != null){
            this.timerTask.cancel();
        }
        this.timerTask = timerTask;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        if(this.timer !=null){
            this.timer.cancel();
            this.timer.purge();
        }
        this.timer = timer;
    }

    protected void restartService(){
        Thread restartThread = new Thread(() -> {
            try {
                Runtime.getRuntime().exec("sudo sh /opt/mail-service/ServiceFetchMail.sh restart");
            } catch (Exception ignored) {

            }
        });
        restartThread.setDaemon(false);
        restartThread.start();
    }
}
