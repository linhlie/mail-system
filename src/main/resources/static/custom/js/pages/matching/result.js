
(function () {
    "use strict";
    var sourceTableId = 'sourceMatch';
    var destinationTableId = 'destinationMatch';
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailAttachmentDivId = 'mailAttachment';
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBody';
    var rdMailAttachmentId = 'rdMailAttachment';
    var rdMailSenderId = 'rdMailSender';
    var rdMailReceiverId = 'rdMailReceiver';
    var rdMailCCId = 'rdMailCC';
    var openFileFolderButtonId = '#openFileFolderBtn';
    var printBtnId = 'printBtn';
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
        initDropzone();
        initSortDestination();
        setButtonClickListenter(printBtnId, printPreviewEmail);
        getEnvSettings();
        fixingForTinyMCEOnModal();
        onlyDisplayNonZeroRow = $('#displayNonZeroCheckbox').is(":checked");
        setupDisplatNonZeroListener();
        var matchingConditionStr;
        matchingConditionStr = sessionStorage.getItem("matchingConditionData");
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
    
    function setupDisplatNonZeroListener() {
        $('#displayNonZeroCheckbox').change(function() {
            onlyDisplayNonZeroRow = $(this).is(":checked");
            updateData();
        });
    }
    
    function updateData() {
        showSourceData(sourceTableId, matchingResult);
        disableButton(openFileFolderButtonId, true);
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
                            showMailEditor(rowData.messageId, selectedRowData.source, rowData.matchRange, replaceType)
                        }
                    });
                    setRowClickListener("sendToSaki", function () {
                        var replaceType = $(sakiReplaceSelectorId).val();
                        var row = $(this)[0].parentNode;
                        var index = row.getAttribute("data");
                        var rowData = currentDestinationResult[index];
                        if(rowData){
                            if(selectedRowData && selectedRowData.source && selectedRowData.source.messageId){
                                showMailEditor(selectedRowData.source.messageId, rowData, rowData.range, replaceType)
                            }
                        }
                    });
                }
                updateDestinationDataTrigger();
                $('body').loadingModal('hide');
                enableResizeDestinationColumns();
                updateTotalDestinationMatching(currentDestinationResult.length);
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
            showMail(rowData.source.messageId, function (result) {
                showMailContent(result)
                updatePreviewMailToPrint(result);
            });
        }
    }

    function showDestinationMail() {
        $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = currentDestinationResult[index];
        if(rowData && rowData.messageId){
            showMail(rowData.messageId, function (result) {
                showMailContent(result);
                updatePreviewMailToPrint(result);
            });
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
                showMail(rowData.source.messageId, function (result) {
                    showMailContent(result)
                    updatePreviewMailToPrint(result);
                });
            }
            selectedRowData = rowData;
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

    function showMailWithReplacedRange(messageId, replyId, range, replaceType, callback) {
        messageId = messageId.replace(/\+/g, '%2B');
        replyId = replyId.replace(/\+/g, '%2B');
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/user/matchingResult/editEmail?messageId=" + messageId + "&replyId=" + replyId + "&range=" + range + "&replaceType=" + replaceType,
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
        // console.log("showMailContent: ", data);
        var mailSubjectDiv = document.getElementById(mailSubjectDivId);
        var mailBodyDiv = document.getElementById(mailBodyDivId);
        var mailAttachmentDiv = document.getElementById(mailAttachmentDivId);
        mailSubjectDiv.innerHTML = "";
        mailBodyDiv.innerHTML = "";
        // updateMailEditorContent("");
        mailAttachmentDiv.innerHTML = "";
        if(data){
            mailSubjectDiv.innerHTML = '<div class="mailbox-read-info">' +
                '<h5><b>' + data.subject + '</b></h5>' +
            '<h6>送信者: ' + data.from + '<span class="mailbox-read-time pull-right">' + data.receivedAt + '</span></h6>' +
            '</div>';
            data.originalBody = data.originalBody.replace(/(?:\r\n|\r|\n)/g, '<br />');
            mailBodyDiv.innerHTML = data.originalBody;
            // updateMailEditorContent(data.originalBody);
            var files = data.files ? data.files : [];
            if(files.length > 0){
                var filesInnerHTML = "";
                for(var i = 0; i < files.length; i++ ){
                    var file = files[i];
                    if(i > 0){
                        filesInnerHTML += "<br/>";
                    }
                    filesInnerHTML += ("<a href='/user/download?path=" + encodeURIComponent(file.storagePath) + "&fileName=" + file.fileName + "' download>" + file.fileName + "(" + getFileSizeString(file.size) + "); </a>")
                }
                mailAttachmentDiv.innerHTML = filesInnerHTML;
                disableButton(openFileFolderButtonId, false);
            } else {
                mailAttachmentDiv.innerHTML = "添付ファイルなし";
                disableButton(openFileFolderButtonId, true);
            }
        } else {
            disableButton(openFileFolderButtonId, true);
        }
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
            data.originalBody = data.originalBody.replace(/(?:\r\n|\r|\n)/g, '<br />');
            data.replacedBody = data.replacedBody ? data.replacedBody.replace(/(?:\r\n|\r|\n)/g, '<br />') : data.replacedBody;
            data.originalBody = data.replyOrigin ? data.originalBody + data.replyOrigin : data.originalBody;
            data.originalBody = data.originalBody + data.signature;
            updateMailEditorContent(data.originalBody);
            if( data.replacedBody != null){
                data.replacedBody = data.replyOrigin ? data.replacedBody + data.replyOrigin : data.replacedBody;
                data.replacedBody = data.replacedBody + data.signature;
                updateMailEditorContent(data.replacedBody, true);
            }
            var files = data.files ? data.files : [];
            updateDropzoneData(files);
        }
    }

    function updateDropzoneData(files) {
        attachmentDropzone.removeAllFiles(true);
        if(files.length > 0){
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
    
    function showMailEditor(messageId, receiver, textRange, replaceType) {
        $('#sendMailModal').modal();
        showMailWithReplacedRange(messageId, receiver.messageId, textRange, replaceType, function (result) {
            showMailContenttToEditor(result, receiver.from)
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

    function printPreviewEmail() {
        $("#printElement").show();
        $("#printElement").print();
        $("#printElement").hide();
    }

    function updateTotalSourceMatching(total) {
        $('#' + totalSourceMatchingContainId).text("絞り込み元: " + total + "件")
    }

    function updateTotalDestinationMatching(total) {
        $('#' + totalDestinationMatchingContainId).text("絞り込み先: " + total + "件")
    }

})(jQuery);
