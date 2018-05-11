
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
    var printBtnId = 'printBtn';
    var extractResult = null;
    var sourceMatchDataTable;
    var selectedRowData;

    var isDebug = true;
    var debugMailAddress = "ows-test@world-link-system.com";

    var attachmentDropzoneId = "#reply-dropzone";
    var attachmentDropzone;

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
        initDropzone();
        getEnvSettings();
        setButtonClickListenter(printBtnId, printPreviewEmail);
        loadExtractData();
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

    function enableResizeColums() {
        $("#" + sourceTableId).colResizable(
            {
                resizeMode:'overflow',
            }
        );
    }

    function loadExtractData() {
        var extractDataStr;
        var key = window.location.href.indexOf("extractSource") >=0 ? "extractSourceData" : "extractDestinationData";
        extractDataStr = sessionStorage.getItem(key);
        if(extractDataStr){
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
                    if(data && data.status){
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
            if(typeof callback === "function"){
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
        if(data.length > 0){
            var html = replaceSourceHTML;
            for(var i = 0; i < data.length; i ++){
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#"+ tableId + "> tbody").html(html);
            setRowClickListener("sourceRow", selectedRow);
            setRowClickListener("reply", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = extractResult[index];
                if(rowData && rowData.messageId){
                    console.log("reply: ", rowData);
                    showMailEditor(rowData.messageId, rowData.from)
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
        if(!!sourceMatchDataTable){
            sourceMatchDataTable.destroy();
        }
    }

    function initSortSource() {
        $("#sourceMatch").tablesorter(
            {
                theme : 'default',
                headers: {
                    4: {
                        sorter: false
                    },
                },
                sortList: [[1,1], [2,0]]
            });
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
        var rowData = extractResult[index];
        if(rowData && rowData && rowData.messageId){
            showMail(rowData.messageId, function (result) {
                showMailContent(result);
                updatePreviewMailToPrint(result);
            });
        }
    }

    function selectFirstRow() {
        if(extractResult && extractResult.length > 0){
            var firstTr = $('#' + sourceTableId).find(' tbody tr:first');
            firstTr.addClass('highlight-selected').siblings().removeClass('highlight-selected');
            var index = firstTr[0].getAttribute("data");
            var rowData = extractResult[index];
            if(rowData && rowData.messageId){
                showMail(rowData.messageId, function (result) {
                    showMailContent(result);
                    updatePreviewMailToPrint(result);
                });
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
        $("#"+ tableId + "> tbody").html(replaceHtml);
    }

    function showMail(messageId, callback) {
        messageId = messageId.replace(/\+/g, '%2B');
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/user/matchingResult/email?messageId=" + messageId,
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

    function showMailContent(data) {
        var mailSubjectDiv = document.getElementById(mailSubjectDivId);
        var mailBodyDiv = document.getElementById(mailBodyDivId);
        var mailAttachmentDiv = document.getElementById(mailAttachmentDivId);
        mailSubjectDiv.innerHTML = "";
        mailBodyDiv.innerHTML = "";
        mailAttachmentDiv.innerHTML = "";
        if(data){
            mailSubjectDiv.innerHTML = '<div class="mailbox-read-info">' +
                '<h5><b>' + data.subject + '</b></h5>' +
                '<h6>送信者: ' + data.from + '<span class="mailbox-read-time pull-right">' + data.receivedAt + '</span></h6>' +
                '</div>';
            data.originalBody = data.originalBody.replace(/(?:\r\n|\r|\n)/g, '<br />');
            mailBodyDiv.innerHTML = data.originalBody;
            var files = data.files ? data.files : [];
            if(files.length > 0){
                var filesInnerHTML = "";
                for(var i = 0; i < files.length; i++ ){
                    var file = files[i];
                    if(i > 0){
                        filesInnerHTML += "<br/>";
                    }
                    filesInnerHTML += ("<a href='/user/download?path=" + encodeURIComponent(file.storagePath) + "&fileName=" + file.fileName + "' download>" + file.fileName + "(" + (file.size/1024) + "KB); </a>")
                }
                mailAttachmentDiv.innerHTML = filesInnerHTML;
            } else {
                mailAttachmentDiv.innerHTML = "添付ファイルなし";
            }
        }
    }
    
    function updatePreviewMailToPrint(data) {
        var printElment = document.getElementById('printElement');
        printElment.innerHTML = "";
        if(data){
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
            if(files.length > 0){
                var filesInnerHTML = "";
                for(var i = 0; i < files.length; i++ ){
                    var file = files[i];
                    var fileName = file.fileName;
                    var fileSize = (file.size/1024) + " KB ";
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
            //TODO: receiver and cc input value validation, split, match Email regex;
            var btn = $(this);
            btn.button('loading');
            var attachmentData = getAttachmentData();
            var form = {
                messageId: messageId,
                subject: $( "#" + rdMailSubjectId).val(),
                receiver: $( "#" + rdMailReceiverId).val(),
                cc: $( "#" + rdMailCCId).val(),
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
                console.error("showMailWithReplacedRange ERROR : ", e);
                if(typeof callback === "function"){
                    callback();
                }
            }
        });
    }

    function showMailContenttToEditor(data, receiver) {
        receiver = isDebug ? debugMailAddress : receiver;
        document.getElementById(rdMailReceiverId).value = receiver;
        updateMailEditorContent("");
        if(data){
            document.getElementById(rdMailSenderId).value = data.account;
            var cc = data.to.split(", ");
            // var index = cc.indexOf(data.account);
            // if(index > -1){
            //     cc.splice(index, 1)
            // }
            document.getElementById(rdMailCCId).value = cc.join(", ");
            document.getElementById(rdMailSubjectId).value = data.subject;
            data.originalBody = data.replyOrigin ? data.replyOrigin : "";
            data.originalBody = data.originalBody + data.signature;
            updateMailEditorContent(data.originalBody);
        }
        updateDropzoneData();
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

    function updateDropzoneData() {
        attachmentDropzone.removeAllFiles(true);
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

})(jQuery);
