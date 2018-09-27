(function () {
    "use strict";
    var engineerTableId = 'enginnerTable';
    var destinationTableId = 'destinationMatch';
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailPreviewId = 'previewBody';
    var mailAttachmentDivId = 'mailAttachment';
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBody';
    var rdMailAttachmentId = 'rdMailAttachment';
    var rdMailSenderId = 'rdMailSender';
    var rdMailReceiverId = 'rdMailReceiver';
    var rdMailCCId = 'rdMailCC';
    var printBtnId = 'printBtn';
    var printElementId = 'printElement';
    var matchingResult = null;
    var mailList = {};
    var currentDestinationResult = [];
    var onlyDisplayNonZeroRow = true;
    var destinationMatchDataTable;
    var selectedRowData;
    var totalSourceMatchingContainId = "totalSourceMatching";
    
    var engineerNameId = "#engineerNameSelect";
    var partnerNameId = "#partnerNameSelect";

    var attachmentDropzoneId = "#attachment-dropzone";
    var attachmentDropzone;

    var selectedEngineerTableRow;
    var selectedDestinationTableRow;
    
    var matchingConditionEmailMatchingEngineerKey = "matchingConditionData-email-matching-engineer";
    var distinguishEmailMatchingEngineerKey = "distinguish-email-matching-engineer";
    var spaceEffectiveEmailMatchingEngineerKey = "spaceEffective-email-matching-engineer";

    
    var engineerFirstBtnId = "engineer-first";
    var engineerLastBtnId = "engineer-last";
    var engineerPrevBtnId = "engineer-prev";
    var engineerNextBtnId = "engineer-next";
    var emailFirstBtnId = "email-first";
    var emailLastBtnId = "email-last";
    var emailPrevBtnId = "email-prev";
    var emailNextBtnId = "email-next";

    var originalContentWrapId = "ows-mail-body";

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

    var dataLinesConfirm;

    var standardEscape = ["+", "?", "(", ")", "{", "}", "|"];

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

    var replaceEngineerHTML = '<tr role="row" class="hidden">'+
        '<td class="clickable" name="sourceRow" data="engineerMatchingDTO.name"><span></span></td>'+
        '<td class="clickable" name="sourceRow" data="engineerMatchingDTO.partnerName"><span></span></td>'+
        '<td align="center" class="clickable fit" name="sourceRow" data="engineerMatchingDTO.active">'+
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>'+
        '<td align="center" class="clickable fit" name="sourceRow" data="engineerMatchingDTO.autoExtend">'+
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>'+
        '<td align="center" class="clickable fit" name="sourceRow" data="engineerMatchingDTO.dormant">'+
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>' +
    	'</tr>';
    

    var replaceDestinationHTML = '<tr role="row" class="hidden">' +
        '<td class="clickable fit" name="showDestinationMail" rowspan="1" colspan="1" data="matchRange"><span></span></td>' +
        '<td class="clickable fit" name="showDestinationMail" rowspan="1" colspan="1" data="receivedAt"><span></span></td>' +
        '<td class="clickable fit" name="showDestinationMail" rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td class="clickable" name="showDestinationMail" rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '<td class="clickable text-center fit" name="reply" rowspan="1" colspan="1">' +
        '<button type="button" class="btn btn-xs btn-default">返信</button>' +
        '</td>' +
        '</tr>';

    $(function () {
        previewDraggingSetup2();
        initSearch(mailBodyDivId, "moto");
        initDropzone();
        initSortDestination();
        setButtonClickListenter(printBtnId, function () {
            printPreviewEmail(printElementId);
        });
        setButtonClickListenter(emailFirstBtnId, emailMoveToFirst);
        setButtonClickListenter(emailLastBtnId, emailMoveToLast);
        setButtonClickListenter(emailPrevBtnId, emailMoveToPrev);
        setButtonClickListenter(emailNextBtnId, emailMoveToNext);
        setButtonClickListenter(engineerFirstBtnId, engineerMoveToFirst);
        setButtonClickListenter(engineerLastBtnId, engineerMoveToLast);
        setButtonClickListenter(engineerPrevBtnId, engineerMoveToPrev);
        setButtonClickListenter(engineerNextBtnId, engineerMoveToNext);

        getEnvSettings();
        fixingForTinyMCEOnModal();
        var matchingConditionStr;
        matchingConditionStr = sessionStorage.getItem(matchingConditionEmailMatchingEngineerKey);
        var spaceEffectiveStr = sessionStorage.getItem(spaceEffectiveEmailMatchingEngineerKey);
        var distinguishStr = sessionStorage.getItem(distinguishEmailMatchingEngineerKey);
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
                url: "/user/emailMatchingEngineer/submitForm",
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
        $(document).on("keyup", keydownHandler);
    });

    function keydownHandler(e) {
        var button = undefined;
        if(e.shiftKey && (e.which || e.keyCode) == 113) {
            e.preventDefault();
            button = $("#" + engineerFirstBtnId);
        } else if(e.shiftKey && (e.which || e.keyCode) == 115) {
            e.preventDefault();
            button = $("#" + engineerLastBtnId);
        } else if(!e.shiftKey && (e.which || e.keyCode) == 113) {
            e.preventDefault();
            button = $("#" + engineerPrevBtnId);
        } else if(!e.shiftKey && (e.which || e.keyCode) == 115) {
            e.preventDefault();
            button = $("#" + engineerNextBtnId);
        } else if(e.shiftKey && (e.which || e.keyCode) == 119) {
            e.preventDefault();
            button = $("#" + emailFirstBtnId);
        } else if(e.shiftKey && (e.which || e.keyCode) == 120) {
            e.preventDefault();
            button = $("#" + emailLastBtnId);
        } else if(!e.shiftKey && (e.which || e.keyCode) == 119) {
            e.preventDefault();
            button = $("#" + emailPrevBtnId);
        } else if(!e.shiftKey && (e.which || e.keyCode) == 120) {
            e.preventDefault();
            button = $("#" + emailNextBtnId);
        }
        if(button && !button.is(":disabled")) {
            button.click();
        }
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
    
    function enableResizeSourceColumns() {
        enableResizeColums(engineerTableId);
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
        showSourceData(engineerTableId, matchingResult);
    }

    function showSourceData(tableId, data) {
        removeAllRow(tableId, replaceEngineerHTML);
        var sourceMatchingCounter = 0;
        if(data.length > 0){
            var html = replaceEngineerHTML;
            for(var i = 0; i < data.length; i ++){
                if(onlyDisplayNonZeroRow && data[i]
                    && data[i].listEmailDTO && data[i].listEmailDTO.length == 0){
                    continue;
                }
                html = html + addRowWithData(tableId, data[i], i);
                sourceMatchingCounter++;
            }
            $("#"+ tableId + "> tbody").html(html);
            setRowClickListener("sourceRow", function () {
                selectedRow($(this).closest('tr'))
            });
        }
        initSortSource();
        selectFirstRow();
        enableResizeSourceColumns();
        updateTotalSourceMatching(sourceMatchingCounter);
    }
    
    function initSortSource() {
        $("#enginnerTable").tablesorter(
            {
                theme : 'default',
                sortList: [[2,1], [3,0]]
            })
            .bind("sortEnd",function() {
                console.log(index+"  "+total);
            	var index = selectedEngineerTableRow ? selectedEngineerTableRow.index() : -1;
                var total = matchingResult ? matchingResult.length : 0;
                updateEngineerControls(index, total);
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
            currentDestinationResult = data.listEmailDTO;
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
                    setRowClickListener("showDestinationMail", function () {
                        showDestinationMail($(this).closest('tr'))
                    });
                    setRowClickListener("reply", function () {
                    	console.log("reply");
                        var row = $(this)[0].parentNode;
                        var index = row.getAttribute("data");
                        var rowData = currentDestinationResult[index];
                        if (rowData && rowData.messageId) {
                            showMailEditor(rowData.messageId, lastSelectedSendMailAccountId, rowData);
                        }
                    });
                }
                updateDestinationDataTrigger();
                $('body').loadingModal('hide');
                enableResizeDestinationColumns();
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
        $("#"+destinationTableId).tablesorter(
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
            })
            .bind("sortEnd",function() {
                var index = selectedDestinationTableRow ? selectedDestinationTableRow.index() : -1;
                var total = currentDestinationResult ? currentDestinationResult.length : 0;
                updateEmailControls(index, total);
            });
    }
    
    function updateDestinationDataTrigger() {
        $("#"+destinationTableId).trigger("updateAll", [ true, function () {
            
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
            	if(cellNode.nodeName == "IMG") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    cellNode.className = !!cellData ? undefined : cellNode.className;
                }
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
    
    function showDestinationMail(row) {
        selectedDestinationTableRow = row;
        updateEmailControls(row.index(), currentDestinationResult.length);
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
        var index = row[0].getAttribute("data");
        var rowData = currentDestinationResult[index];
        if(rowData && rowData.messageId){
            showMail(rowData.messageId, rowData.word, function (result) {
                showMailContent(result, [mailSubjectDivId, mailBodyDivId, mailAttachmentDivId]);
                updatePreviewMailToPrint(result, printElementId);
            }, rowData.matchRange);
        }
    }
    
    function selectFirstRow() {
        if(matchingResult && matchingResult.length > 0){
            var firstTr = $('#' + engineerTableId).find(' tbody tr:first');
            if(!firstTr[0]) return;
            var index = firstTr[0].getAttribute("data");
            if(index == null){
                $('#' + engineerTableId).find(' tbody tr:first').remove();
                selectFirstRow();
                return;
            }
            selectedRow(firstTr);
        }
    }
    
    function selectedRow(row) {
        selectedEngineerTableRow = row;
        updateEngineerControls(row.index(), matchingResult.length);
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
        var index = row[0].getAttribute("data");
        var rowData = matchingResult[index];
        selectedRowData = rowData;
        setEngineerInfo(rowData);
        showDestinationData(destinationTableId, rowData);
    }
    
    function setEngineerInfo(rowData){
    	var engineer = rowData.engineerMatchingDTO;
    	if(engineer != null){
        	$(engineerNameId).val(engineer.name);
        	$(partnerNameId).val(engineer.partnerName);
    	}
    }

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#"+ tableId + "> tbody").html(replaceHtml);
    }
    
    function showMail(messageId, listHighlightWord, callback, matchRange) {
        messageId = messageId.replace(/\+/g, '%2B');
        var url = "/user/matchingResult/email?messageId=" + messageId;
        if(selectedRowData){
            listHighlightWord = selectedRowData.listMatchingWord;
        }
    	if(listHighlightWord != null){
    		for(var i=0;i<listHighlightWord.length;i++){
    			var highlightWord = listHighlightWord[i];
    			if(highlightWord && highlightWord.length > 0) {
    	            highlightWord = highlightWord.replace(/\+/g, '%2B');
    	            url = url + "&highlightWord=" + highlightWord
    	                + "&spaceEffective=" + spaceEffective
    	                + "&distinguish=" + distinguish;
    	        }
    		}
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
    
    function showMailEditor(messageId, accountId, receiver) {
        var cachedSeparateTab = getCachedSeparateTabSetting();
        if(cachedSeparateTab) {
            showMailEditorInNewTab(messageId, accountId, receiver);
        } else {
            showMailEditorInTab(messageId, accountId, receiver);
        }
    }
    
    function showMailEditorInNewTab(messageId, accountId, receiver) {
        var data = {
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
        showMailWithReplacedRangeNew(messageId, accountId, function (email, accounts) {
            showMailContentToEditor(email, accounts, receiver)
        });
        $('#' + rdMailSenderId).off('change');
        $('#' + rdMailSenderId).change(function() {
            lastSelectedSendMailAccountId = this.value;
            showMailWithReplacedRangeNew(lastMessageId, this.value, function (email, accounts) {
                showMailContentToEditor(email, accounts, lastReceiver)
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
                messageId: messageId,
                subject: $("#" + rdMailSubjectId).val(),
                receiver: $("#" + rdMailReceiverId).val().replace(/\s*,\s*/g, ","),
                cc: $("#" + rdMailCCId).val().replace(/\s*,\s*/g, ","),
                content: getMailEditorContent(),
                originAttachment: attachmentData.origin,
                uploadAttachment: attachmentData.upload,
                accountId: !!lastSelectedSendMailAccountId ? lastSelectedSendMailAccountId : undefined,
                sendType: "[返信]",
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
        try {
            for(var i = 0; i < highLightWords.length; i++){
                var highLightWord = highLightWords[i];
                if(highLightWord && highLightWord.length > 0) {
                    var parts = getHighlightWordParts(highLightWord);
                    var regex = new RegExp(parts.join("\\s*"), "gmi");
                    regexs.push(regex);
                }
            }
        } catch (e) {
            console.error("[ERR] buildHighlightWordRegex: ", e);
        }
        return regexs;
    }

    function getHighlightWordParts(word) {
        var parts = word.split("");
        var result = [];
        for(var i = 0; i < parts.length; i++) {
            var part = word[i];
            if(standardEscape.indexOf(word[i]) > -1) {
                 part = "\\" + part;
            }
            result.push(part);
        }
        return result;
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
    
    function previewDraggingSetup2() {
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
                var tableHeight = Math.floor((topHeight - 78) / 2);
                tableHeight = tableHeight > 60 ? tableHeight : 60;
                tableHeight = tableHeight < 420 ? tableHeight : 420;
                var previewHeightChange = 500 - tableHeight * 2;
                var previewHeight = 444 + previewHeightChange;
                $('.matching-result .table-container').css("height", tableHeight + "px");
                $('.matching-result .mail-body').css("height", previewHeight + "px");
                $('#ghostbar2').remove();
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

    function wrapperWithRed(text) {
        return '<div class="gmail_extra" style="color: #ff0000;">' + text + '</div>';
    }

    function emailMoveToFirst() {
        var firstTr = $('#' + destinationTableId).find(' tbody tr:first');
        showDestinationMail(firstTr);
    }

    function emailMoveToPrev() {
        if(!selectedDestinationTableRow) {
            emailMoveToLast();
        } else {
            showDestinationMail(selectedDestinationTableRow.prev());
        }
    }

    function emailMoveToNext() {
        if(!selectedDestinationTableRow) {
            emailMoveToFirst();
        } else {
            showDestinationMail(selectedDestinationTableRow.next());
        }
    }

    function emailMoveToLast() {
        var lastTr = $('#' + destinationTableId).find(' tbody tr:last');
        showDestinationMail(lastTr.prev());
    }
    
    function engineerMoveToFirst() {
        var firstTr = $('#' + engineerTableId).find(' tbody tr:first');
        selectedRow(firstTr);
    }

    function engineerMoveToPrev() {
        if(!selectedEngineerTableRow) {
            sourceLast();
        } else {
            selectedRow(selectedEngineerTableRow.prev());
        }
    }

    function engineerMoveToNext() {
        if(!selectedEngineerTableRow) {
            sourceNext();
        } else {
            selectedRow(selectedEngineerTableRow.next());
        }
    }

    function engineerMoveToLast() {
        var lastTr = $('#' + engineerTableId).find(' tbody tr:last');
        selectedRow(lastTr.prev());
    }


    function getHistoryType() {
        return lastSendTo === "moto" ? 1 : 2;
    }
    
    function showMailWithReplacedRangeNew(messageId, accountId, callback) {
        messageId = messageId.replace(/\+/g, '%2B');
        var type = window.location.href.indexOf("extractSource") >= 0 ? 6 : 7;
        var url = "/user/matchingResult/replyEmail?messageId=" + messageId + "&type=" + type;
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
                if (data.status) {
                    email = data.mail;
                    accounts = data.list;
                }
                if (typeof callback === "function") {
                    callback(email, accounts);
                }
            },
            error: function (e) {
                console.error("showMailWithReplacedRange ERROR : ", e);
                if (typeof callback === "function") {
                    callback();
                }
            }
        });
    }
    
    function showMailContentToEditor(data, accounts, receiverData, engineer) {
        var receiverListStr = receiverData.replyTo ? receiverData.replyTo : receiverData.from;
        getInforPartner(receiverListStr, function(partnerInfor){
        	showMailContentToEditorFinal(data, accounts, receiverData, engineer, partnerInfor);
        });
    }
    
    function showMailContentToEditorFinal(data, accounts, receiverData, engineer, partnerInfor) {
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
    
    function getInforPartner(sentTo, callback){
        function onSuccess(response) {
            if(response) {
                if(typeof callback == 'function'){
                	callback(response.msg);
                }
            }
        }
        function onError() {
        	if(typeof callback == 'function'){
            	callback();
            }
        }

        getInforPartnerAPI(sentTo, onSuccess, onError);
    }

})(jQuery);
