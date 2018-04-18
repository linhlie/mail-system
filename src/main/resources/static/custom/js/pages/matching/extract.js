
(function () {
    "use strict";
    var sourceTableId = 'sourceMatch';
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailAttachmentDivId = 'mailAttachment';
    var totalResultContainId = 'totalResultContain';
    var extractResult = null;
    var sourceMatchDataTable;
    var selectedRowData;

    var replaceSourceHTML = '<tr role="row" class="hidden">' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="range"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="receivedAt"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="to"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '</tr>';

    $(function () {
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
    });

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
        }
        updateTotalResult(data.length);
        initSortSource();
        selectFirstRow();
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
        sourceMatchDataTable = $("#sourceMatch").DataTable({
            "bPaginate": false,
            "bFilter": false,
            "bInfo": false,
            "order": [[ 1, "desc" ]]
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
                showMailContent(result)
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
                    showMailContent(result)
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
        } else {
        }
    }

})(jQuery);
