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

    var reSendEmail;

    var extensionCommands = {
        ".pdf": "ms-word:ofv|u|",
        ".docx": "ms-word:ofv|u|",
        ".doc": "ms-word:ofv|u|",
        ".xls": "ms-excel:ofv|u|",
        ".xlsx": "ms-excel:ofv|u|",
        ".xlsm": "ms-excel:ofv|u|",
        ".ppt": "ms-powerpoint:ofv|u|",
        ".pptx": "ms-powerpoint:ofv|u|",
    }

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
        var separateReSendMailDataStr = sessionStorage.getItem("separateReSendMailData");
        var separateReSendMailData = JSON.parse(separateReSendMailDataStr);
        var id = separateReSendMailData.id;
        showMailEditor(id);
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

    function showMailEditor(id) {
        $('#sendMailModal').modal();
        lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
        getDetailMailHistory(id, function (email, accounts, files) {
            reSendEmail = email;
            updateSenderSelector(email, accounts, function (account) {
                showMailContentToEditor(email, accounts, files);
                autoResizeHeight();
                $('#' + rdMailSenderId).off('change');
                $('#' + rdMailSenderId).change(function() {
                    showMailContentToEditor(email, accounts, files);
                    autoResizeHeight();
                });
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
            if(!reSendEmail) return;
            var btn = $(this);
            btn.button('loading');
            var attachmentData = getAttachmentData(attachmentDropzone);
            var filesAttachOrigin = getFileAtachOrigin();
            for(var i=0;i<filesAttachOrigin.length;i++){
                attachmentData.upload.push(filesAttachOrigin[i]);
            }
            var form = {
                messageId: reSendEmail.messageId,
                subject: $("#" + rdMailSubjectId).val(),
                receiver: $("#" + rdMailReceiverId).val().replace(/\s*,\s*/g, ","),
                cc: $("#" + rdMailCCId).val().replace(/\s*,\s*/g, ","),
                content: getMailEditorContent(),
                originAttachment: attachmentData.origin,
                uploadAttachment: attachmentData.upload,
                accountId: !!lastSelectedSendMailAccountId ? lastSelectedSendMailAccountId : undefined,
                sendType: reSendEmail.sendType,
                historyType: 11,
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
            sendMail();
        })
    }

    function getMailEditorContent() {
        var editor = tinymce.get(rdMailBodyId);
        return editor.getContent();
    }

    function updateSenderSelector(email, accounts, callback) {
        accounts = accounts || [];
        var flagAccount = false;
        $('#' + rdMailSenderId).empty();
        $.each(accounts, function (i, item) {
            if(item.account == email.from){
                flagAccount = true;
                lastSelectedSendMailAccountId = item.id;
            }
            $('#' + rdMailSenderId).append($('<option>', {
                value: item.id,
                text : item.account,
                selected: (item.account.toString() === email.from)
            }));
        });

        if(!flagAccount){
            $('#' + rdMailSenderId).empty();
            $.each(accounts, function (i, item) {
                $('#' + rdMailSenderId).append($('<option>', {
                    value: item.id,
                    text : item.account,
                    selected: (item.id.toString() === lastSelectedSendMailAccountId)
                }));
            });
        }
        if (typeof callback === "function") {
            callback();
        }
    }

    function resetValidation() {
        receiverValidate = true;
        ccValidate = true;
        $('#' + rdMailCCId + '-container').removeClass('has-error')
        $('#' + rdMailReceiverId + '-container').removeClass('has-error')
    }

    function showMailContentToEditor(data, accounts, files) {
        var receiverListStr = data.to;
        resetValidation();
        lastSelectedSendMailAccountId = $('#' + rdMailSenderId).val();
        var account;
        for(var i=0;i<accounts.length;i++){
            if(accounts[i].id == lastSelectedSendMailAccountId){
                account = accounts[i];
            }
        }
        document.getElementById(rdMailReceiverId).value = receiverListStr;
        updateMailEditorContent("");
        if (data && account) {
            senderGlobal = account.account;
            var cc = data.cc ? data.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            var externalCC = account.cc ? account.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            externalCCGlobal = externalCC;
            cc = updateCCList(cc,externalCC);
            $('#' + rdMailCCId).importTags(cc.join(","));

            document.getElementById(rdMailSubjectId).value = data.subject;
            updateMailEditorContent(data.body);
            var rdMailAttachment = document.getElementById(rdMailAttachmentId);
            showFileAttach(rdMailAttachment, files);
        }
        updateDropzoneData(attachmentDropzone);
    }


    function updateMailEditorContent(content, preventClear) {
        var editor = tinymce.get(rdMailBodyId);
        editor.setContent(content);
        if (!preventClear) {
            editor.undoManager.clear();
        }
        editor.undoManager.add();
    }

    function showFileAttach(divFileAttachId, files){
        if(files && files.length > 0){
            var filesInnerHTML = "";
            for(var i = 0; i < files.length; i++ ){
                var file = files[i];
                var fileExtension = getFileExtension(file.fileName);
                var command = extensionCommands[fileExtension];
                command = (isWindows() && !!command) ? command : "nope";
                var url = window.location.origin + "/download/fileUpload/" + encodeURIComponent(file.digest) + "/" + file.fileName;
                if(i > 0){
                    filesInnerHTML += "<br/>";
                }
                filesInnerHTML += ("<button type='button' class='btn btn-link download-link' data-id='"+file.id+"' data-filename='" + file.fileName + "' data-command='" + command + "' data-download='" + url + "'>" + file.fileName + "(" + getFileSizeString(file.size) + ") </button>");
                filesInnerHTML += "<span class='remove-mail-attachment'>&nbsp;x&nbsp;</span>";
            }
            divFileAttachId.innerHTML = filesInnerHTML;
            setDownloadLinkClickListener();
            removeAttachOriginListener()
        } else {
            divFileAttachId.innerHTML = "添付ファイルなし";
        }
    }

    function removeAttachOriginListener() {
        $(".remove-mail-attachment").off('click');
        $(".remove-mail-attachment").click(function () {
            var fileAttachmentBtn = $(this).prev();
            if(fileAttachmentBtn){
                var brTag = fileAttachmentBtn.prev();
                if(brTag.is("br")){
                    brTag.remove();
                }else{
                    $(this).next().remove();
                }
                fileAttachmentBtn.remove();
                $(this).remove();
            }
        })
    }

    function setDownloadLinkClickListener() {
        $('button.download-link').off("click");
        $('button.download-link').click(function(event) {
            var command = $(this).attr("data-command");
            var href = $(this).attr("data-download");
            var fileName = $(this).attr('data-filename');
            if(command.startsWith("nope")) {
                alert("Not support features");
            } else {
                doDownload(command+href, fileName);
            }
        });

        $.contextMenu({
            selector: 'button.download-link',
            callback: function(key, options) {
                var m = "clicked: " + key;
                var command = $(this).attr("data-command");
                var href = $(this).attr("data-download");
                var fileName = $(this).attr('data-filename');
                if(key === "open") {
                    if(command.startsWith("nope")) {
                        alert("Not support features");
                    } else {
                        doDownload(command+href, fileName);
                    }
                } else if (key === "save_as") {
                    doDownload(href, fileName);
                }
            },
            items: {
                "open": {"name": "Open"},
                "save_as": {"name": "Save as"},
            }
        });
    }


    function doDownload(href, fileName){
        var a = document.createElement('A');
        a.href = href;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }

    function getFileAtachOrigin() {
        var files = [];
        $('#rdMailAttachment button').each(function(){
            var id = $(this).attr('data-id');
            files.push(id);
        });
        return files;
    }

})(jQuery);
