
(function () {
    "use strict";
    var formId = '#matchingResultForm';
    var sourceTableId = 'sourceMatch';
    var destinationTableId = 'destinationMatch';
    var matchingResultStr;
    matchingResultStr = sessionStorage.getItem("matchingResultData");
    matchingResultStr = matchingResultStr || "null";
    var matchingResult = null;
    var currentDestinationResult = [];
    try {
        matchingResult  = JSON.parse(matchingResultStr);
    } catch (error) {
        console.error("[ERROR] parse matching result error: ", error);
    }
    console.log("window.matchingResultData: ", matchingResult);

    $(function () {
        showSourceData(sourceTableId, matchingResult);
    });

    function showSourceData(tableId, data) {
        removeAllRow(tableId);
        if(data.length > 0){
            for(var i = 0; i < data.length; i ++){
                addRowWithData(tableId, data[i], i, function () {
                    setRowClickListener("showSourceMail", showSourceMail);
                    setRowClickListener("sourceRow", selectedRow);
                });
            }
        }
    }
    
    function showDestinationData(tableId, data) {
        var word = data.word;
        var source = data.source;
        currentDestinationResult = data.destinationList;
        removeAllRow(tableId);
        if(currentDestinationResult.length > 0){
            for(var i = 0; i < currentDestinationResult.length; i ++){
                currentDestinationResult[i].word = word;
                addRowWithData(tableId, currentDestinationResult[i], i, function () {
                    setRowClickListener("showDestinationMail", showDestinationMail);
                });
            }
        }
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
        //TODO: show mail
    }

    function showDestinationMail() {
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = currentDestinationResult[index];
        if(rowData && rowData.messageId){
            showMail(rowData.messageId);
        }
    }
    
    function selectedRow() {
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = matchingResult[index];
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
        console.log("showMailContent: ", data);
    }

})(jQuery);
