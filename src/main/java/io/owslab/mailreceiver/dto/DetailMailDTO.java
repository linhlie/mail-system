package io.owslab.mailreceiver.dto;

import com.mariten.kanatools.KanaConverter;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.Email;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by khanhlvb on 3/13/18.
 */
public class DetailMailDTO {
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Tokyo");
    private String messageId;

    private long accountId;

    private String account;

    private String from;

    private String subject;

    private String to;

    private String cc;

    private String bcc;

    private String replyTo;

    private String sentAt;

    private String receivedAt;

    private String replyOrigin;

    private boolean hasAttachment;

    private int contentType;

    private String originalBody;
    private String replacedBody;
    private String signature;
    private String externalCC;

    private List<FileDTO> files;

    public DetailMailDTO(Email email) {
        this.setMessageId(email.getMessageId());
        this.setAccountId(email.getAccountId());
        this.setFrom(email.getFrom());
        this.setSubject(email.getSubject());
        this.setTo(email.getTo());
        this.setCc(email.getCc());
        this.setBcc(email.getBcc());
        this.setReplyTo(email.getReplyTo());
        this.setSentAt(DateFormatUtils.format(email.getSentAt(), "yyyy-MM-dd HH:mm:ss", TIME_ZONE, null));
        this.setReceivedAt(DateFormatUtils.format(email.getReceivedAt(), "yyyy-MM-dd HH:mm:ss", TIME_ZONE, null));
        this.setHasAttachment(email.isHasAttachment());
        this.setContentType(email.getContentType());
        this.setOriginalBody(email.getOriginalBody());
        this.files = new ArrayList<>();
    }

    public DetailMailDTO(Email email, String account) {
        this(email);
        this.setAccount(account);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getOriginalBody() {
        return originalBody;
    }

    public void setOriginalBody(String originalBody) {
        int conv_op_flags = 0;
        conv_op_flags |= KanaConverter.OP_HAN_KATA_TO_ZEN_KATA;
        conv_op_flags |= KanaConverter.OP_ZEN_ASCII_TO_HAN_ASCII;
        String japaneseOptimizedText = KanaConverter.convertKana(originalBody, conv_op_flags);
        this.originalBody = japaneseOptimizedText;
    }

    public String getReplacedBody() {
        return replacedBody;
    }

    public void setReplacedBody(String replacedBody) {
        this.replacedBody = replacedBody;
    }

    public List<FileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<FileDTO> files) {
        this.files = files;
    }

    public boolean addFile(AttachmentFile file) {
        return this.files.add(new FileDTO(file));
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getReplyOrigin() {
        return replyOrigin;
    }

    public void setReplyOrigin(String replyOrigin) {
        this.replyOrigin = replyOrigin;
    }

    public String getExternalCC() {
        return externalCC;
    }

    public void setExternalCC(String externalCC) {
        this.externalCC = externalCC;
    }
}
