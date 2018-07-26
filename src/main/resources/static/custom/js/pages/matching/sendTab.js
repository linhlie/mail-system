(function () {
    "use strict";
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBody';
    var rdMailAttachmentId = 'rdMailAttachment';
    var rdMailSenderId = 'rdMailSender';
    var rdMailReceiverId = 'rdMailReceiver';
    var rdMailCCId = 'rdMailCC';

    var receiverValidate = true;
    var ccValidate = true;

    var externalCCGlobal = [];
    var senderGlobal = "";

    var attachmentDropzoneId = "#attachment-dropzone";
    var attachmentDropzone;

    var lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
    var lastReceiver;
    var lastMessageId;
    var lastTextRange;
    var lastTextMatchRange;
    var lastReplaceType;
    var lastSendTo;

    $(function () {
        initDropzone();
        var separateSendMailDataStr = sessionStorage.getItem("separateSendMailData");
        var separateSendMailData = JSON.parse(separateSendMailDataStr);
        lastMessageId = separateSendMailData.messageId;
        lastReceiver = separateSendMailData.receiver;
        lastTextRange = separateSendMailData.textRange;
        lastTextMatchRange = separateSendMailData.textMatchRange;
        lastReplaceType = separateSendMailData.replaceType;
        lastSendTo = separateSendMailData.sendTo;
        showMailEditor(separateSendMailData.accountId, lastMessageId, lastReceiver, lastTextRange, lastTextMatchRange, lastReplaceType, lastSendTo);
    });

    function showMailEditor(accountId, messageId, receiver, textRange, textMatchRange, replaceType, sendTo) {
        setSendMailTitle(sendTo);
        showMailWithReplacedRange(accountId, messageId, receiver.messageId, textRange, textMatchRange, replaceType, sendTo, function (email, accounts) {
            showMailContenttToEditor(email, accounts, receiver, sendTo)
        });
        $('#' + rdMailSenderId).off('change');
        $('#' + rdMailSenderId).change(function() {
            lastSelectedSendMailAccountId = this.value;
            showMailWithReplacedRange(this.value, lastMessageId, lastReceiver.messageId, lastTextRange, lastTextMatchRange, lastReplaceType, lastSendTo, function (email, accounts) {
                showMailContenttToEditor(email, accounts, lastReceiver, lastSendTo)
            });
        });
        $("button[name='sendSuggestMailClose']").off('click');
        $('#cancelSendSuggestMail').button('reset');
        $("button[name='sendSuggestMailClose']").click(function() {
            var btn = $('#cancelSendSuggestMail');
            btn.button('loading');
            var attachmentData = getAttachmentData(attachmentDropzone);
            if(attachmentData.upload.length == 0) {
                btn.button('reset');
                $('#sendMailModal').modal('hide');
                return;
            }
            var form = {
                uploadAttachment: attachmentData.upload,
            };
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/removeUploadedFiles",
                data: JSON.stringify(form),
                dataType: 'json',
                cache: false,
                timeout: 600000,
                success: function (data) {
                    btn.button('reset');
                    $('#sendMailModal').modal('hide');

                },
                error: function (e) {
                    console.log("ERROR : sendSuggestMail: ", e);
                    btn.button('reset');
                    $('#sendMailModal').modal('hide');
                }
            });
        });
        $('#sendSuggestMail').off('click');
        $('#sendSuggestMail').button('reset');
        $("#sendSuggestMail").click(function () {
            receiverValidate = validateAndShowEmailListInput(rdMailReceiverId, false);
            ccValidate = validateAndShowEmailListInput(rdMailCCId, true);
            if(!(receiverValidate && ccValidate)) return;
            var btn = $(this);
            btn.button('loading');
            var attachmentData = getAttachmentData(attachmentDropzone);
            var form = {
                messageId: lastReceiver.messageId,
                subject: $( "#" + rdMailSubjectId).val(),
                receiver: $( "#" + rdMailReceiverId).val().replace(/\s*,\s*/g, ","),
                cc: $( "#" + rdMailCCId).val().replace(/\s*,\s*/g, ","),
                content: getMailEditorContent(),
                originAttachment: attachmentData.origin,
                uploadAttachment: attachmentData.upload,
                accountId: !!lastSelectedSendMailAccountId ? lastSelectedSendMailAccountId : undefined,
                matchingMessageId: messageId,
                sendType: lastSendTo === "moto" ? "[元へ]" : "[先へ]",
                historyType: lastSendTo === "moto" ? 1 : 2,
            };
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/user/sendRecommendationMail",
                data: JSON.stringify(form),
                dataType: 'json',
                cache: false,
                timeout: 600000,
                success: function (data) {
                    btn.button('reset');
                    console.log("sendSuggestMail: ", data);
                    $('#sendMailModal').modal('hide');
                    if(data && data.status){
                        //TODO: noti send mail success
                    } else {
                        //TODO: noti send mail failed
                    }

                },
                error: function (e) {
                    btn.button('reset');
                    console.log("ERROR : sendSuggestMail: ", e);
                    $('#sendMailModal').modal('hide');
                    //TODO: noti send mail error
                }
            });
        })
    }

    function showMailContenttToEditor(data, accounts, receiverData, sendTo) {
        var receiverListStr = receiverData.replyTo ? receiverData.replyTo : receiverData.from
        resetValidation();
        document.getElementById(rdMailReceiverId).value = receiverListStr;
        updateMailEditorContent("");
        if(data){
            updateSenderSelector(data, accounts);
            senderGlobal = data.account;
            var to = data.to ? data.to.replace(/\s*,\s*/g, ",").split(",") : [];
            var cc = data.cc ? data.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            var externalCC = data.externalCC ? data.externalCC.replace(/\s*,\s*/g, ",").split(",") : [];
            externalCCGlobal = externalCC;
            cc = updateCCList(cc,to);
            var indexOfSender = cc.indexOf(data.account);
            if(indexOfSender > -1){
                cc.splice(indexOfSender, 1);
            }
            var receiverList = receiverListStr.replace(/\s*,\s*/g, ",").split(",");
            if(receiverList.length > 0) {
                cc = updateCCList(cc, [receiverData.from]);
            }
            for(var i = 0; i < receiverList.length; i++) {
                var receiver = receiverList[i];
                var indexOfReceiver = cc.indexOf(receiver);
                if (indexOfReceiver > -1) {
                    cc.splice(indexOfReceiver, 1)
                }
            }
            cc = updateCCList(cc, externalCC);
            $('#' + rdMailCCId).importTags(cc.join(","));
            document.getElementById(rdMailSubjectId).value = data.subject;
            data.replacedBody = data.replacedBody ? (isHTML(data.originalBody) ? data.replacedBody : wrapPlainText(data.replacedBody)) : data.replacedBody;
            data.originalBody = wrapText(data.originalBody);
            data.replyOrigin = data.replyOrigin ? wrapText(data.replyOrigin) : data.replyOrigin;
            data.replyOrigin = getReplyWrapper(data);
            data.originalBody = data.replyOrigin ? data.originalBody + data.replyOrigin : data.originalBody;
            data.excerpt = getDecorateExcerpt(data.excerpt, sendTo);
            data.originalBody = data.excerpt + data.originalBody;
            data.originalBody = data.originalBody + data.signature;
            updateMailEditorContent(data.originalBody);
            if( data.replacedBody != null){
                data.replacedBody = data.replyOrigin ? data.replacedBody + data.replyOrigin : data.replacedBody;
                data.replacedBody = data.excerpt + data.replacedBody;
                data.replacedBody = data.replacedBody + data.signature;
                updateMailEditorContent(data.replacedBody, true);
            }
            var files = data.files ? data.files : [];
            updateDropzoneData(attachmentDropzone, files);
        }
    }

    function resetValidation() {
        receiverValidate = true;
        ccValidate = true;
        $('#' + rdMailCCId + '-container').removeClass('has-error')
        $('#' + rdMailReceiverId + '-container').removeClass('has-error')
    }

    function setSendMailTitle(sendTo) {
        var title = "返信";
        if(sendTo === "moto") {
            title = "マッチング【元】へメール送信";
        } else if(sendTo === "saki") {
            title = "マッチング【先】へメール送信";
        }
        $('#sendSuggestMailTitle').text(title);
    }

    function updateMailEditorContent(content, preventClear){
        console.log("content: ", content);
        var editor = tinymce.get(rdMailBodyId);
        editor.setContent(content);
        if(!preventClear){
            editor.undoManager.clear();
        }
        editor.undoManager.add();
    }

    function updateSenderSelector(email, accounts) {
        accounts = accounts || [];
        $('#' + rdMailSenderId).empty();
        $.each(accounts, function (i, item) {
            $('#' + rdMailSenderId).append($('<option>', {
                value: item.id,
                text : item.account,
                selected: (item.id.toString() === lastSelectedSendMailAccountId)
            }));
        });
    }

    function initDropzone() {
        Dropzone.autoDiscover = false;
        attachmentDropzone = new Dropzone("div" + attachmentDropzoneId, {
            url: "/upload",
            addRemoveLinks: true,
            maxFilesize: 2,
            filesizeBase: 1000,
            dictRemoveFile: "削除",
            dictCancelUpload: "キャンセル",
            dictDefaultMessage: "Drop files here or click to upload.",
            init: function() {
                this.on("success", function(file, response) {
                    if(response && response.status){
                        var data = response.list && response.list.length > 0 ? response.list[0] : null;
                        if(data){
                            file.id = data.id;
                        }
                    }
                });
                this.on("removedfile", function(file) {
                    if(!!file && !!file.upload && !!file.id){
                        removeFile(file.id)
                    }
                });
            },
            thumbnail: function(file, dataUrl) {}
        })
    }

    function getMailEditorContent() {
        var editor = tinymce.get(rdMailBodyId);
        return editor.getContent();
    }

})(jQuery);
