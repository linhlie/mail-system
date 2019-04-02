(function () {
    "use strict";
    var sendMailHistoryTableId = 'sendMailHistory';
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailAttachmentDivId = 'mailAttachment';
    var historyQuickFilterId = 'historyQuickFilter';
    var fromDateId = 'historyFromDate';
    var toDateId = 'historyToDate';
    var historySearchBtnId = 'historySearchBtn';
    var histories = null;
    var historyDataTable;
    var selectedRowData;

    var lastSelectedSendMailAccountId;
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBody';
    var rdMailSenderId = 'rdMailSender';
    var rdMailReceiverId = 'rdMailReceiver';
    var rdMailAttachmentId = 'rdMailAttachment';
    var rdMailCCId = 'rdMailCC';
    var attachmentDropzoneId = "#reply-dropzone";
    var attachmentDropzone;

    var receiverValidate = true;
    var ccValidate = true;

    var externalCCGlobal = [];
    var senderGlobal = "";
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

    var replaceHistoryHTML = '<tr role="row" class="hidden">' +
        '<td class="clickable" name="historyRow" rowspan="1" colspan="1" data="sentAt"><span></span></td>' +
        '<td class="clickable" name="historyRow" rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td class="clickable" name="historyRow" rowspan="1" colspan="1" data="to"><span></span></td>' +
        '<td class="clickable" name="historyRow" rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '<td class="clickable" name="historyRow" rowspan="1" colspan="1" data="sendType"><span></span></td>' +
        '<td class="clickable" name="historyRow" rowspan="1" colspan="1" data="originalReceivedAt"><span></span></td>' +
        '<td class="clickable" name="historyRow" rowspan="1" colspan="1" data="matchingReceivedAt"><span></span></td>' +
        '<td class="clickable" name="historyRow" rowspan="1" colspan="1" data="matchingMailAddress"><span></span></td>' +
        '<td class="clickable" name="historyRow" rowspan="1" colspan="1" data="username"><span></span></td>' +
        '<td class="clickable text-center" name="reSend" rowspan="1" colspan="1">' +
        '<button type="button" class="btn btn-xs btn-default">再送</button>' +
        '</td>';
    '</tr>';
    $(function () {
        initTagsInput();
        initDropzone();
        initSortHistory();
        setupDatePickers();
        updateDisableDatePickers($('#' + historyQuickFilterId).val());
        var payload = getSearchPayload();
        loadHistoryData(payload);
        initStickyHeader();
        addEventListeners();
    });

    function initTagsInput() {
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
            init: function () {
                this.on("success", function (file, response) {
                    if (response && response.status) {
                        var data = response.list && response.list.length > 0 ? response.list[0] : null;
                        if (data) {
                            file.id = data.id;
                        }
                    }
                });
                this.on("removedfile", function (file) {
                    if (!!file && !!file.upload && !!file.id) {
                        removeFile(file.id)
                    }
                });
            },
            thumbnail: function (file, dataUrl) {
            }
        })
    }

    function addEventListeners() {
        addHistorySearchButtonClickListener()
    }

    function addHistorySearchButtonClickListener() {
        $('#' + historySearchBtnId).off('click');
        $('#' + historySearchBtnId).click(function () {
            var payload = getSearchPayload();
            loadHistoryData(payload);
        });
    }

    function getSearchPayload() {
        var payload = {
            filterType: $("#" + historyQuickFilterId).val(),
            fromDateStr: $("#" + fromDateId).val(),
            toDateStr: $("#" + toDateId).val(),
        };
        return payload;
    }

    function setupDatePickers() {
        var datepicker = $.fn.datepicker.noConflict();
        $.fn.bootstrapDP = datepicker;
        $('#' + fromDateId).datepicker({
            beforeShow: function() {
                setTimeout(function(){
                    $('.ui-datepicker').css('z-index', 99999999999999);
                }, 0);
            }
        });
        $('#' + toDateId).datepicker({
            beforeShow: function() {
                setTimeout(function(){
                    $('.ui-datepicker').css('z-index', 99999999999999);
                }, 0);
            }
        });
        $('#' + historyQuickFilterId).change(function() {
            updateDisableDatePickers(this.value);
        });
    }

    function updateDisableDatePickers(type) {
        var disabled = type !== "期間";
        $('#' + fromDateId).datepicker("option", "disabled", disabled);
        $('#' + toDateId).datepicker("option", "disabled", disabled);
    }

    function enableResizeColums() {
        $("#" + sendMailHistoryTableId).colResizable(
            {
                disable: true,
            }
        );
        if(screen.width > 768){
            $("#" + sendMailHistoryTableId).colResizable(
                {
                    resizeMode:'overflow',
                    minWidth: 30
                }
            );
        }else{
            $("#" + sendMailHistoryTableId).colResizable(
                {
                    minWidth: 30
                }
            );
        }
    }

    function loadHistoryData(payload) {
        payload = payload ? payload : {};
        var payloadStr = JSON.stringify(payload);
        $('body').loadingModal({
            position: 'auto',
            text: 'ローディング...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/user/sendMailHistoryData",
            data: payloadStr,
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                $('body').loadingModal('hide');
                if (data && data.status) {
                    histories = data.list;
                } else {
                    console.error("[ERROR] submit failed: ");
                }
                updateData();
            },
            error: function (e) {
                console.error("[ERROR] submit error: ", e);
                $('body').loadingModal('hide');
                updateData();
            }
        });
    }

    function updateData() {
        showHistoryData(sendMailHistoryTableId, histories);
    }

    function showHistoryData(tableId, data) {
        destroySortHistory();
        removeAllRow(tableId, replaceHistoryHTML);
        if (data && data.length > 0) {
            var html = replaceHistoryHTML;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("historyRow", selectedRow);
            setRowClickListener("reSend", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = histories[index];
                if (rowData && rowData.id) {
                    showMailEditor(rowData.id);
                }
            });
        }
        selectFirstRow();
        updateHistoryDataTrigger(tableId);
        enableResizeColums();
    }

    function updateHistoryDataTrigger(tableId) {
        $("#" + tableId).trigger("updateAll", [ true, function () {

        } ]);
    }

    function destroySortHistory() {
        if (!!historyDataTable) {
            historyDataTable.destroy();
        }
    }

    function initSortHistory() {
        $("#" + sendMailHistoryTableId).tablesorter(
            {
                theme: 'default',
                headers : { 9 : { sorter: false } },
                sortList: [[0, 1], [1, 0]]
            });
    }

    function addRowWithData(tableId, data, index) {
        var table = document.getElementById(tableId);
        if (!table) return "";
        var rowToClone = table.rows[1];
        var row = rowToClone.cloneNode(true);
        row.setAttribute("data", index);
        row.className = undefined;
        var cells = row.cells;
        for (var i = 0; i < cells.length; i++) {
            var cell = cells.item(i);
            var cellKeysData = cell.getAttribute("data");
            if (!cellKeysData || cellKeysData.length == 0) continue;
            var cellKeys = cellKeysData.split(".");
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if (cellNode) {
                if (cellNode.nodeName == "SPAN") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    if (Array.isArray(cellData)) {
                        cellNode.textContent = cellData.length;
                    } else {
                        cellNode.textContent = cellData;
                    }
                }
            }
        }
        return row.outerHTML;
    }

    function setRowClickListener(name, callback) {
        $("td[name='" + name + "']").off('click');
        $("td[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function showHistory() {
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = histories[index];
        showMailContent(rowData);
    }

    function selectFirstRow() {
        if (histories && histories.length > 0) {
            var firstTr = $('#' + sendMailHistoryTableId).find(' tbody tr:first');
            firstTr.addClass('highlight-selected').siblings().removeClass('highlight-selected');
            var index = firstTr[0].getAttribute("data");
            var rowData = histories[index];
            showMailContent(rowData);
            selectedRowData = rowData;
        }
    }

    function selectedRow() {
        $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
        showHistory.call(this);
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = histories[index];
        selectedRowData = rowData;
    }

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function showMailContent(data) {
        if(data){
            if(data.hasAttachment){
                showMailReSendHistories(data.id, function (files) {
                    showMailContentDetail(data, files);
                });
            }else{
                showMailContentDetail(data);
            }
        }
    }

    function showMailContentDetail(data, files) {
        var mailSubjectDiv = document.getElementById(mailSubjectDivId);
        var mailAttachmentDiv = document.getElementById(mailAttachmentDivId);
        mailSubjectDiv.innerHTML = "";
        showMailBodyContent({body: ""});
        mailAttachmentDiv.innerHTML = "";
        if (data) {
            mailSubjectDiv.innerHTML = '<div class="mailbox-read-info">' +
                '<h5><b>' + data.subject + '</b></h5>' +
                '<h6>送信者:&nbsp;' + data.from + '&nbsp;&nbsp;&nbsp;&nbsp;受信者:&nbsp;' + data.to + '<span class="mailbox-read-time pull-right">' + data.sentAt + '</span></h6>' +
                '</div>';
            showMailBodyContent(data);
            showFileAttach(mailAttachmentDiv, files, "show");
        }
    }

    function resetMailContentDetail() {
        var mailSubjectDiv = document.getElementById(mailSubjectDivId);
        var mailAttachmentDiv = document.getElementById(mailAttachmentDivId);
        mailSubjectDiv.innerHTML = "";
        showMailBodyContent({body: ""});
        mailAttachmentDiv.innerHTML = "";
    }

    function showFileAttach(divFileAttachId, files, type){
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
                if(type == "reSend"){
                    filesInnerHTML += "<span class='remove-mail-attachment'>&nbsp;x&nbsp;</span>";
                }
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

    function showMailBodyContent(data) {
        data.body = data.body.replace(/(?:\r\n|\r|\n)/g, '<br />');
        var mailBodyDiv = document.getElementById(mailBodyDivId);
        mailBodyDiv.innerHTML = data.body;
    }

    function showMailEditor(id) {
        var cachedSeparateTab = getCachedSeparateTabSetting();
        if(cachedSeparateTab) {
            showMailEditorInNewTab(id);
        } else {
            showMailEditorInTab(id);
        }
    }

    function showMailEditorInNewTab(id) {
        var data = {
            "id" : id
        };
        sessionStorage.setItem("separateReSendMailData", JSON.stringify(data));
        var win = window.open('/user/reSendNewTab', '_blank');
        if (win) {
            win.focus();
        } else {
            alert('Please allow popups for this website');
        }
    }

    function showMailEditorInTab(id) {
        $('#sendMailModal').modal();
        lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
        getDetailMailHistory(id, function (email, accounts, files) {
            reSendEmail = email;
            updateSenderSelector(email, accounts, function (account) {
                showMailContentToEditor(email, accounts, files);
                $('#' + rdMailSenderId).off('change');
                $('#' + rdMailSenderId).change(function() {
                    showMailContentToEditor(email, accounts, files);
                });
            });
        });

        $("button[name='sendSuggestMailClose']").off('click');
        $('#cancelSendSuggestMail').button('reset');
        $("button[name='sendSuggestMailClose']").click(function () {
            var btn = $('#cancelSendSuggestMail');
            btn.button('loading');
            var attachmentData = getAttachmentData(attachmentDropzone);
            if (attachmentData.upload.length == 0) {
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
                    console.error("ERROR : sendSuggestMail: ", e);
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
                    $('#sendMailModal').modal('hide');
                    if (data && data.status) {
                        //TODO: noti send mail success
                        var payload = getSearchPayload();
                        loadHistoryData(payload);
                        resetMailContentDetail();
                    } else {
                        //TODO: noti send mail failed
                    }

                },
                error: function (e) {
                    btn.button('reset');
                    console.error("ERROR : sendSuggestMail: ", e);
                    $('#sendMailModal').modal('hide');
                    //TODO: noti send mail error
                }
            });
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
            showFileAttach(rdMailAttachment, files, "reSend");
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