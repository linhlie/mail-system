(function () {
    "use strict";
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBodyTab';
    var rdMailSenderId = 'rdMailSender';
    var rdMailReceiverId = 'rdMailReceiver';
    var rdMailCCId = 'rdMailCC';

    var receiverValidate = true;
    var ccValidate = true;

    var externalCCGlobal = [];
    var senderGlobal = "";

    var attachmentDropzoneId = "#attachment-dropzone-tab";
    var attachmentDropzone;

    var lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
    var lastReceiver;
    var lastMessageId;
    var lastTextRange;
    var lastTextMatchRange;
    var lastReplaceType;
    var lastSendTo;
    var lastHistoryType;
    var engineer;
    var type;
    var matching = false;

    var originalContentWrapId = "ows-mail-body";
    var dataLinesConfirm;

    var currentEmail;
    var currentEngineer;
    var emailAccounts = [];

    var SEND_TO_ENGINEER = "sendToEngineer";
    var SEND_TO_EMAIL_MATCHING = "send to email matching";
    var REPLY_EMAIL = "reply";

    $(function () {
        initDropzone();
        $('#' + rdMailCCId).tagsInput({
            defaultText: '',
            minInputWidth: 150,
            maxInputWidth: 600,
            width: 'auto',
            pattern: /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
            validationMsg: 'メールアドレスを入力してください',
            onChange: function (elem, elem_tags) {
                $('.tag').each(function () {
                    var tag = $(this).text();
                    var email = tag.substring(0, tag.length - 3);
                    var globalMailDoman = getEmailDomain(senderGlobal);
                    var emailDomain = getEmailDomain(email);
                    if (globalMailDoman == emailDomain) {
                        $(this).css('background-color', 'yellow');
                    } else if(externalCCGlobal.indexOf(email) >= 0) {
                        $(this).css('background-color', 'yellow');
                    }
                });
            }
        });
        tinymce.init({
            force_br_newlines : true,
            force_p_newlines : false,
            forced_root_block : '',
            selector: '#' + rdMailBodyId,
            language: 'ja',
            theme: 'modern',
            statusbar: false,
            height: 350,
            plugins: [
                'advlist autolink link image lists charmap preview hr anchor pagebreak',
                'searchreplace visualblocks visualchars code insertdatetime nonbreaking',
                'table contextmenu directionality template paste textcolor colorpicker'
            ],
            menubar: 'edit view insert format table',
            toolbar: 'undo redo | fontsizeselect | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image',
            fontsize_formats: '6pt 8pt 10pt 11pt 12pt 13pt 14pt 16pt 18pt 20pt 24pt 28pt 32pt 36pt 40pt 45pt 50pt',
            init_instance_callback: function (editor) {
                init();
            }
        });
    });
    
    function init() {
        var separateSendMailDataStr = sessionStorage.getItem("separateSendMailData");
        var separateSendMailData = JSON.parse(separateSendMailDataStr);
        lastMessageId = separateSendMailData.messageId;
        lastReceiver = separateSendMailData.receiver;
        lastTextRange = separateSendMailData.textRange;
        lastTextMatchRange = separateSendMailData.textMatchRange;
        lastReplaceType = separateSendMailData.replaceType;
        lastSendTo = separateSendMailData.sendTo;
        lastHistoryType = separateSendMailData.historyType;
        type = separateSendMailData.type;
        engineer = separateSendMailData.engineer;
        matching = !!lastSendTo;
        showMailEditor(separateSendMailData.accountId, lastMessageId, lastReceiver, lastTextRange, lastTextMatchRange, lastReplaceType, lastSendTo, type, engineer);
    }

    function autoResizeHeight() {
        var mainHeight = $('#sendDiv').height();
        var oldHeight = $('#' + rdMailBodyId + '_ifr').height();
        var remain = mainHeight - oldHeight;
        var newHeight = $(window).height() - remain;
        newHeight = newHeight > 350 ? newHeight : 350;
        resizeHeightEditor(newHeight);
    }
    
    function resizeHeightEditor(newHeight) {
        tinyMCE.DOM.setStyle(tinyMCE.DOM.get(rdMailBodyId + '_ifr'), 'height', newHeight + 'px');
    }

    function showMailEditor(accountId, messageId, receiver, textRange, textMatchRange, replaceType, sendTo, type, enginner) {
        setSendMailTitle(sendTo);
        console.log(type);
        switch (type) {
            case SEND_TO_ENGINEER:
                composeEmailToEngineer(messageId, receiver, enginner.id);
                break;
            case REPLY_EMAIL:
                composeReplyEmail(messageId, receiver, enginner);
                break;
            case SEND_TO_EMAIL_MATCHING:
                composeMatchingEmail(accountId, messageId, receiver.messageId, textRange, textMatchRange, replaceType, sendTo, receiver);
                break;
        }

        $("button[name='sendSuggestMailClose']").off('click');
        $('#cancelSendSuggestMail').button('reset');
        $("button[name='sendSuggestMailClose']").click(function() {
            var btn = $('#cancelSendSuggestMail');
            btn.button('loading');
            var attachmentData = getAttachmentData(attachmentDropzone);
            if(attachmentData.upload.length == 0) {
                btn.button('reset');
                window.close();
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
                    window.close();

                },
                error: function (e) {
                    console.log("ERROR : removeUploadedFiles: ", e);
                    btn.button('reset');
                    window.close();
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
                sendType: lastHistoryType,
                historyType: lastHistoryType,
            };
            
            function sendMail() {
                btn.button('loading');
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
                        if(data && data.status){
                            alert("成功しました");
                        } else {
                            alert("失敗しました");
                        }
                        window.close();

                    },
                    error: function (e) {
                        btn.button('reset');
                        console.log("ERROR : sendSuggestMail: ", e);
                        $('#sendMailModal').modal('hide');
                        //TODO: noti send mail error
                    }
                });
            }
            var stripped = strip(form.content, originalContentWrapId);
            var afterEditDataLines = getHeaderFooterLines(stripped);
            if(matching || type === "sendToEngineer") {
                checkDataLines(dataLinesConfirm, afterEditDataLines, function (allowSend) {
                    if(allowSend) {
                        sendMail();
                    }
                });
            } else {
                sendMail();
            }
        })
    }

    function composeEmailToEngineer(messageId, receiver, engineerId) {
        messageId = messageId.replace(/\+/g, '%2B');
        var replyId = messageId;
        var range = receiver.matchRange;
        var matchRange = receiver.range;
        var replaceType = 1;
        var url = "/user/engineerMatching/replyEngineer?messageId=" + messageId + "&replyId=" + replyId + "&range=" + range + "&matchRange=" + matchRange + "&replaceType=" + replaceType + "&engineerId=" + engineerId;
        var type = 10;
        url = url + "&type=" + type;

        function onSuccess(response) {
            if(response && response.status) {
                currentEmail = response.mail;
                currentEngineer = response.engineer;
                emailAccounts = response.list;
                updateSenderSelector(receiver, response.emailAccountId, SEND_TO_ENGINEER);
                showMailContentToEditorEngineer();
            }
        }

        function onError(e) {
            console.error("composeEmailToEngineer ERROR : ", e);
        }
        composeEmailAPI(url, onSuccess, onError);
    }

    function composeReplyEmail(messageId, receiver, engineer) {
        messageId = messageId.replace(/\+/g, '%2B');
        var receiverStr = receiver.replyTo ? receiver.replyTo : receiver.from;
        if(engineer && engineer!=null){
            var type = 9;
            var url = "/user/engineerMatching/replyEmail?messageId=" + messageId + "&type=" + type + "&receiver=" + receiverStr + "&engineerId=" +engineer.id;
        }else{
            var type = window.location.href.indexOf("extractSource") >= 0 ? 6 : 7;
            var url = "/user/matchingResult/replyEmail?messageId=" + messageId + "&type=" + type + "&receiver=" + receiverStr;
        }

        function onSuccess(response) {
            if(response && response.status) {
                currentEmail = response.mail;
                currentEngineer = response.engineer;
                emailAccounts = response.list;
                updateSenderSelector(receiver, response.emailAccountId, REPLY_EMAIL);
                showMailContentToEditor(receiver);
            }
        }

        function onError(e) {
            console.error("composeEmail ERROR: ", e);
        }
        composeEmailAPI(url, onSuccess, onError);
    }

    function composeMatchingEmail(accountId, messageId, replyId, range, matchRange, replaceType, sendTo, receiver) {
        messageId = messageId.replace(/\+/g, '%2B');
        replyId = replyId.replace(/\+/g, '%2B');
        var url = "/user/matchingResult/editEmail?messageId=" + messageId + "&replyId=" + replyId + "&range=" + range + "&matchRange=" + matchRange + "&replaceType=" + replaceType;
        var type = sendTo === "moto" ? 4 : 5;
        var receiverStr = receiver.replyTo ? receiver.replyTo : receiver.from;
        url = url + "&type=" + type + "&receiver=" + receiverStr;

        function onSuccess(response) {
            if(response && response.status) {
                currentEmail = response.mail;
                emailAccounts = response.list;
                updateSenderSelector(receiver, response.emailAccountId, SEND_TO_EMAIL_MATCHING, sendTo);
                showMailMatchingContentToEditor(receiver, sendTo)
            }
        }

        function onError(e) {
            console.error("composeEmail ERROR : ", e);
        }
        composeEmailAPI(url, onSuccess, onError);
    }

    function updateSenderSelector(receiver, accountId, type, sendTo) {
        var accounts = emailAccounts || [];
        $('#' + rdMailSenderId).empty();
        $.each(accounts, function (i, item) {
            $('#' + rdMailSenderId).append($('<option>', {
                value: item.id,
                text : item.account,
                selected: (accountId > 0? item.id == accountId : item.id == lastSelectedSendMailAccountId)
            }));
        });
        lastSelectedSendMailAccountId = $( '#' + rdMailSenderId +' option:selected' ).val();

        $('#' + rdMailSenderId).off('change');
        $('#' + rdMailSenderId).change(function() {
            lastSelectedSendMailAccountId = this.value;
            localStorage.setItem("selectedSendMailAccountId", lastSelectedSendMailAccountId);
            switch (type) {
                case REPLY_EMAIL:
                    showMailContentToEditor(receiver);
                    break;
                case SEND_TO_ENGINEER:
                    showMailContentToEditorEngineer();
                    break;
                case SEND_TO_EMAIL_MATCHING:
                    showMailMatchingContentToEditor(receiver, sendTo);
                    break;
            }
        });
    }

    function getSenderSelected() {
        var accountId = $( '#' + rdMailSenderId +' option:selected' ).val();
        for(var i=0;i< emailAccounts.length ;i++){
            if(emailAccounts[i].id == accountId){
                return emailAccounts[i];
            }
        }
        return null;
    }

    function showMailContentToEditorEngineer() {
        if(!currentEngineer && currentEngineer == null) return
        var receiverListStr = currentEngineer.mailAddress;
        resetValidation();
        document.getElementById(rdMailReceiverId).value = receiverListStr;
        updateMailEditorContent("");
        var data = currentEmail;
        var sender = getSenderSelected();
        if (data && data != null && sender != null) {
            senderGlobal = sender.account;
            var externalCC = sender.cc ? sender.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            externalCCGlobal = externalCC;
            $('#' + rdMailCCId).importTags(externalCC.join(","));

            document.getElementById(rdMailSubjectId).value = data.subject;
            var replacedBody = data.replacedBody ? (isHTML(data.originalBody) ? data.replacedBody : wrapPlainText(data.replacedBody)) : data.replacedBody;
            var originalBody = wrapText(originalBody);
            originalBody = wrapInDivWithId(originalContentWrapId,originalBody);
            var stripped = strip(originalBody, originalContentWrapId);
            dataLinesConfirm = getHeaderFooterLines(stripped);
            var replyOrigin = data.replyOrigin ? wrapText(data.replyOrigin) : data.replyOrigin;
            replyOrigin = getReplyWrapper(data, replyOrigin);
            originalBody = replyOrigin ? originalBody + replyOrigin : originalBody;
            originalBody = sender.greeting + "<br/><br/>" + originalBody + sender.signature;
            updateMailEditorContent(originalBody);
            if( replacedBody != null){
                replacedBody = wrapInDivWithId(originalContentWrapId, replacedBody);
                stripped = strip(replacedBody, originalContentWrapId);
                dataLinesConfirm = getHeaderFooterLines(stripped);
                replacedBody = replyOrigin ? replacedBody + replyOrigin : replacedBody;
                replacedBody = sender.greeting + "<br/><br/>" + replacedBody + sender.signature;
                updateMailEditorContent(replacedBody, true);
            }
            var files = data.files ? data.files : [];
            updateDropzoneData(attachmentDropzone, files);
        }
    }

    function showMailContentToEditor(receiverData) {
        var receiverListStr = receiverData.replyTo ? receiverData.replyTo : receiverData.from;
        resetValidation();
        document.getElementById(rdMailReceiverId).value = receiverListStr;
        updateMailEditorContent("");
        var data = currentEmail;
        var sender = getSenderSelected();
        if (data && data != null && sender != null) {
            senderGlobal = sender.account;
            var to = data.to ? data.to.replace(/\s*,\s*/g, ",").split(",") : [];
            var cc = data.cc ? data.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            var externalCC = sender.cc ? sender.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            externalCCGlobal = externalCC;
            cc = updateCCList(cc,to);
            var indexOfSender = cc.indexOf(sender.account);
            if (indexOfSender > -1) {
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
            var replyOrigin = data.replyOrigin ? wrapText(data.replyOrigin) : data.replyOrigin;
            replyOrigin = getReplyWrapper(data, replyOrigin);
            var originalBody = replyOrigin ? replyOrigin : "";
            if(currentEngineer && currentEngineer != null){
                originalBody = currentEngineer.introduction + originalBody;
            }
            originalBody = sender.greeting + "<br/><br/>" + originalBody + sender.signature;
            updateMailEditorContent(originalBody);
        }
        updateDropzoneData(attachmentDropzone);
    }

    function showMailMatchingContentToEditor(receiverData, sendTo) {
        var receiverListStr = receiverData.replyTo ? receiverData.replyTo : receiverData.from;
        resetValidation();
        document.getElementById(rdMailReceiverId).value = receiverListStr;
        updateMailEditorContent("");
        var data = currentEmail;
        var sender = getSenderSelected();
        if (data && data != null && sender != null) {
            senderGlobal = sender.account;
            var to = data.to ? data.to.replace(/\s*,\s*/g, ",").split(",") : [];
            var cc = data.cc ? data.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            var externalCC = sender.cc ? sender.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            externalCCGlobal = externalCC;
            cc = updateCCList(cc,to);
            var indexOfSender = cc.indexOf(sender.account);
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
            var replacedBody = data.replacedBody ? (isHTML(data.originalBody) ? data.replacedBody : wrapPlainText(data.replacedBody)) : data.replacedBody;
            var originalBody = wrapText(data.originalBody);
            originalBody = wrapInDivWithId(originalContentWrapId, originalBody);
            var stripped = strip(originalBody, originalContentWrapId);
            dataLinesConfirm = getHeaderFooterLines(stripped);
            var replyOrigin = data.replyOrigin ? wrapText(data.replyOrigin) : data.replyOrigin;
            replyOrigin = getReplyWrapper(data, replyOrigin);
            originalBody = replyOrigin ? originalBody + replyOrigin : originalBody;
            originalBody = sender.greeting + "<br/><br/>" + originalBody + sender.signature;
            updateMailEditorContent(originalBody);
            if( replacedBody != null){
                replacedBody = wrapInDivWithId(originalContentWrapId, replacedBody);
                stripped = strip(replacedBody, originalContentWrapId);
                dataLinesConfirm = getHeaderFooterLines(stripped);
                replacedBody = replyOrigin ? replacedBody + replyOrigin : replacedBody;
                replacedBody = sender.greeting + "<br/><br/>" + replacedBody + sender.signature;
                updateMailEditorContent(replacedBody, true);
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
        var editor = tinymce.get(rdMailBodyId);
        editor.setContent(content);
        if(!preventClear){
            editor.undoManager.clear();
        }
        editor.undoManager.add();
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
