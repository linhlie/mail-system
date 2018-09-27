(function () {
    "use strict";
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBodyTab';
    var rdMailAttachmentId = 'rdMailAttachment';
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
    var matching = false;

    var originalContentWrapId = "ows-mail-body";
    var dataLinesConfirm;

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
                'table contextmenu directionality template paste textcolor'
            ],
            menubar: 'edit view insert format table',
            toolbar: 'undo redo | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image',
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
        matching = !!lastSendTo;
        showMailEditor(separateSendMailData.accountId, lastMessageId, lastReceiver, lastTextRange, lastTextMatchRange, lastReplaceType, lastSendTo);
    }

    function autoResizeHeight() {
        var mainHeight = $('#sendDiv').height();
        var oldHeight = $('#' + rdMailBodyId + '_ifr').height();
        var remain = mainHeight - oldHeight;
        var newHeight = $(window).height() - remain;
        console.log($(window).height(), newHeight, remain, mainHeight, oldHeight);
        newHeight = newHeight > 350 ? newHeight : 350;
        resizeHeightEditor(newHeight);
    }
    
    function resizeHeightEditor(newHeight) {
        tinyMCE.DOM.setStyle(tinyMCE.DOM.get(rdMailBodyId + '_ifr'), 'height', newHeight + 'px');
    }

    function showMailEditor(accountId, messageId, receiver, textRange, textMatchRange, replaceType, sendTo) {
        setSendMailTitle(sendTo);
        showMailWithData(accountId, messageId, receiver.messageId, textRange, textMatchRange, replaceType, sendTo, function (email, accounts) {
            showMailContentToEditor(email, accounts, receiver, sendTo)
        });
        $('#' + rdMailSenderId).off('change');
        $('#' + rdMailSenderId).change(function() {
            lastSelectedSendMailAccountId = this.value;
            showMailWithData(this.value, lastMessageId, lastReceiver.messageId, lastTextRange, lastTextMatchRange, lastReplaceType, lastSendTo, function (email, accounts) {
                showMailContentToEditor(email, accounts, lastReceiver, lastSendTo)
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
                sendType: !lastSendTo ? "[返信]" : (lastSendTo === "moto" ? "[元へ]" : "[先へ]"),
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
            if(matching) {
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

    function showMailWithData(accountId, messageId, replyId, range, matchRange, replaceType, sendTo, callback) {
        if(matching) {
            showMailWithReplacedRange(accountId, messageId, replyId, range, matchRange, replaceType, sendTo, callback);
        } else {
            showReplyMail(accountId, messageId, callback)
        }
    }
    
    function showMailContentToEditor(data, accounts, receiverData, sendTo) {
        if(matching) {
            showMailContentToEditorMatching(data, accounts, receiverData, sendTo);
        } else {
            showMailContentToEditorReply(data, accounts, receiverData);
        }
        autoResizeHeight();
    }
    
    function showMailContentToEditorMatching(data, accounts, receiverData, sendTo) {
        var receiverListStr = receiverData.replyTo ? receiverData.replyTo : receiverData.from;
        getInforPartner(receiverListStr, function(partnerInfor){
        	showMailContentToEditorMatchingFinal(data, accounts, receiverData, sendTo, partnerInfor);
        });
    }
    
    function showMailContentToEditorMatchingFinal(data, accounts, receiverData, sendTo, partnerInfor) {
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
            data.originalBody = wrapInDivWithId(originalContentWrapId,data.originalBody);
            var stripped = strip(data.originalBody, originalContentWrapId);
            dataLinesConfirm = getHeaderFooterLines(stripped);
            data.replyOrigin = data.replyOrigin ? wrapText(data.replyOrigin) : data.replyOrigin;
            data.replyOrigin = getReplyWrapper(data);
            data.originalBody = data.replyOrigin ? data.originalBody + data.replyOrigin : data.originalBody;
            data.excerpt = getDecorateExcerpt(data.excerpt, sendTo);
            data.originalBody = data.excerpt + data.originalBody;
            data.originalBody = data.originalBody + data.signature;
            if(partnerInfor != null && partnerInfor != ""){
                data.originalBody = partnerInfor + data.originalBody;
            }
            updateMailEditorContent(data.originalBody);
            if( data.replacedBody != null){
                data.replacedBody = wrapInDivWithId(originalContentWrapId, data.replacedBody);
                stripped = strip(data.replacedBody, originalContentWrapId);
                dataLinesConfirm = getHeaderFooterLines(stripped);
                data.replacedBody = data.replyOrigin ? data.replacedBody + data.replyOrigin : data.replacedBody;
                data.replacedBody = data.excerpt + data.replacedBody;
                data.replacedBody = data.replacedBody + data.signature;
                if(partnerInfor != null && partnerInfor != ""){
                    data.replacedBody = partnerInfor + data.replacedBody;
                }
                updateMailEditorContent(data.replacedBody, true);
            }
            var files = data.files ? data.files : [];
            updateDropzoneData(attachmentDropzone, files);
        }
    }
    
    function showMailContentToEditorReply(data, accounts, receiverData) {
        var receiverListStr = receiverData.replyTo ? receiverData.replyTo : receiverData.from;
        getInforPartner(receiverListStr, function(partnerInfor){
        	showMailContentToEditorReplyFinal(data, accounts, receiverData, partnerInfor);
        });
    }

    function showMailContentToEditorReplyFinal(data, accounts, receiverData, partnerInfor) {
        var receiverListStr = receiverData.replyTo ? receiverData.replyTo : receiverData.from;
        resetValidation();
        document.getElementById(rdMailReceiverId).value = receiverListStr;
        updateMailEditorContent("");
        if (data) {
            updateSenderSelector(data, accounts);
            senderGlobal = data.account;
            var to = data.to ? data.to.replace(/\s*,\s*/g, ",").split(",") : [];
            var cc = data.cc ? data.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            var externalCC = data.externalCC ? data.externalCC.replace(/\s*,\s*/g, ",").split(",") : [];
            externalCCGlobal = externalCC;
            cc = updateCCList(cc,to);
            var indexOfSender = cc.indexOf(data.account);
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
            data.replyOrigin = data.replyOrigin ? wrapText(data.replyOrigin) : data.replyOrigin;
            data.replyOrigin = getReplyWrapper(data);
            data.originalBody = data.replyOrigin ? data.replyOrigin : "";
            data.originalBody = getExcerptWithGreeting(data.excerpt) + data.originalBody;
            data.originalBody = data.originalBody + data.signature;
            if(partnerInfor != null && partnerInfor != ""){
                data.originalBody = partnerInfor + data.originalBody;
            }
            updateMailEditorContent(data.originalBody);
        }
        updateDropzoneData(attachmentDropzone);
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
    
    function getInforPartner(sentTo, callback){
        function onSuccess(response ) {
            if(response) {
            	if(response.status){
            		if(typeof callback == 'function'){
                    	callback(response.msg);
                    }
            	}else{
            		if(typeof callback == 'function'){
                    	callback();
                    }
            	}            
            }
        }
        function onError() {
        	if(typeof callback == 'function'){
            	callback();
            	alert('所属企業の情報の取得に失敗しました。');
            }
        }

        getInforPartnerAPI(sentTo, onSuccess, onError);
    }

})(jQuery);
