
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
    var openFileFolderButtonId = '#openFileFolderBtn';
    var matchingResult = null;
    var currentDestinationResult = [];
    var onlyDisplayNonZeroRow = true;
    var sourceMatchDataTable;
    var destinationMatchDataTable;
    var selectedRowData;
    var motoReplaceSelectorId = "#motoReplaceSelector";
    var sakiReplaceSelectorId = "#sakiReplaceSelector";

    var isDebug = true;
    var debugMailAddress = "ows-test@world-link-system.com";

    $(function () {
        getEnvSettings();
        fixingForTinyMCEOnModal()
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
        destroySortSource();
        removeAllRow(tableId);
        if(data.length > 0){
            for(var i = 0; i < data.length; i ++){
                if(onlyDisplayNonZeroRow && data[i]
                    && data[i].destinationList && data[i].destinationList.length == 0){
                    continue;
                }
                addRowWithData(tableId, data[i], i, function () {
                    setRowClickListener("sourceRow", selectedRow);
                });
            }
        }
        initSortSource();
        selectFirstRow();
    }

    function destroySortSource() {
        if(!!sourceMatchDataTable){
            sourceMatchDataTable.destroy();
        }
    }
    
    function initSortSource() {
        sourceMatchDataTable = $("#sourceMatch").DataTable({
            "bPaginate": false,
            "bFilter": false,
            "bInfo": false,
            "order": [[ 2, "desc" ]]
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
            destroySortDestination();
            removeAllRow(tableId);
            if(currentDestinationResult.length > 0){
                console.log("start currentDestinationResult");
                for(var i = 0; i < currentDestinationResult.length; i++){
                    var index = i;
                    setTimeout(function () {
                        currentDestinationResult[index].word = word;
                        addRowWithData(tableId, currentDestinationResult[index], index, function () {
                            setRowClickListener("showDestinationMail", showDestinationMail);
                        });
                    }, 5 * index);
                }
                setTimeout(function () {
                    setRowClickListener("sendToMoto", function () {
                        var replaceType = $(motoReplaceSelectorId).val();
                        var row = $(this)[0].parentNode;
                        var index = row.getAttribute("data");
                        var rowData = currentDestinationResult[index];
                        if(rowData && rowData.messageId){
                            showMailEditor(rowData.messageId, selectedRowData.source.from, rowData.matchRange, replaceType)
                        }
                    });
                    setRowClickListener("sendToSaki", function () {
                        var replaceType = $(sakiReplaceSelectorId).val();
                        var row = $(this)[0].parentNode;
                        var index = row.getAttribute("data");
                        var rowData = currentDestinationResult[index];
                        if(rowData){
                            if(selectedRowData && selectedRowData.source && selectedRowData.source.messageId){
                                showMailEditor(selectedRowData.source.messageId, rowData.from, rowData.range, replaceType)
                            }
                        }
                    });
                    initSortDestination();
                    $('body').loadingModal('hide');
                }, (currentDestinationResult.length + 1) * 5)
            }
        }, 50)
    }

    function destroySortDestination() {
        if(!!destinationMatchDataTable){
            destinationMatchDataTable.destroy();
        }
    }

    function initSortDestination() {
        destinationMatchDataTable = $("#destinationMatch").DataTable({
            "bPaginate": false,
            "bFilter": false,
            "bInfo": false,
            "order": [[ 3, "desc" ]],
            columnDefs: [
                { orderable: false, targets: [-1, -2] }
            ]
        });
    }

    function addRowWithData(tableId, data, index, callback) {
        var table = document.getElementById(tableId);
        if(!table) return;
        var body = table.tBodies[0];
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
        body.appendChild(row);
        if(typeof callback === "function"){
            callback(data);
        }
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
            });
        }
    }
    
    function selectFirstRow() {
        if(matchingResult && matchingResult.length > 0){
            $('#' + sourceTableId).find(' tbody tr:first').addClass('highlight-selected').siblings().removeClass('highlight-selected');
            var rowData = matchingResult[0];
            if(rowData && rowData.source && rowData.source.messageId){
                showMail(rowData.source.messageId, function (result) {
                    showMailContent(result)
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

    function removeAllRow(tableId) { //Except header row
        var table = document.getElementById(tableId);
        while(table && table.rows && table.rows.length > 2){
            var row = table.rows[2];
            row.parentNode.removeChild(row);
        }
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

    function showMailWithReplacedRange(messageId, range, replaceType, callback) {
        messageId = messageId.replace(/\+/g, '%2B');
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/user/matchingResult/editEmail?messageId=" + messageId + "&range=" + range + "&replaceType=" + replaceType,
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
                    filesInnerHTML += ("<a href='/user/download?path=" + encodeURIComponent(file.storagePath) + "&fileName=" + file.fileName + "' download>" + file.fileName + "(" + (file.size/1024) + "KB); </a>")
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
        var rdMailAttachmentDiv = document.getElementById(rdMailAttachmentId);
        rdMailAttachmentDiv.innerHTML = "";
        updateMailEditorContent("");
        if(data){
            document.getElementById(rdMailSenderId).value = data.to;
            document.getElementById(rdMailSubjectId).value = data.subject;
            data.originalBody = data.originalBody.replace(/(?:\r\n|\r|\n)/g, '<br />');
            data.replacedBody = data.replacedBody ? data.replacedBody.replace(/(?:\r\n|\r|\n)/g, '<br />') : data.replacedBody;
            updateMailEditorContent(data.originalBody);
            if( data.replacedBody != null){
                updateMailEditorContent(data.replacedBody, true);
            }
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
                rdMailAttachmentDiv.innerHTML = filesInnerHTML;
            } else {
                rdMailAttachmentDiv.innerHTML = "添付ファイルなし";
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
        showMailWithReplacedRange(messageId, textRange, replaceType, function (result) {
            showMailContenttToEditor(result, receiver)
        });
        $('#sendSuggestMail').off('click');
        $("#sendSuggestMail").click(function () {
            var btn = $(this);
            btn.button('loading');
            var form = {
                messageId: messageId,
                subject: $( "#" + rdMailSubjectId).val(),
                receiver: $( "#" + rdMailReceiverId).val(),
                content: getMailEditorContent()
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

})(jQuery);
