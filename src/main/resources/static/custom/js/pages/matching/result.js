
(function () {
    "use strict";
    var sourceTableId = 'sourceMatch';
    var destinationTableId = 'destinationMatch';
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailAttachmentDivId = 'mailAttachment';
    var openFileFolderButtonId = '#openFileFolderBtn';
    var matchingResult = null;
    var currentDestinationResult = [];
    var onlyDisplayNonZeroRow = true;
    var sourceMatchDataTable;
    var destinationMatchDataTable;
    var selectedRowData;

    $(function () {
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
        console.log("showSourceData: ", data);
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
            "order": [],
        });
    }
    
    function showDestinationData(tableId, data) {
        setTimeout(function () {
            var word = data.word;
            var source = data.source;
            currentDestinationResult = data.destinationList;
            destroySortDestination();
            removeAllRow(tableId);
            if(currentDestinationResult.length > 0){
                for(var i = 0; i < currentDestinationResult.length; i ++){
                    currentDestinationResult[i].word = word;
                    addRowWithData(tableId, currentDestinationResult[i], i, function () {
                        setRowClickListener("showDestinationMail", showDestinationMail);
                    });
                }
            }
            setRowClickListener("sendToMoto", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = currentDestinationResult[index];
                if(rowData && rowData.messageId){
                    console.log("sendToMoto: ", rowData.messageId, rowData.matchRange); //Down use can duoi
                    //TODO: popup
                }
            });
            setRowClickListener("sendToSaki", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = currentDestinationResult[index];
                if(rowData){
                    if(selectedRowData && selectedRowData.source && selectedRowData.source.messageId){
                        console.log("sendToSaki: ", selectedRowData.source.messageId, rowData.range); //Up use can tren
                        //TODO: popup
                    }
                }
            });
            initSortDestination();
        }.bind(this), 0);
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
            "order": [],
            columnDefs: [
                { orderable: false, targets: [-1, -2] }
            ]
        });
    }

    function addRowWithData(tableId, data, index, callback) {
        var table = document.getElementById(tableId);
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
            showMail(rowData.source.messageId);
        }
    }

    function showDestinationMail() {
        $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = currentDestinationResult[index];
        if(rowData && rowData.messageId){
            showMail(rowData.messageId);
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
        while(table.rows.length > 2){
            var row = table.rows[2];
            row.parentNode.removeChild(row);
        }
    }
    
    function showMail(messageId) {
        messageId = messageId.replace(/\+/g, '%2B');
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/user/matchingResult/email?messageId=" + messageId,
            cache: false,
            timeout: 600000,
            success: function (data) {
                if(data.status){
                    if(data.list && data.list.length > 0){
                        showMailContent(data.list[0])
                    }
                }
            },
            error: function (e) {
                console.error("getMail ERROR : ", e);
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
        updateMailEditorContent("");
        mailAttachmentDiv.innerHTML = "";
        if(data){
            mailSubjectDiv.innerHTML = '<div class="mailbox-read-info">' +
                '<h5><b>' + data.subject + '</b></h5>' +
            '<h6>送信者: ' + data.from + '<span class="mailbox-read-time pull-right">' + data.receivedAt + '</span></h6>' +
            '</div>';
            data.originalBody = data.originalBody.replace(/(?:\r\n|\r|\n)/g, '<br />');
            mailBodyDiv.innerHTML = data.originalBody;
            updateMailEditorContent(data.originalBody);
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
                disableButton(openFileFolderButtonId, true);
            }
        } else {
            disableButton(openFileFolderButtonId, true);
        }
    }

    function updateMailEditorContent(content){
        // var editor = tinymce.get('mailBody');
        // editor.setContent(content);
        // editor.undoManager.clear();
        // editor.undoManager.add();
    }
    
    function getMailEditorContent() {
        // var editor = tinymce.get('mailBody');
        // console.log("Mail editor content: ", editor.getContent());
    }

    function disableButton(buttonId, disabled) {
        if(buttonId && buttonId.length > 0){
            $(buttonId).prop("disabled", disabled);
        }
    }

})(jQuery);
