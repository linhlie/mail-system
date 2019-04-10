(function () {
    "use strict";
    var sourceTableId = 'sourceMatch';
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailAttachmentDivId = 'mailAttachment';
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBody';
    var rdMailSenderId = 'rdMailSender';
    var rdMailReceiverId = 'rdMailReceiver';
    var rdMailCCId = 'rdMailCC';
    var totalResultContainId = 'totalResultContain';

    var extractFirstBtnId = "extract-first";
    var extractLastBtnId = "extract-last";
    var extractPrevBtnId = "extract-prev";
    var extractNextBtnId = "extract-next";

    var printBtnId = 'printBtn';
    var extractResult = null;
    var sourceMatchDataTable;
    var selectedRowData;

    var isDebug = true;
    var debugMailAddress = "ows-test@world-link-system.com";

    var attachmentDropzoneId = "#reply-dropzone";
    var attachmentDropzone;

    var selectedSourceTableRow;

    var receiverValidate = true;
    var ccValidate = true;

    var externalCCGlobal = [];
    var senderGlobal = "";
    var lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
    var lastReceiver;
    var lastMessageId;

    var currentEmail;
    var emailAccounts = [];
    var REPLY_EMAIL = "reply";

    var markSearchOptions = {
        "element": "mark",
        "className": "mark-search",
        "separateWordSearch": false,
    };

    var rangeMarkOptions = {
        "element": "mark",
        "className": "mark-range",
        "separateWordSearch": false,
        "acrossElements": true,
    };

    var headerOrigin = '<tr>'+
        '<th class="col-xs-1" >金額</th>'+
        '<th class="col-xs-2" >受信日時</th>'+
        '<th class="col-xs-2" >送信者</th>'+
        '<th class="col-xs-4" >件名</th>'+
        '<th class="col-xs-1" ></th>';
    // </tr>

    var headerAlertPartner = '<th class="col-xs-2" style="color: red">取引先アラート</th>';
    var headerAlertPeople = '<th class="col-xs-2" style="color: red">担当アラート</th>';

    var replaceSourceHTML = '<tr role="row" class="hidden">' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="range"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="receivedAt"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '<td class="clickable text-center" name="reply" rowspan="1" colspan="1">' +
        '<button type="button" class="btn btn-xs btn-default">返信</button>' +
        '</td>';
        // '</tr>';

    var replaceAlertBusinessPartnerHTML = '<td name="alertLevelSourceRow" rowspan="1" colspan="1" data="alertLevel" style="text-align: center;">' +
        '<span style="display: inline-block;"></span></td>';

    var replaceAlertPeopleInChargePartnerHTML = '<td name="alertLevelPeopleInChargeSource" rowspan="1" colspan="1" data="peopleInChargeAlertLevel" style="text-align: center;">' +
        '<span style="display: inline-block;"></span></td>';

    $(function () {
        initSearch();
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
        initDropzone();
        getEnvSettings();
        setButtonClickListenter(printBtnId, printPreviewEmail);
        loadExtractData();
        initStickyHeader();
        setButtonClickListenter(extractFirstBtnId, extractFirst);
        setButtonClickListenter(extractLastBtnId, extractLast);
        setButtonClickListenter(extractPrevBtnId, extractPrev);
        setButtonClickListenter(extractNextBtnId, extractNext);
        keyDownListeners();
        previewDraggingSetup();
    });

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

    function getEnvSettings() {
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/user/matchingResult/envSettings",
            cache: false,
            timeout: 600000,
            success: function (data) {
                try {
                    var result = data && data.msg ? JSON.parse(data.msg) : null;
                    if (result) {
                        isDebug = result["debug_on"];
                        debugMailAddress = result["debug_receive_mail_address"];
                    }
                } catch (error) {
                    console.error("getEnvSettings ERROR : ", error);
                }
            },
            error: function (e) {
                console.error("getEnvSettings FAILED : ", e);
            }
        });
    }

    function enableResizeColums() {
        $("#" + sourceTableId).colResizable(
            {
                disable: true,
            }
        );
        if(screen.width > 768){
            $("#" + sourceTableId).colResizable(
                {
                    resizeMode:'overflow',
                    minWidth: 30
                }
            );
        }else{
            $("#" + sourceTableId).colResizable(
                {
                    minWidth: 30
                }
            );
        }
    }

    function loadExtractData() {
        var extractDataStr;
        var key = window.location.href.indexOf("extractSource") >= 0 ? "extractSourceData" : "extractDestinationData";
        if(window.location.href.indexOf("extractEmailStatistic") >= 0){
            key = "extractEmailStatisticData";
        }
        extractDataStr = sessionStorage.getItem(key);
        if (extractDataStr) {
            $('body').loadingModal({
                position: 'auto',
                text: '抽出中...',
                color: '#fff',
                opacity: '0.7',
                backgroundColor: 'rgb(0,0,0)',
                animation: 'doubleBounce',
            });
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/user/submitExtract",
                data: extractDataStr,
                dataType: 'json',
                cache: false,
                timeout: 600000,
                success: function (data) {
                    $('body').loadingModal('hide');
                    if (data && data.status) {
                        extractResult = data.list;
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
        } else {
            updateData();
        }
    }

    function setButtonClickListenter(id, callback) {
        $('#' + id).off('click');
        $('#' + id).click(function () {
            if (typeof callback === "function") {
                callback();
            }
        });
    }

    function updateData() {
        var replaceBody = replaceSourceHTML;
        var replaceHeader = headerOrigin;
        var isAlertpartner = false;
        var isAlertpeople = false;
        for(var i=0;i<extractResult.length;i++){
            if(extractResult[i].alertLevel != null && extractResult[i].alertLevel != ""){
                isAlertpartner = true;
            }
            if(extractResult[i].peopleInChargeAlertLevel != null && extractResult[i].peopleInChargeAlertLevel !=""){
                isAlertpeople = true;
            }
        }

        if(isAlertpartner){
            replaceBody = replaceBody + replaceAlertBusinessPartnerHTML;
            replaceHeader = replaceHeader + headerAlertPartner;
        }

        if(isAlertpeople){
            replaceBody = replaceBody + replaceAlertPeopleInChargePartnerHTML;
            replaceHeader = replaceHeader + headerAlertPeople;
        }
        replaceBody = replaceBody + '</tr>';
        replaceHeader = replaceHeader + '</tr>';
        showSourceData(sourceTableId, extractResult, replaceHeader, replaceBody);
    }

    function showSourceData(tableId, data, replaceHeader, replaceBody) {
        destroySortSource();
        $("#" + tableId + "> thead").html(replaceHeader);
        removeAllRow(tableId, replaceBody);
        if (data.length > 0) {
            var html = replaceBody;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> thead").html(replaceHeader);
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("sourceRow", function () {
                selectedRow($(this).closest('tr'))
            });
            setRowClickListener("reply", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = extractResult[index];
                if (rowData && rowData.messageId) {
                    lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
                    showMailEditor(rowData.messageId, lastSelectedSendMailAccountId, rowData)
                }
            });
            setRowClickListener("alertLevelSourceRow", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = data[index];
                if (rowData && rowData.alertLevel) {
                    $.alert({
                        title: '',
                        content: '' +
                            '<form action="" class="formName">' +
                            '<div class="form-group form-alert">' +
                            '<label>取引先アラート:'+ rowData.alertLevel +'</label>' +
                            '<label>取 引 先 名:' + rowData.partnerName + '</label>' +
                            '<hr>'+
                            '<span>' + rowData.alertContent + '</span>' +
                            '</form>',
                    });
                }
            });
            setRowClickListener("alertLevelPeopleInChargeSource", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = data[index];
                if (rowData && rowData.peopleInChargeAlertLevel) {
                    $.alert({
                        title: '',
                        content: '' +
                            '<form action="" class="formName">' +
                            '<div class="form-group form-alert">' +
                            '<label>取引先アラート:'+ rowData.peopleInChargeAlertLevel +'</label>' +
                            '<label>取 引 先 名:' + rowData.partnerName + '</label>' +
                            '<label>担 当 者 名:' + rowData.peopleInChargeName + '</label>' +
                            '<label>メールアドレス:' + rowData.peopleinChargeEmail + '</label>' +
                            '<hr>'+
                            '<span>' + rowData.peopleInChargeAlertContent + '</span>' +
                            '</form>',
                    });
                }
            });
        }
        updateTotalResult(data.length);
        initSortSource();
        selectFirstRow();
        enableResizeColums();
    }

    function updateTotalResult(total) {
        var raw = $('#' + totalResultContainId).text();
        $('#' + totalResultContainId).text(raw + " " + total + "件")
    }

    function destroySortSource() {
        if (!!sourceMatchDataTable) {
            sourceMatchDataTable.destroy();
        }
    }

    function initSortSource() {
        $("#sourceMatch").tablesorter(
            {
                theme: 'default',
                headers: {
                    4: {
                        sorter: false
                    },
                },
                sortList: [[1, 1], [2, 0]]
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
                        if( (cellData != null && cellData != "") && (cellKeysData === "alertLevel" || cellKeysData === "peopleInChargeAlertLevel" )){
                            cell.setAttribute("Class", "clickable");
                        }
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

    function showSourceMail(index) {
        var rowData = extractResult[index];
        if (rowData && rowData && rowData.messageId) {
            showMail(rowData.messageId, function (result) {
                showMailContent(result);
                updatePreviewMailToPrint(result);
            }, rowData.range);
        }
    }

    function selectFirstRow() {
        if (extractResult && extractResult.length > 0) {
            var firstTr = $('#' + sourceTableId).find(' tbody tr:first');
            selectedRow(firstTr);
        }
    }

    function selectedRow(row) {
        selectedSourceTableRow = row;
        updateSourceControls(row.index(), extractResult.length);
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
        var index = row[0].getAttribute("data");
        var rowData = extractResult[index];
        selectedRowData = rowData;
        showSourceMail(index);
    }

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function showMail(messageId, callback, matchRange) {
        messageId = messageId.replace(/\+/g, '%2B');
        var url = "/user/matchingResult/email?messageId=" + messageId;
        if(matchRange && matchRange.length > 0) {
            url = url + "&matchRange=" + matchRange
        }
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: url,
            cache: false,
            timeout: 600000,
            success: function (data) {
                var result;
                if (data.status) {
                    if (data.list && data.list.length > 0) {
                        result = data.list[0];
                    }
                }
                if (typeof callback === "function") {
                    callback(result);
                }
            },
            error: function (e) {
                console.error("getMail ERROR : ", e);
                if (typeof callback === "function") {
                    callback();
                }
            }
        });
    }

    function showMailContent(data) {
        var mailSubjectDiv = document.getElementById(mailSubjectDivId);
        var mailBodyDiv = document.getElementById(mailBodyDivId);
        var mailAttachmentDiv = document.getElementById(mailAttachmentDivId);
        mailSubjectDiv.innerHTML = "";
        showMailBodyContent({originalBody: ""});
        mailAttachmentDiv.innerHTML = "";
        if (data) {
            mailSubjectDiv.innerHTML = '<div class="mailbox-read-info">' +
                '<h5><b>' + data.subject + '</b></h5>' +
                '<h6>送信者: ' + data.from + '<span class="mailbox-read-time pull-right">' + data.receivedAt + '</span></h6>' +
                '</div>';
            showMailBodyContent(data);
            var files = data.files ? data.files : [];
            showAttachFile(mailAttachmentDiv, files);
        }
    }

    function updatePreviewMailToPrint(data) {
        var printElment = document.getElementById('printElement');
        printElment.innerHTML = "";
        if (data) {
            data.originalBody = wrapText(data.originalBody);
            var innerHtml = '<div class="box-body no-padding">' +
                '<div class="mailbox-read-info">' +
                '<h3>' + data.subject + '</h3>' +
                '<h5>From: ' + data.from + '<span class="mailbox-read-time pull-right">' + data.receivedAt + '</span></h5>' +
                '</div>' +
                '<div class="mailbox-read-message">' + data.originalBody + '</div>' +
                '</div>' +
                '<div class="box-footer"> ' +
                '<ul class="mailbox-attachments clearfix"> ';
            var files = data.files ? data.files : [];
            if (files.length > 0) {
                var filesInnerHTML = "";
                for (var i = 0; i < files.length; i++) {
                    var file = files[i];
                    var fileName = file.fileName;
                    var fileSize = getFileSizeString(file.size);
                    var fileInnerHTML = '<li> <span class="mailbox-attachment-icon">' +
                        '<i class="fa fa-file-o"></i>' +
                        '</span> ' +
                        '<div class="mailbox-attachment-info"> ' +
                        '<a href="#" class="mailbox-attachment-name">' +
                        '<i class="fa fa-paperclip"></i>' + fileName +
                        '</a> ' +
                        '<span class="mailbox-attachment-size">' + fileSize + '<a href="#" class="btn btn-default btn-xs pull-right"><i class="fa fa-cloud-download"></i></a> </span> ' +
                        '</div> ' +
                        '</li>';
                    filesInnerHTML += fileInnerHTML
                }
                innerHtml = innerHtml + filesInnerHTML;
            }
            innerHtml = innerHtml + '</ul></div>';
            printElment.innerHTML = innerHtml;
        }
    }

    function printPreviewEmail() {
        $("#printElement").show();
        $("#printElement").print();
        $("#printElement").hide();
    }

    function showMailBodyContent(data) {
        data.originalBody = wrapText(data.originalBody);
        var mailBodyDiv = document.getElementById(mailBodyDivId);
        mailBodyDiv.scrollTop = 0;
        mailBodyDiv.innerHTML = data.originalBody;
        highlight(data);
    }
    
    function showMailEditor(messageId, accountId, receiver) {
        var cachedSeparateTab = getCachedSeparateTabSetting();
        if(cachedSeparateTab) {
            showMailEditorInNewTab(messageId, accountId, receiver);
        } else {
            showMailEditorInTab(messageId, accountId, receiver);
        }
    }
    
    function showMailEditorInNewTab(messageId, accountId, receiver) {
        var type = window.location.href.indexOf("extractSource") >= 0 ? 6 : 7;
        var data = {
            "type": REPLY_EMAIL,
            "sendTo": type,
            "accountId" : accountId,
            "messageId" : messageId,
            "receiver" : receiver,
            "historyType": getHistoryType(),
        };
        sessionStorage.setItem("separateSendMailData", JSON.stringify(data));
        var win = window.open('/user/sendTab', '_blank');
        if (win) {
            win.focus();
        } else {
            alert('Please allow popups for this website');
        }
    }

    function showMailEditorInTab(messageId, accountId, receiver) {
        $('#sendMailModal').modal();
        lastReceiver = receiver;
        lastMessageId = messageId;
        composeEmail(messageId, receiver);

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
            var btn = $(this);
            btn.button('loading');
            var attachmentData = getAttachmentData(attachmentDropzone);
            var form = {
                messageId: messageId,
                subject: $("#" + rdMailSubjectId).val(),
                receiver: $("#" + rdMailReceiverId).val().replace(/\s*,\s*/g, ","),
                cc: $("#" + rdMailCCId).val().replace(/\s*,\s*/g, ","),
                content: getMailEditorContent(),
                originAttachment: attachmentData.origin,
                uploadAttachment: attachmentData.upload,
                accountId: !!lastSelectedSendMailAccountId ? lastSelectedSendMailAccountId : undefined,
                sendType: getHistoryType(),
                historyType: getHistoryType(),
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

    function composeEmail(messageId, receiver) {
        messageId = messageId.replace(/\+/g, '%2B');
        var receiverStr = receiver.replyTo ? receiver.replyTo : receiver.from;
        var type = window.location.href.indexOf("extractSource") >= 0 ? 6 : 7;
        var url = "/user/matchingResult/replyEmail?messageId=" + messageId + "&type=" + type + "&receiver=" + receiverStr;

        function onSuccess(response) {
            if(response && response.status) {
                currentEmail = response.mail;
                emailAccounts = response.list;
                updateSenderSelector(receiver);
                showMailContentToEditor(receiver)
            }
        }

        function onError(e) {
            console.error("composeEmail ERROR : ", e);
        }
        composeEmailAPI(url, onSuccess, onError);
    }

    function updateSenderSelector(receiver) {
        var accounts = emailAccounts || [];
        $('#' + rdMailSenderId).empty();
        $.each(accounts, function (i, item) {
            $('#' + rdMailSenderId).append($('<option>', {
                value: item.id,
                text : item.account,
                selected: (item.id== lastSelectedSendMailAccountId)
            }));
        });

        $('#' + rdMailSenderId).off('change');
        $('#' + rdMailSenderId).change(function() {
            lastSelectedSendMailAccountId = this.value;
            localStorage.setItem("selectedSendMailAccountId", lastSelectedSendMailAccountId);
            showMailContentToEditor(receiver);
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
            var replyOrigin = data.replyOrigin;
            replyOrigin = replyOrigin ? wrapText(replyOrigin) : replyOrigin;
            replyOrigin = getReplyWrapper(data, replyOrigin);
            var originalBody = replyOrigin ? replyOrigin : "";
            var greeting = sender.greeting == null ? "" : sender.greeting;
            var signature = sender.signature == null ? "" : sender.signature;
            originalBody = greeting + "<br/><br/>" + originalBody + "<br/><br/>"  + signature;
            updateMailEditorContent(originalBody);
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

    function getMailEditorContent() {
        var editor = tinymce.get(rdMailBodyId);
        return editor.getContent();
    }

    function resetValidation() {
        receiverValidate = true;
        ccValidate = true;
        $('#' + rdMailCCId + '-container').removeClass('has-error')
        $('#' + rdMailReceiverId + '-container').removeClass('has-error')
    }

    function highlight(data) {
        data = data || {};
        var highLightRanges = data.highLightRanges || [];
        $("input[type='search']").val("");
        $("#" + mailBodyDivId).unmark({
            done: function() {
                $("#" + mailBodyDivId).mark(highLightRanges, rangeMarkOptions);
            }
        });
    }

    function initSearch() {
        // the input field
        var $input = $("input[type='search']"),
            // clear button
            $clearBtn = $("button[data-search='clear']"),
            // prev button
            $prevBtn = $("button[data-search='prev']"),
            // next button
            $nextBtn = $("button[data-search='next']"),
            // the context where to search
            $content = $("#" + mailBodyDivId),
            // jQuery object to save <mark> elements
            $results,
            // the class that will be appended to the current
            // focused element
            currentClass = "current",
            // the current index of the focused element
            currentIndex = 0;

        $input.keyup(function(event) {
            if (event.keyCode === 10 || event.keyCode === 13)
                event.preventDefault();
        });

        function jumpTo() {
            if ($results.length) {
                var position,
                    $current = $results.eq(currentIndex);
                $results.removeClass(currentClass);
                if ($current.length) {
                    $current.addClass(currentClass);
                    $content.scrollTop($content.scrollTop() + $current.position().top
                        - $content.height()/2 + $current.height()/2);
                }
            }
        }

        $input.on("input", function() {
            var searchVal = this.value;
            $content.unmark(
                Object.assign(
                    {},
                    markSearchOptions,
                    {
                        done: function() {
                            $content.mark(searchVal, Object.assign({},
                                markSearchOptions,
                                {
                                    done: function() {
                                        $results = $content.find("mark.mark-search");
                                        currentIndex = 0;
                                        jumpTo();
                                    }
                                }
                            ));
                        }
                    }
                )
            );
        });

        /**
         * Clears the search
         */
        $clearBtn.on("click", function() {
            $content.unmark(markSearchOptions);
            $input.val("").focus();
        });

        /**
         * Next and previous search jump to
         */
        $nextBtn.add($prevBtn).on("click", function() {
            if ($results.length) {
                currentIndex += $(this).is($prevBtn) ? -1 : 1;
                if (currentIndex < 0) {
                    currentIndex = $results.length - 1;
                }
                if (currentIndex > $results.length - 1) {
                    currentIndex = 0;
                }
                jumpTo();
            }
        });
    }
    
    function getHistoryType() {
        return window.location.href.indexOf("extractSource") >= 0 ? 3 : 4;
    }
    
    function keyDownListeners() {
        $(document).on("keyup", keydownHandler);
    }
    
    function keydownHandler(e) {
        var button = undefined;
        if(e.shiftKey && (e.which || e.keyCode) == 113) {
            e.preventDefault();
            button = $("#" + extractFirstBtnId);
        } else if(e.shiftKey && (e.which || e.keyCode) == 115) {
            e.preventDefault();
            button = $("#" + extractLastBtnId);
        } else if(!e.shiftKey && (e.which || e.keyCode) == 113) {
            e.preventDefault();
            button = $("#" + extractPrevBtnId);
        } else if(!e.shiftKey && (e.which || e.keyCode) == 115) {
            e.preventDefault();
            button = $("#" + extractNextBtnId);
        }
        if(button && !button.is(":disabled")) {
            button.click();
        }
    }

    function extractFirst() {
        var firstTr = $('#' + sourceTableId).find(' tbody tr:first');
        selectedRow(firstTr);
    }

    function extractPrev() {
        if(!selectedSourceTableRow) {
            extractLast();
        } else {
            selectedRow(selectedSourceTableRow.prev());
        }
    }

    function extractNext() {
        if(!selectedSourceTableRow) {
            extractNext();
        } else {
            selectedRow(selectedSourceTableRow.next());
        }
    }

    function extractLast() {
        var lastTr = $('#' + sourceTableId).find(' tbody tr:last');
        selectedRow(lastTr.prev());
    }

    function previewDraggingSetup() {
        var dragging = false;
        $('#dragbar2').mousedown(function(e){
            e.preventDefault();

            dragging = true;
            var dragbar = $('#dragbar2');
            var ghostbar = $('<div>',
                {id:'ghostbar2',
                    css: {
                        width: dragbar.outerWidth(),
                        top: dragbar.offset().top,
                        left: dragbar.offset().left
                    }
                }).appendTo('body');

            $(document).mousemove(function(e){
                ghostbar.css("top",e.pageY);
            });

        });

        $(document).mouseup(function(e){
            if (dragging)
            {
                var container = $('#table-section');
                var topHeight = (e.pageY - container.offset().top);
                var tableHeight = Math.floor(topHeight - 55);
                tableHeight = tableHeight > 60 ? tableHeight : 60;
                tableHeight = tableHeight < 800 ? tableHeight : 800;
                var previewHeightChange = 450 - tableHeight;
                var previewHeight = 444 + previewHeightChange;
                $('.matching-result .table-container').css("height", tableHeight + "px");
                $('.matching-result .mail-body').css("height", previewHeight + "px");
                $('#ghostbar2').remove();
                $(document).unbind('mousemove');
                dragging = false;
            }
        });
    }

})(jQuery);
