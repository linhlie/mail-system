package io.owslab.mailreceiver.response;

import io.owslab.mailreceiver.dto.AccountDTO;
import io.owslab.mailreceiver.dto.BulletinBoardDTO;
import io.owslab.mailreceiver.dto.DetailMailDTO;
import io.owslab.mailreceiver.model.BulletinBoard;
import io.owslab.mailreceiver.model.EmailAccount;

import java.util.List;

/**
 * Created by khanhlvb on 7/9/18.
 */
public class DashboardResponseBody extends AjaxResponseBody {
    private boolean hasSystemError;
    private String latestReceive;
    private List<String> clickCount;
    private List<String> clickEmailMatchingEngineerCount;
    private List<String> sendMailEmailMatchingEngineerClick;
    private List<String> receiveMailNumber;
    private List<String> sendPerClick;
    private List<EmailAccount> emailAccounts;
    private List<AccountDTO> users;
    private int checkMailInterval;
    private List<BulletinBoardDTO> listBulletinBoardDTO;
    private long accountId;
    private List<String> replyEmailsViaInboxCount;
    private List<String> clickReplyEmailsViaInboxCount;
    private List<String> createSchedulerCount;
    private List<String> sendMailSchedulerCount;

    public DashboardResponseBody(String msg, boolean status, int errorCode) {
        super(msg, status);
    }

    public DashboardResponseBody(String msg) {
        super(msg, false);
    }

    public DashboardResponseBody() {
        this("");
    }

    public String getLatestReceive() {
        return latestReceive;
    }

    public void setLatestReceive(String latestReceive) {
        this.latestReceive = latestReceive;
    }

    public List<String> getClickCount() {
        return clickCount;
    }

    public void setClickCount(List<String> clickCount) {
        this.clickCount = clickCount;
    }

    public List<String> getReceiveMailNumber() {
        return receiveMailNumber;
    }

    public void setReceiveMailNumber(List<String> receiveMailNumber) {
        this.receiveMailNumber = receiveMailNumber;
    }

    public List<String> getSendPerClick() {
        return sendPerClick;
    }

    public void setSendPerClick(List<String> sendPerClick) {
        this.sendPerClick = sendPerClick;
    }

    public boolean isHasSystemError() {
        return hasSystemError;
    }

    public void setHasSystemError(boolean hasSystemError) {
        this.hasSystemError = hasSystemError;
    }

    public List<EmailAccount> getEmailAccounts() {
        return emailAccounts;
    }

    public void setEmailAccounts(List<EmailAccount> emailAccounts) {
        this.emailAccounts = emailAccounts;
    }

    public List<AccountDTO> getUsers() {
        return users;
    }

    public void setUsers(List<AccountDTO> users) {
        this.users = users;
    }

    public int getCheckMailInterval() {
        return checkMailInterval;
    }

    public void setCheckMailInterval(int checkMailInterval) {
        this.checkMailInterval = checkMailInterval;
    }

    public List<String> getClickEmailMatchingEngineerCount() { return clickEmailMatchingEngineerCount; }

    public void setClickEmailMatchingEngineerCount(List<String> clickEmailMatchingEngineerCount) { this.clickEmailMatchingEngineerCount = clickEmailMatchingEngineerCount; }

    public List<String> getSendMailEmailMatchingEngineerClick() { return sendMailEmailMatchingEngineerClick; }

    public void setSendMailEmailMatchingEngineerClick(List<String> sendMailEmailMatchingEngineerClick) { this.sendMailEmailMatchingEngineerClick = sendMailEmailMatchingEngineerClick; }

    public List<BulletinBoardDTO> getListBulletinBoardDTO() { return listBulletinBoardDTO; }

    public void setListBulletinBoardDTO(List<BulletinBoardDTO> listBulletinBoardDTO) { this.listBulletinBoardDTO = listBulletinBoardDTO; }

    public long getAccountId() { return accountId; }

    public void setAccountId(long accountId) { this.accountId = accountId; }

    public List<String> getReplyEmailsViaInboxCount() {
        return replyEmailsViaInboxCount;
    }

    public void setReplyEmailsViaInboxCount(List<String> replyEmailsViaInboxCount) {
        this.replyEmailsViaInboxCount = replyEmailsViaInboxCount;
    }

    public List<String> getClickReplyEmailsViaInboxCount() {
        return clickReplyEmailsViaInboxCount;
    }

    public void setClickReplyEmailsViaInboxCount(List<String> clickReplyEmailsViaInboxCount) {
        this.clickReplyEmailsViaInboxCount = clickReplyEmailsViaInboxCount;
    }

    public List<String> getCreateSchedulerCount() {
        return createSchedulerCount;
    }

    public void setCreateSchedulerCount(List<String> createSchedulerCount) {
        this.createSchedulerCount = createSchedulerCount;
    }

    public List<String> getSendMailSchedulerCount() {
        return sendMailSchedulerCount;
    }

    public void setSendMailSchedulerCount(List<String> sendMailSchedulerCount) {
        this.sendMailSchedulerCount = sendMailSchedulerCount;
    }
}