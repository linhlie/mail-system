
(function () {
    "use strict";
    var sourceTableId = 'sourceMatch';
    var destinationTableId = 'destinationMatch';
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailPreviewId = 'previewBody';
    var mailAttachmentDivId = 'mailAttachment';
    var sakiPreviewContainerId = 'saki-preview-container';
    var mailSakiSubjectDivId = 'mailSakiSubject';
    var mailSakiBodyDivId = 'mailSakiBody';
    var mailSakiAttachmentDivId = 'mailSakiAttachment';
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBody';
    var rdMailAttachmentId = 'rdMailAttachment';
    var rdMailSenderId = 'rdMailSender';
    var rdMailReceiverId = 'rdMailReceiver';
    var rdMailCCId = 'rdMailCC';
    var printBtnId = 'printBtn';
    var printElementId = 'printElement';
    var printSakiBtnId = 'printSakiBtn';
    var printSakiElementId = 'printSakiElement';
    var matchingResult = null;
    var mailList = {};
    var currentDestinationResult = [];
    var onlyDisplayNonZeroRow = true;
    var sourceMatchDataTable;
    var destinationMatchDataTable;
    var selectedRowData;
    var motoReplaceSelectorId = "#motoReplaceSelector";
    var sakiReplaceSelectorId = "#sakiReplaceSelector";
    var totalSourceMatchingContainId = "totalSourceMatching";
    var totalDestinationMatchingContainId = "totalDestinationMatching";

    var attachmentDropzoneId = "#attachment-dropzone";
    var attachmentDropzone;

    var isDebug = true;
    var debugMailAddress = "ows-test@world-link-system.com";

    var receiverValidate = true;
    var ccValidate = true;

    var externalCCGlobal = [];
    var senderGlobal = "";
    var lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
    var lastReceiver;
    var lastMessageId;
    var lastTextRange;
    var lastTextMatchRange;
    var lastReplaceType;
    var lastSendTo;

    var spaceEffective = false;
    var distinguish = false;

    var markOptions = {
        "element": "mark",
        "separateWordSearch": false,
        "acrossElements": false,
    };

    var invisibleMarkOptions = {
        "element": "mark",
        "className": "mark-invisible",
        "separateWordSearch": false,
        "acrossElements": true,
    };

    var rangeMarkOptions = {
        "element": "mark",
        "className": "mark-range",
        "separateWordSearch": false,
        "acrossElements": true,
    };

    var markSearchOptions = {
        "element": "mark",
        "className": "mark-search",
        "separateWordSearch": false,
        "acrossElements": true,
    };

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

    var replaceSourceHTML = '<tr role="row" class="hidden">' +
        '<td class="clickable fit" name="sourceRow" rowspan="1" colspan="1" data="word"><span></span></td>' +
        '<td class="clickable fit" name="sourceRow" rowspan="1" colspan="1" data="destinationList"><span></span></td>' +
        '<td class="clickable fit" name="sourceRow" rowspan="1" colspan="1" data="source.receivedAt"><span></span></td>' +
        '<td class="clickable fit" name="sourceRow" rowspan="1" colspan="1" data="source.from"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="source.subject"><span></span></td>' +
        '</tr>';

    var replaceDestinationHTML = '<tr role="row" class="hidden">' +
        '<td class="clickable fit" name="showDestinationMail" rowspan="1" colspan="1" data="word"><span></span></td>' +
        '<td class="clickable fit" name="showDestinationMail" rowspan="1" colspan="1" data="range"><span></span></td>' +
        '<td class="clickable fit" name="showDestinationMail" rowspan="1" colspan="1" data="matchRange"><span></span></td>' +
        '<td class="clickable fit" name="showDestinationMail" rowspan="1" colspan="1" data="receivedAt"><span></span></td>' +
        '<td class="clickable fit" name="showDestinationMail" rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td class="clickable" name="showDestinationMail" rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '<td class="clickable text-center fit" name="sendToMoto" rowspan="1" colspan="1">' +
        '<button type="button" class="btn btn-xs btn-default">元へ</button>' +
        '</td>' +
        '<td class="clickable text-center fit" name="sendToSaki" rowspan="1" colspan="1">' +
        '<button type="button" class="btn btn-xs btn-default">先へ</button>' +
        '</td>' +
        '</tr>';

    $(function () {
        previewDraggingSetup();
        initSearch(mailBodyDivId, "moto");
        initSearch(mailSakiBodyDivId, "saki");
        initReplaceSelector();
        initDropzone();
        initSortDestination();
        setButtonClickListenter(printBtnId, function () {
            printPreviewEmail(printElementId);
        });
        setButtonClickListenter(printSakiBtnId, function () {
            printPreviewEmail(printSakiElementId);
        });
        getEnvSettings();
        fixingForTinyMCEOnModal();
        var matchingConditionStr;
        matchingConditionStr = sessionStorage.getItem("matchingConditionData");
        var spaceEffectiveStr = sessionStorage.getItem("spaceEffective");
        var distinguishStr = sessionStorage.getItem("distinguish");
        spaceEffective = spaceEffectiveStr ? !!JSON.parse(spaceEffectiveStr) : false;
        distinguish = distinguishStr ? !!JSON.parse(distinguishStr) : false;
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
        if(matchingConditionStr){
            $('body').loadingModal({
                position: 'auto',
                text: 'マッチング中...',
                color: '#fff',
                opacity: '0.7',
                backgroundColor: 'rgb(0,0,0)',
                animation: 'doubleBounce',
            });
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/user/matchingSettings/submitForm",
                data: matchingConditionStr,
                dataType: 'json',
                cache: false,
                timeout: 600000,
                success: function (data) {
                    $('body').loadingModal('hide');
                    if(data && data.status){
                        matchingResult = data.list;
                        mailList = data.mailList || {};
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
        initStickyHeader();
    });

    function getEmailDomain(email) {
        if(typeof email === "string"  && email.indexOf("@") >= 0) {
            return email.split("@")[1]
        }
        return "";
    }

    function initReplaceSelector() {
        var motoSelectedValue = localStorage.getItem("motoSelectedValue");
        var sakiSelectedValue = localStorage.getItem("sakiSelectedValue");
        if(!!motoSelectedValue) {
            $(motoReplaceSelectorId).val(motoSelectedValue);
        }
        if(!!sakiSelectedValue) {
            $(sakiReplaceSelectorId).val(sakiSelectedValue);
        }
        $(motoReplaceSelectorId).change(function() {
            localStorage.setItem("motoSelectedValue", $(motoReplaceSelectorId).val());
        });
        $(sakiReplaceSelectorId).change(function() {
            localStorage.setItem("sakiSelectedValue", $(sakiReplaceSelectorId).val());
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

    function removeFile(fileId){
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/removeUploadedFile?fileId=" + fileId,
            cache: false,
            timeout: 600000,
            success: function (data) {
                console.log("removeFile SUCCESS : ", data);
            },
            error: function (e) {
                console.error("removeFile ERROR : ", e);
            }
        });
    }
    
    function getAttachmentData() {
        var result = {
            origin: [],
            upload: []
        };
        for(var i = 0; i < attachmentDropzone.files.length; i++){
            var file = attachmentDropzone.files[i];
            if(!!file.id){
                if(!!file.upload){
                    result.upload.push(file.id);
                } else {
                    result.origin.push(file.id);
                }
            }
        }
        return result;
    }
    
    function enableResizeSourceColumns() {
        enableResizeColums(sourceTableId);
    }

    function enableResizeDestinationColumns() {
        enableResizeColums(destinationTableId);
    }

    function enableResizeColums(tableId) {
        $("#" + tableId).colResizable(
            {
                disable: true,
            }
        );
        $("#" + tableId).colResizable(
            {
                resizeMode:'overflow'
            }
        );
    }

    function setButtonClickListenter(id, callback) {
        $('#' + id).off('click');
        $('#' + id).click(function () {
            if(typeof callback === "function"){
                callback();
            }
        });
    }

    function fixingForTinyMCEOnModal() {
        $(document).on('focusin', function(e) {
            if ($(e.target).closest(".mce-window").length) {
                e.stopImmediatePropagation();
            }
        });
    }
    
    function updateData() {
        showSourceData(sourceTableId, matchingResult);
    }

    function showSourceData(tableId, data) {
        removeAllRow(tableId, replaceSourceHTML);
        var sourceMatchingCounter = 0;
        if(data.length > 0){
            var html = replaceSourceHTML;
            for(var i = 0; i < data.length; i ++){
                if(onlyDisplayNonZeroRow && data[i]
                    && data[i].destinationList && data[i].destinationList.length == 0){
                    continue;
                }
                var messageId = data[i].source.messageId;
                data[i].source = Object.assign(data[i].source, mailList[messageId]);
                html = html + addRowWithData(tableId, data[i], i);
                sourceMatchingCounter++;
            }
            $("#"+ tableId + "> tbody").html(html);
            setRowClickListener("sourceRow", selectedRow);
        }
        initSortSource();
        selectFirstRow();
        enableResizeSourceColumns();
        updateTotalSourceMatching(sourceMatchingCounter);
    }
    
    function initSortSource() {
        $("#sourceMatch").tablesorter(
            {
                theme : 'default',
                sortList: [[2,1], [3,0]]
            });

    }
    
    function showDestinationData(tableId, data) {
        setTimeout(function () {
            $('body').loadingModal('destroy');
            $('body').loadingModal({
                position: 'auto',
                text: 'Loading...',
                color: '#fff',
                opacity: '0.7',
                backgroundColor: 'rgb(0,0,0)',
                animation: 'doubleBounce',
            });
            var word = data.word;
            var source = data.source;
            currentDestinationResult = data.destinationList;
            removeAllRow(tableId, replaceDestinationHTML);
            setTimeout(function () {
                if(currentDestinationResult.length > 0){
                    var html = replaceDestinationHTML;
                    for(var i = 0; i < currentDestinationResult.length; i++){
                        currentDestinationResult[i].word = word;
                        var messageId = currentDestinationResult[i].messageId;
                        currentDestinationResult[i] = Object.assign(currentDestinationResult[i], mailList[messageId]);
                        html = html + addRowWithData(tableId, currentDestinationResult[i], i);
                    }
                    $("#"+ tableId + "> tbody").html(html);
                    setRowClickListener("showDestinationMail", showDestinationMail);
                    setRowClickListener("sendToMoto", function () {
                        var replaceType = $(motoReplaceSelectorId).val();
                        var row = $(this)[0].parentNode;
                        var index = row.getAttribute("data");
                        var rowData = currentDestinationResult[index];
                        if(rowData && rowData.messageId){
                            lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
                            showMailEditor(lastSelectedSendMailAccountId, rowData.messageId, selectedRowData.source, rowData.matchRange, rowData.range, replaceType, "moto")
                        }
                    });
                    setRowClickListener("sendToSaki", function () {
                        var replaceType = $(sakiReplaceSelectorId).val();
                        var row = $(this)[0].parentNode;
                        var index = row.getAttribute("data");
                        var rowData = currentDestinationResult[index];
                        if(rowData){
                            if(selectedRowData && selectedRowData.source && selectedRowData.source.messageId){
                                lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
                                showMailEditor(lastSelectedSendMailAccountId, selectedRowData.source.messageId, rowData, rowData.range, rowData.matchRange, replaceType, "saki")
                            }
                        }
                    });
                }
                updateDestinationDataTrigger();
                $('body').loadingModal('hide');
                enableResizeDestinationColumns();
                updateTotalDestinationMatching(currentDestinationResult.length);
                $("#"+ tableId).closest('.table-container-wrapper').scrollTop(0);
            }, 10)
        }, 10)
    }

    function destroySortDestination() {
        if(!!destinationMatchDataTable){
            destinationMatchDataTable.destroy();
        }
    }

    function initSortDestination() {
        $("#destinationMatch").tablesorter(
            {
                theme : 'default',
                headers: {
                    6: {
                        sorter: false
                    },
                    7: {
                        sorter: false
                    }
                },
                sortList: [[3,1], [4,0]]
            });
    }
    
    function updateDestinationDataTrigger() {
        $("#destinationMatch").trigger("updateAll", [ true, function () {
            
        } ]);
    }

    function addRowWithData(tableId, data, index) {
        var table = document.getElementById(tableId);
        if(!table) return "";
        var rowToClone = table.rows[1];
        var row = rowToClone.cloneNode(true);
        row.setAttribute("data", index);
        row.className = undefined;
        var cells = row.cells;
        for(var i = 0; i < cells.length; i++){
            var cell = cells.item(i);
            var cellKeysData = cell.getAttribute("data");
            if(!cellKeysData || cellKeysData.length == 0) continue;
            var cellKeys = cellKeysData.split(".");
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if(cellNode){
                if(cellNode.nodeName == "SPAN") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    if(Array.isArray(cellData)){
                        cellNode.textContent = cellData.length;
                    } else {
                        cellNode.textContent = cellData;
                    }
                }
            }
        }
        return row.outerHTML;
        // body.appendChild(row);
    }
    
    function setRowClickListener(name, callback) {
        $("td[name='"+name+"']").off('click');
        $("td[name='"+name+"']").click(function () {
            if(typeof callback == "function"){
                callback.apply(this);
            }
        })
    }
    
    function showSourceMail() {
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = matchingResult[index];
        if(rowData && rowData.source && rowData.source.messageId){
            $('#' + sakiPreviewContainerId).hide();
            showMail(rowData.source.messageId, rowData.word, function (result) {
                showMailContent(result, [mailSubjectDivId, mailBodyDivId, mailAttachmentDivId]);
                updatePreviewMailToPrint(result, printElementId);
            });
        }
    }

    function showDestinationMail() {
        $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = currentDestinationResult[index];
        if(rowData && rowData.messageId){
            showMail(rowData.messageId, rowData.word, function (result) {
                showMailContent(result, [mailSakiSubjectDivId, mailSakiBodyDivId, mailSakiAttachmentDivId]);
                updatePreviewMailToPrint(result, printSakiElementId);
                $('#' + sakiPreviewContainerId).show();
            }, rowData.matchRange);
        }
    }
    
    function selectFirstRow() {
        if(matchingResult && matchingResult.length > 0){
            var firstTr = $('#' + sourceTableId).find(' tbody tr:first');
            if(!firstTr[0]) return;
            var index = firstTr[0].getAttribute("data");
            if(index == null){
                $('#' + sourceTableId).find(' tbody tr:first').remove();
                selectFirstRow();
                return;
            }
            firstTr.addClass('highlight-selected').siblings().removeClass('highlight-selected');
            var rowData = matchingResult[index];
            if(rowData && rowData.source && rowData.source.messageId){
                showMail(rowData.source.messageId, rowData.word, function (result) {
                    showMailContent(result, [mailSubjectDivId, mailBodyDivId, mailAttachmentDivId]);
                    updatePreviewMailToPrint(result, printElementId);
                });
            }
            selectedRowData = rowData;
            $('#' + sakiPreviewContainerId).hide();
            showDestinationData(destinationTableId, rowData);
        }
    }
    
    function selectedRow() {
        $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
        showSourceMail.call(this);
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = matchingResult[index];
        selectedRowData = rowData;
        showDestinationData(destinationTableId, rowData);
    }

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#"+ tableId + "> tbody").html(replaceHtml);
    }
    
    function showMail(messageId, highlightWord, callback, matchRange) {
        messageId = messageId.replace(/\+/g, '%2B');
        var url = "/user/matchingResult/email?messageId=" + messageId;
        if(highlightWord && highlightWord.length > 0) {
            highlightWord = highlightWord.replace(/\+/g, '%2B');
            url = url + "&highlightWord=" + highlightWord
                + "&spaceEffective=" + spaceEffective
                + "&distinguish=" + distinguish;
        }
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
                if(data.status){
                    if(data.list && data.list.length > 0){
                        result = data.list[0];
                    }
                }
                if(typeof callback === "function"){
                    callback(result);
                }
            },
            error: function (e) {
                console.error("getMail ERROR : ", e);
                if(typeof callback === "function"){
                    callback();
                }
            }
        });
    }

    function showMailWithReplacedRange(accountId, messageId, replyId, range, matchRange, replaceType, sendTo, callback) {
        messageId = messageId.replace(/\+/g, '%2B');
        replyId = replyId.replace(/\+/g, '%2B');
        var url = "/user/matchingResult/editEmail?messageId=" + messageId + "&replyId=" + replyId + "&range=" + range + "&matchRange=" + matchRange + "&replaceType=" + replaceType;
        var type = sendTo === "moto" ? 4 : 5
        url = url + "&type=" + type;
        if(!!accountId){
            url = url + "&accountId=" + accountId;
        }
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: url,
            cache: false,
            timeout: 600000,
            success: function (data) {
                var email;
                var accounts;
                if(data.status){
                    email = data.mail;
                    accounts = data.list;
                }
                if(typeof callback === "function"){
                    callback(email, accounts);
                }
            },
            error: function (e) {
                console.error("getMail ERROR : ", e);
                if(typeof callback === "function"){
                    callback();
                }
            }
        });
    }

    function showMailContent(data, elementIds) {
        var mailSubjectDiv = document.getElementById(elementIds[0]);
        var mailAttachmentDiv = document.getElementById(elementIds[2]);
        mailSubjectDiv.innerHTML = "";
        showMailBodyContent(elementIds[1], {originalBody: ""});
        mailAttachmentDiv.innerHTML = "";
        if(data){
            mailSubjectDiv.innerHTML = '<div class="mailbox-read-info">' +
                '<h5><b>' + data.subject + '</b></h5>' +
            '<h6>送信者: ' + data.from + '<span class="mailbox-read-time pull-right">' + data.receivedAt + '</span></h6>' +
            '</div>';
            showMailBodyContent(elementIds[1], data);
            var files = data.files ? data.files : [];
            if(files.length > 0){
                var filesInnerHTML = "";
                for(var i = 0; i < files.length; i++ ){
                    var file = files[i];
                    var fileExtension = getFileExtension(file.fileName);
                    var command = extensionCommands[fileExtension];
                    command = (isWindows() && !!command) ? command : "nope";
                    var url = window.location.origin + "/download/" + encodeURIComponent(file.digest) + "/" + file.fileName;
                    if(i > 0){
                        filesInnerHTML += "<br/>";
                    }
                    filesInnerHTML += ("<button type='button' class='btn btn-link download-link' data-filename='" + file.fileName + "' data-command='" + command + "' data-download='" + url + "'>" + file.fileName + "(" + getFileSizeString(file.size) + "); </button>")
                }
                mailAttachmentDiv.innerHTML = filesInnerHTML;
                setDownloadLinkClickListener();
            } else {
                mailAttachmentDiv.innerHTML = "添付ファイルなし";
            }
        }
    }
    
    function showMailBodyContent(id, data) {
        data.originalBody = wrapText(data.originalBody);
        var mailBodyDiv = document.getElementById(id);
        mailBodyDiv.scrollTop = 0;
        mailBodyDiv.innerHTML = data.originalBody;
        highlight(id, data);
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
            updateDropzoneData(files);
        }
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

    function updateCCList(currentCCs, newCCs) {
        for (var i = 0; i < newCCs.length; i++) {
            if (currentCCs.indexOf(newCCs[i]) == -1) {
                currentCCs.push(newCCs[i]);
            }
        }
        return currentCCs;
    }

    function updateDropzoneData(files) {
        var cachedIncludeAttachmentStr = localStorage.getItem("includeAttachment");
        var cachedIncludeAttachment = typeof cachedIncludeAttachmentStr !== "string" ? false : !!JSON.parse(cachedIncludeAttachmentStr);
        attachmentDropzone.removeAllFiles(true);
        if(cachedIncludeAttachment && files.length > 0){
            for(var i = 0; i < files.length; i++ ){
                var file = files[i];
                var mockFile = { id: file.id, name: file.fileName, size: file.size, type: 'text/plain'};
                attachmentDropzone.emit("addedfile", mockFile);
                attachmentDropzone.emit("processing", mockFile);
                attachmentDropzone.emit("success", mockFile);
                attachmentDropzone.emit("complete", mockFile);
                attachmentDropzone.files.push( mockFile );
            }
        }
    }

    function updateMailEditorContent(content, preventClear){
        var editor = tinymce.get(rdMailBodyId);
        editor.setContent(content);
        if(!preventClear){
            editor.undoManager.clear();
        }
        editor.undoManager.add();
    }
    
    function getMailEditorContent() {
        var editor = tinymce.get(rdMailBodyId);
        return editor.getContent();
    }

    function disableButton(buttonId, disabled) {
        if(buttonId && buttonId.length > 0){
            $(buttonId).prop("disabled", disabled);
        }
    }
    
    function showMailEditor(accountId, messageId, receiver, textRange, textMatchRange, replaceType, sendTo) {
        $('#sendMailModal').modal();
        if(sendTo === "moto") {
            $('#sendSuggestMailTitle').text("マッチング【元】へメール送信");
        } else {
            $('#sendSuggestMailTitle').text("マッチング【先】へメール送信");
        }
        lastMessageId = messageId;
        lastReceiver = receiver;
        lastTextRange = textRange;
        lastTextMatchRange = textMatchRange;
        lastReplaceType = replaceType;
        lastSendTo = sendTo;
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
            var attachmentData = getAttachmentData();
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
            receiverValidate = validateaNDsHOWEmailListInput(rdMailReceiverId, false);
            ccValidate = validateaNDsHOWEmailListInput(rdMailCCId, true);
            if(!(receiverValidate && ccValidate)) return;
            var btn = $(this);
            btn.button('loading');
            var attachmentData = getAttachmentData();
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
                    if(result){
                        isDebug = result["debug_on"];
                        debugMailAddress = result["debug_receive_mail_address"];
                    }
                } catch (error){
                    console.error("getEnvSettings ERROR : ", error);
                }
            },
            error: function (e) {
                console.error("getEnvSettings FAILED : ", e);
            }
        });
    }

    function updatePreviewMailToPrint(data, printElmentId) {
        var printElment = document.getElementById(printElmentId);
        printElment.innerHTML = "";
        if(data){
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
            if(files.length > 0){
                var filesInnerHTML = "";
                for(var i = 0; i < files.length; i++ ){
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

    function getFileSizeString(fileSize) {
        return fileSize >= 1000 ? (Math.round( (fileSize/1000) * 10 ) / 10) + " KB " : fileSize + " B"
    }

    function printPreviewEmail(id) {
        $("#" + id).show();
        $("#" + id).print();
        $("#" + id).hide();
    }

    function updateTotalSourceMatching(total) {
        $('#' + totalSourceMatchingContainId).text("絞り込み元: " + total + "件")
    }

    function updateTotalDestinationMatching(total) {
        $('#' + totalDestinationMatchingContainId).text("絞り込み先: " + total + "件")
    }

    function setInputChangeListener(id, acceptEmpty, callback) {
        $('#' + id).on('input', function() {
            var valid = validateEmailListInput(id);
            if(!acceptEmpty) {
                var value = $('#' + id).val();
                valid = valid && (value.length > 0);
            }
            valid ? $('#' + id + '-container').removeClass('has-error') : $('#' + id + '-container').addClass('has-error');
            if(typeof callback === "function") {
                callback(valid);
            }
        });
    }

    function validateaNDsHOWEmailListInput(id, acceptEmpty) {
        var valid = validateEmailListInput(id);
        if (!acceptEmpty) {
            var value = $('#' + id).val();
            valid = valid && (value.length > 0);
        }
        valid ? $('#' + id + '-container').removeClass('has-error') : $('#' + id + '-container').addClass('has-error');
        return valid;
    }

    function validateEmailListInput(id) {
        var rawCC = $('#' + id).val();
        rawCC = rawCC || "";
        var ccText = rawCC.replace(/\s*,\s*/g, ",");
        var cc = ccText.split(",");
        var senderValid = true;
        if(cc.length === 1 && cc[0] == "") {
            senderValid = true;
        } else {
            for(var i = 0; i < cc.length; i++) {
                var email = cc[i];
                var valid = validateEmail(email);
                if(!valid) {
                    senderValid = false;
                    break;
                }
            }
        }
        return senderValid;
    }

    function validateEmail(email) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }

    function resetValidation() {
        receiverValidate = true;
        ccValidate = true;
        $('#' + rdMailCCId + '-container').removeClass('has-error')
        $('#' + rdMailReceiverId + '-container').removeClass('has-error')
    }

    
    function highlight(id, data) {
        data = data || {};
        var highLightWords = data.highLightWords || [];
        highLightWords = buildHighlightWordNonSpace(highLightWords);
        var highLightWordRegexs = buildHighlightWordRegex(highLightWords);
        var excludeWords = data.excludeWords || [];
        excludeWords = buildHighlightWordNonSpace(excludeWords);
        var excludeWordRegexs = buildHighlightWordRegex(excludeWords);
        var highLightRanges = data.highLightRanges || [];
        $("input[data-search='"+ id +"']").val("");
        $("#" + id).unmark({
            done: function() {
                for(var i = 0; i < highLightWordRegexs.length; i++){
                    $("#" + id).markRegExp(highLightWordRegexs[i], markOptions);
                }
                for(var y = 0; y < excludeWordRegexs.length; y++){
                    $("#" + id).markRegExp(excludeWordRegexs[y], invisibleMarkOptions);
                }
                $("#" + id).mark(highLightRanges, rangeMarkOptions);
            }
        });
    }
    
    function buildHighlightWordNonSpace(highLightWords) {
        var highLightWordsExtended = [];
        for(var i = 0; i < highLightWords.length; i++) {
            var highLightWord = highLightWords[i];
            // highLightWordsExtended.push(highLightWord);
            var highLightWordNonSpace = highLightWord.replace(/ /g,'');
            if(highLightWordsExtended.indexOf(highLightWordNonSpace) == -1) {
                highLightWordsExtended.push(highLightWordNonSpace);
            }
        }
        return highLightWordsExtended;
    }
    
    function buildHighlightWordRegex(highLightWords) {
        var regexs = [];
        for(var i = 0; i < highLightWords.length; i++){
            var highLightWord = highLightWords[i];
            if(highLightWord && highLightWord.length > 0) {
                var regex = new RegExp(highLightWord.split("").join("\\s*"), "gmi");
                regexs.push(regex);
            }
        }
        return regexs;
    }

    function initSearch(id, type) {
        // the input field
        var $input = $("input[data-search='"+ id +"']"),
            // clear button
            $clearBtn = $("button[data-search='" + type + "-clear']"),
            // prev button
            $prevBtn = $("button[data-search='" + type + "-prev']"),
            // next button
            $nextBtn = $("button[data-search='" + type + "-next']"),
            // the context where to search
            $content = $("#" + id),
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

    function getFileExtension(fileName) {
        var parts = fileName.split(".");
        var extension = "." + parts[(parts.length - 1)];
        return extension.toLowerCase();
    }

    function getOS() {
        var userAgent = window.navigator.userAgent,
            platform = window.navigator.platform,
            macosPlatforms = ['Macintosh', 'MacIntel', 'MacPPC', 'Mac68K'],
            windowsPlatforms = ['Win32', 'Win64', 'Windows', 'WinCE'],
            iosPlatforms = ['iPhone', 'iPad', 'iPod'],
            os = null;

        if (macosPlatforms.indexOf(platform) !== -1) {
            os = 'Mac OS';
        } else if (iosPlatforms.indexOf(platform) !== -1) {
            os = 'iOS';
        } else if (windowsPlatforms.indexOf(platform) !== -1) {
            os = 'Windows';
        } else if (/Android/.test(userAgent)) {
            os = 'Android';
        } else if (!os && /Linux/.test(platform)) {
            os = 'Linux';
        }

        return os;
    }
    
    function isWindows() {
        var os = getOS();
        return (os === "Windows");
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
    
    function previewDraggingSetup() {
        var i = 0;
        var dragging = false;
        $('#dragbar').mousedown(function(e){
            e.preventDefault();

            dragging = true;
            var main = $('#saki-preview-wrapper');
            var dragbar = $('#dragbar');
            var ghostbar = $('<div>',
                {id:'ghostbar',
                    css: {
                        height: dragbar.outerHeight(),
                        top: dragbar.offset().top,
                        left: dragbar.offset().left
                    }
                }).appendTo('body');

            $(document).mousemove(function(e){
                ghostbar.css("left",e.pageX+2);
            });

        });

        $(document).mouseup(function(e){
            if (dragging)
            {
                var container = $('#preview-section');
                var leftWidth = (e.pageX - container.offset().left);
                leftWidth = leftWidth <= (container.width()/4) ? 3 : leftWidth;
                if(leftWidth == 3) {
                    var keeperHeight = $('#saki-preview-content-wrapper').outerHeight();
                    keeperHeight = keeperHeight > 0 ? keeperHeight : 600;
                    console.log("keeperHeight: ", keeperHeight);
                    $('#moto-preview-content-keeper').css("height", keeperHeight + "px");
                    $('#moto-preview-content-keeper').show();
                    $('#moto-preview-content-wrapper').hide();
                } else {
                    console.log("hide keeperHeight: ");
                    $('#moto-preview-content-keeper').hide();
                    $('#moto-preview-content-wrapper').show();
                }
                var percentage = (leftWidth / container.width()) * 100;
                percentage = percentage > 75 ? 100 : percentage;
                var mainPercentage = 100-percentage;
                console.log("percentage: ", percentage, mainPercentage);
                if(mainPercentage == 0) {
                    $('#saki-preview-content-wrapper').hide();
                } else {
                    $('#saki-preview-content-wrapper').show();
                }

                $('#moto-preview-wrapper').css("width",percentage + "%");
                $('#saki-preview-wrapper').css("width",mainPercentage + "%");
                $('#ghostbar').remove();
                $(document).unbind('mousemove');
                dragging = false;
            }
        });
    }

    function initStickyHeader() {
        $(".table-container-wrapper").scroll(function () {
            $(this).find("thead.sticky-header")
                .css({
                    "user-select": "none",
                    "position": "relative",
                    "z-index": "10",
                    "transform": "translate(0px, " + $(this).scrollTop() + "px)"
                });
        });
    }
    
    function getDecorateExcerpt(excerpt, sendTo) {
        if(sendTo === "moto") {
            excerpt = getExcerptWithGreeting(excerpt, "元");
            excerpt = '<div class="gmail_extra"><span style="color: #ff0000;">【送り先は】マッチング元へ送信</span></div>' + excerpt;
        } else if (sendTo === "saki") {
            excerpt = getExcerptWithGreeting(excerpt, "先");
            excerpt = '<div class="gmail_extra"><span style="color: #ff0000;">【送り先は】マッチング先へ送信</span></div>' + excerpt;
        }
        return excerpt;
    }

    function getExcerptWithGreeting(excerpt, type) {
        var greeting = loadGreeting(type);
        if(greeting == null) {
            greeting = getExceprtLine("---------------------");
            greeting = greeting + '<br /><br /><br /><br /><br />';
        } else {
            greeting = '<br /><br />' + greeting + '<br /><br />';
        }
        excerpt = excerpt + greeting;
        return excerpt;
    }

    function wrapperWithRed(text) {
        return '<div class="gmail_extra" style="color: #ff0000;">' + text + '</div>';
    }

    function getExceprtLine(line) {
        var exceprtLine = '<div class="gmail_extra"><span style="color: #ff0000;">' + line + '</span></div>';
        return exceprtLine;
    }

    function loadGreeting(type) {
        var greeting = null;
        var greetingData = loadGreetingData();
        for(var i = 0; i < greetingData.length; i++){
            var item = greetingData[i];
            if(item && item.type === type) {
                greeting = item.greeting;
                break;
            }
        }
        return greeting
    }

    function loadGreetingData() {
        var greetingDataInStr = localStorage.getItem("greetingData");
        var greetingData = greetingDataInStr == null ? [] : JSON.parse(greetingDataInStr);
        greetingData = Array.isArray(greetingData) ? greetingData : [];
        return greetingData;
    }

    function wrapText(text) {
        return isHTML(text) ? text : wrapPlainText(text);
    }

    function isHTML(str) {
        return str && (str.toLowerCase().indexOf("<html") > -1);
        // var doc = new DOMParser().parseFromString(str, "text/html");
        // return Array.from(doc.body.childNodes).some(node => node.nodeType === 1);
    }

    function wrapPlainText(text) {
        if(text)
            return text.replace(/(?:\r\n|\r|\n)/g, '<br />');
        return text;
    }

    function getReplyWrapper(data) {
        var wrapperText = '<div class="gmail_extra"><br>' +
            '<div class="gmail_quote">' +
            data.replySentAt +
            ' <span dir="ltr">&lt;<a href="mailto:' +
            data.replyFrom +
            '" target="_blank" rel="noopener">' +
            data.replyFrom +
            '</a>&gt;</span>:<br />' +
            '<blockquote class="gmail_quote" style="margin: 0 0 0 .8ex; border-left: 1px #ccc solid; padding-left: 1ex;">' +
            '<div dir="ltr">' + data.replyOrigin + '</div></blockquote></div></div>';
        return wrapperText;
    }

})(jQuery);
