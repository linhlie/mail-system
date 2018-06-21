(function () {
    "use strict";
    var sourceTableId = 'sourceMatch';
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailPreviewId = 'previewBody';
    var mailAttachmentDivId = 'mailAttachment';
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBody';
    var rdMailSenderId = 'rdMailSender';
    var rdMailReceiverId = 'rdMailReceiver';
    var rdMailCCId = 'rdMailCC';
    var totalResultContainId = 'totalResultContain';
    var printBtnId = 'printBtn';
    var extractResult = null;
    var sourceMatchDataTable;
    var selectedRowData;

    var isDebug = true;
    var debugMailAddress = "ows-test@world-link-system.com";

    var attachmentDropzoneId = "#reply-dropzone";
    var attachmentDropzone;

    var receiverValidate = true;
    var ccValidate = true;

    var externalCCGlobal = [];
    var senderGlobal = "";

    var markOptions = {
        "element": "mark",
        "separateWordSearch": false,
    };

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
        '<td class="clickable fit" name="sourceRow" rowspan="1" colspan="1" data="range"><span></span></td>' +
        '<td class="clickable fit" name="sourceRow" rowspan="1" colspan="1" data="receivedAt"><span></span></td>' +
        '<td class="clickable fit" name="sourceRow" rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '<td class="clickable text-center fit" name="reply" rowspan="1" colspan="1">' +
        '<button type="button" class="btn btn-xs btn-default">返信</button>' +
        '</td>' +
        '</tr>';

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
    });

    function getEmailDomain(email) {
        if(typeof email === "string"  && email.indexOf("@") >= 0) {
            return email.split("@")[1]
        }
        return "";
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

    function removeFile(fileId) {
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
                resizeMode: 'overflow',
            }
        );
    }

    function loadExtractData() {
        var extractDataStr;
        var key = window.location.href.indexOf("extractSource") >= 0 ? "extractSourceData" : "extractDestinationData";
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
        showSourceData(sourceTableId, extractResult);
    }

    function showSourceData(tableId, data) {
        destroySortSource();
        removeAllRow(tableId, replaceSourceHTML);
        if (data.length > 0) {
            var html = replaceSourceHTML;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("sourceRow", selectedRow);
            setRowClickListener("reply", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = extractResult[index];
                if (rowData && rowData.messageId) {
                    console.log("reply: ", rowData);
                    showMailEditor(rowData.messageId, rowData)
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

    function showSourceMail() {
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
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
            firstTr.addClass('highlight-selected').siblings().removeClass('highlight-selected');
            var index = firstTr[0].getAttribute("data");
            var rowData = extractResult[index];
            if (rowData && rowData.messageId) {
                showMail(rowData.messageId, function (result) {
                    showMailContent(result);
                    updatePreviewMailToPrint(result);
                }, rowData.range);
            }
            selectedRowData = rowData;
        }
    }

    function selectedRow() {
        $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
        showSourceMail.call(this);
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = extractResult[index];
        selectedRowData = rowData;
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
            if (files.length > 0) {
                var filesInnerHTML = "";
                for (var i = 0; i < files.length; i++) {
                    var file = files[i];
                    var fileExtension = getFileExtension(file.fileName);
                    var command = extensionCommands[fileExtension];
                    command = (isWindows() && !!command) ? command : "nope";
                    var url = window.location.origin + "/download/" + encodeURIComponent(file.digest) + "/" + file.fileName;
                    if (i > 0) {
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

    function getFileSizeString(fileSize) {
        return fileSize >= 1000 ? (Math.round( (fileSize/1000) * 10 ) / 10) + " KB " : fileSize + " B"
    }

    function updatePreviewMailToPrint(data) {
        var printElment = document.getElementById('printElement');
        printElment.innerHTML = "";
        if (data) {
            data.originalBody = data.originalBody.replace(/(?:\r\n|\r|\n)/g, '<br />');
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

    function showMailEditor(messageId, receiver) {
        $('#sendMailModal').modal();
        showMailWithReplacedRange(messageId, function (result) {
            showMailContenttToEditor(result, receiver)
        });
        $("button[name='sendSuggestMailClose']").off('click');
        $('#cancelSendSuggestMail').button('reset');
        $("button[name='sendSuggestMailClose']").click(function () {
            var btn = $('#cancelSendSuggestMail');
            btn.button('loading');
            var attachmentData = getAttachmentData();
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
            receiverValidate = validateaNDsHOWEmailListInput(rdMailReceiverId, false);
            ccValidate = validateaNDsHOWEmailListInput(rdMailCCId, true);
            if(!(receiverValidate && ccValidate)) return;
            var btn = $(this);
            btn.button('loading');
            var attachmentData = getAttachmentData();
            var form = {
                messageId: messageId,
                subject: $("#" + rdMailSubjectId).val(),
                receiver: $("#" + rdMailReceiverId).val().replace(/\s*,\s*/g, ","),
                cc: $("#" + rdMailCCId).val().replace(/\s*,\s*/g, ","),
                content: getMailEditorContent(),
                originAttachment: attachmentData.origin,
                uploadAttachment: attachmentData.upload,
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

    function showMailWithReplacedRange(messageId, callback) {
        messageId = messageId.replace(/\+/g, '%2B');
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/user/matchingResult/replyEmail?messageId=" + messageId,
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
                console.error("showMailWithReplacedRange ERROR : ", e);
                if (typeof callback === "function") {
                    callback();
                }
            }
        });
    }

    function showMailBodyContent(data) {
        data.originalBody = data.originalBody.replace(/(?:\r\n|\r|\n)/g, '<br />');
        var mailBodyDiv = document.getElementById(mailBodyDivId);
        mailBodyDiv.innerHTML = data.originalBody;
        highlight(data);
    }

    function showMailContenttToEditor(data, receiverData) {
        var receiverListStr = receiverData.replyTo ? receiverData.replyTo : receiverData.from;
        resetValidation();
        document.getElementById(rdMailReceiverId).value = receiverListStr;
        updateMailEditorContent("");
        if (data) {
            document.getElementById(rdMailSenderId).value = data.account;
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
            data.replyOrigin = data.replyOrigin ? data.replyOrigin.replace(/(?:\r\n|\r|\n)/g, '<br />') : data.replyOrigin;
            data.originalBody = data.replyOrigin ? data.replyOrigin : "";
            data.originalBody = getExcerptWithGreeting(data.excerpt) + data.originalBody;
            data.originalBody = data.originalBody + data.signature;
            updateMailEditorContent(data.originalBody);
        }
        updateDropzoneData();
    }

    function updateCCList(currentCCs, newCCs) {
        for (var i = 0; i < newCCs.length; i++) {
            if (currentCCs.indexOf(newCCs[i]) == -1) {
                currentCCs.push(newCCs[i]);
            }
        }
        return currentCCs;
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

    function updateDropzoneData() {
        attachmentDropzone.removeAllFiles(true);
    }

    function getAttachmentData() {
        var result = {
            origin: [],
            upload: []
        };
        for (var i = 0; i < attachmentDropzone.files.length; i++) {
            var file = attachmentDropzone.files[i];
            if (!!file.id) {
                if (!!file.upload) {
                    result.upload.push(file.id);
                } else {
                    result.origin.push(file.id);
                }
            }
        }
        return result;
    }

    function setInputChangeListener(id, acceptEmpty, callback) {
        $('#' + id).on('input', function () {
            var valid = validateEmailListInput(id);
            if (!acceptEmpty) {
                var value = $('#' + id).val();
                valid = valid && (value.length > 0);
            }
            valid ? $('#' + id + '-container').removeClass('has-error') : $('#' + id + '-container').addClass('has-error');
            if (typeof callback === "function") {
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
        if (cc.length === 1 && cc[0] == "") {
            senderValid = true;
        } else {
            for (var i = 0; i < cc.length; i++) {
                var email = cc[i];
                var valid = validateEmail(email);
                if (!valid) {
                    senderValid = false;
                    break;
                }
            }
        }
        return senderValid;
    }

    function validateEmail(email) {
        var regex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return regex.test(String(email).toLowerCase());
    }

    function disableButton(btnId, disabled) {
        $('#' + btnId).prop("disabled", disabled);
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

    function getExcerptWithGreeting(excerpt) {
        var greeting = loadGreeting("返");
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

})(jQuery);
