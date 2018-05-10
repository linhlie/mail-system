package io.owslab.mailreceiver.form;

import java.util.List;

/**
 * Created by khanhlvb on 5/10/18.
 */
public class RemoveUploadedFileForm {
    private List<Long> uploadAttachment;

    public RemoveUploadedFileForm () {

    }

    public RemoveUploadedFileForm(List<Long> uploadAttachment) {
        this.uploadAttachment = uploadAttachment;
    }

    public List<Long> getUploadAttachment() {
        return uploadAttachment;
    }

    public void setUploadAttachment(List<Long> uploadAttachment) {
        this.uploadAttachment = uploadAttachment;
    }
}
