package io.owslab.mailreceiver.service.errror;

import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import io.owslab.mailreceiver.service.BeanUtil;
import io.owslab.mailreceiver.service.mail.EmailAccountSettingService;
import io.owslab.mailreceiver.service.mail.SendMailService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;

import java.util.Date;

/**
 * Created by khanhlvb on 8/1/18.
 */
public class ReportErrorService {

    private static String currentError;
    private static Date lastReportSentAt;
    private static final long LEAST_TIME_BETWEEN_TO_REPORT_IN_MINUTE = 30L;

    //Services
    private static EnviromentSettingService ess;
    private static MailAccountsService mas;
    private static EmailAccountSettingService eass;
    private static SendMailService sms;

    private static String administratorMailAddress;
    private static String ccAdministratorMailAddress;
    private static String sendFrom;
    private static String sendUserName;
    private static String sendPassword;
    private static String sendHost;
    private static int sendPort;

    public static String getCurrentError() {
        return currentError;
    }

    public static void init() {
        ess = BeanUtil.getBean(EnviromentSettingService.class);
        mas = BeanUtil.getBean(MailAccountsService.class);
        eass = BeanUtil.getBean(EmailAccountSettingService.class);
        sms = BeanUtil.getBean(SendMailService.class);
        updateAdministratorMailAddress();
        updateSendAccountInfo();
    }

    public static void updateSendAccountInfo() {
        try {
            EmailAccount account = mas.findOneAlertSend();
            EmailAccountSetting accountSetting = null;
            if(account != null) {
                accountSetting = eass.findOneSend(account.getId());
            }
            if(accountSetting != null) {
                sendFrom = account.getAccount();
                sendUserName = accountSetting.getUserName() != null && accountSetting.getUserName().length() > 0 ? accountSetting.getUserName() : account.getAccount();
                sendPassword = accountSetting.getPassword();
                sendHost = accountSetting.getMailServerAddress();
                sendPort = accountSetting.getMailServerPort();
            } else {
                sendFrom = null;
                sendUserName = null;
                sendPassword = null;
                sendHost = null;
                sendPort = 0;
            }
        } catch (Exception e) {

        }
    }

    public static void updateAdministratorMailAddress() {
        try {
            administratorMailAddress = ess.getAdministratorMailAddress();
            ccAdministratorMailAddress = ess.getCcAdministratorMailAddress();
        } catch (Exception e) {

        }
    }

    public static void clear() {
        currentError = null;
    }

    public static boolean hasSystemError() {
        return currentError != null;
    }

    public static void setCurrentError(String error) {
        currentError = error;
    }

    public static void sendReportError(String error) {
        sendReportError(error, true);
    }

    public static void sendReportError(String error, boolean notice) {
        if(notice == true) {
            setCurrentError(error);
        }
        if(shouldSendReportToAdmin()){
            report(error);
        }
    }

    public static void report(String error) {
        if(sendUserName!=null){
            lastReportSentAt = new Date();
            try {
                ReportErrorParams reportErrorParams = new ReportErrorParams.Builder()
                        .setFrom(sendFrom)
                        .setTo(administratorMailAddress)
                        .setCc(ccAdministratorMailAddress)
                        .setContent(error)
                        .setUserName(sendUserName)
                        .setPassword(sendPassword)
                        .setHost(sendHost)
                        .setPort(sendPort)
                        .build();
                sms.sendReportMail(reportErrorParams);
            } catch (Exception e) {
                System.err.println("Can't send report");
            }
        }
    }

    private static boolean shouldSendReportToAdmin(){
        if(lastReportSentAt == null) return true;
        return (System.currentTimeMillis() - lastReportSentAt.getTime()) >= LEAST_TIME_BETWEEN_TO_REPORT_IN_MINUTE  * 60 * 1000;
    }

    public static class ReportErrorParams {
        private String from;
        private String to;
        private String cc;
        private String userName;
        private String password;
        private String host;
        private int port;
        private String content;

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public String getCc() {
            return cc;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getContent() {
            return content;
        }

        //Builder Class
        public static class Builder{
            private String from;
            private String to;
            private String cc;
            private String userName;
            private String password;
            private String host;
            private int port;
            private String content;

            public Builder setFrom(String from) {
                this.from = from;
                return this;
            }

            public Builder setTo(String to) {
                this.to = to;
                return this;
            }

            public Builder setCc(String cc) {
                this.cc = cc;
                return this;
            }

            public Builder setUserName(String userName) {
                this.userName = userName;
                return this;
            }

            public Builder setPassword(String password) {
                this.password = password;
                return this;
            }

            public Builder setHost(String host) {
                this.host = host;
                return this;
            }

            public Builder setPort(int port) {
                this.port = port;
                return this;
            }

            public Builder setContent(String content) {
                this.content = content;
                return this;
            }

            public ReportErrorParams build(){
                //Here we create the actual bank account object, which is always in a fully initialised state when it's returned.
                ReportErrorParams reportErrorParams = new ReportErrorParams();  //Since the builder is in the BankAccount class, we can invoke its private constructor.
                reportErrorParams.from = this.from;
                reportErrorParams.to = this.to;
                reportErrorParams.cc = this.cc;
                reportErrorParams.userName = this.userName;
                reportErrorParams.password = this.password;
                reportErrorParams.host = this.host;
                reportErrorParams.port = this.port;
                reportErrorParams.content = this.content;
                return reportErrorParams;
            }
        }
    }
}
