
(function () {
    "use strict";
    var formId = '#matchingResultForm';
    var sourceTableId = 'sourceMatch';
    var destinationTableId = 'destinationMatch';
    var matchingResultStr;
    matchingResultStr = sessionStorage.getItem("matchingResultData");
    matchingResultStr = matchingResultStr || "null";
    var matchingResult = null;
    try {
        matchingResult  = JSON.parse(matchingResultStr);
    } catch (error) {
        console.error("[ERROR] parse matching result error: ", error);
    }
    console.log("window.matchingResultData: ", matchingResult);

    $(function () {
        setRowClickListener("showMail", showMail);
        setRowClickListener("sourceRow", selectedRow);
        showSourceData(sourceTableId, matchingResult);
    });

    function showSourceData(tableId, data) {
        if(data.length > 0){
            for(var i = 0; i < data.length; i ++){
                addRowWithData(tableId, data[i]);
            }
        }
    }

    function addRowWithData(tableId, data) {
        var table = document.getElementById(tableId);
        var body = table.tBodies[0];
        var rowToClone = table.rows[1];
        var row = rowToClone.cloneNode(true);
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
                    console.log("cellData: ", typeof cellData, Array.isArray(cellData));
                    if(Array.isArray(cellData)){
                        cellNode.textContent = cellData.length;
                    } else {
                        cellNode.textContent = cellData;
                    }
                }
            }
        }
        body.appendChild(row);
        setRowClickListener("showMail", showMail);
        setRowClickListener("sourceRow", selectedRow);
    }
    
    function setRowClickListener(name, callback) {
        $("td[name='"+name+"']").off('click');
        $("td[name='"+name+"']").click(function () {
            if(typeof callback == "function"){
                callback();
            }
        })
    }
    
    function showMail() {
        console.log("showMail")
    }
    
    function selectedRow() {
        console.log("selectedRow");
    }

})(jQuery);
