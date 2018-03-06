
(function () {
    "use strict";
    var sourceTableId = 'motoMail';
    var destinationTableId = 'sakiMail';
    var matchingTableId = 'matching';

    $(function () {
        setAddReplaceLetterRowListener('addMotoRow', sourceTableId, ["group", "combine", "item", "condition", "value"]);
        setAddReplaceLetterRowListener('addSakiRow', destinationTableId, ["group", "combine", "item", "condition", "value"]);
        setAddReplaceLetterRowListener('addMatchRow', matchingTableId, ["group", "combine", "item", "condition", "value"]);
        setRemoveRowListener("removeConditionRow");
        getSourceListData();
        getDestinationListData();
        getMatchingListData();
    });

    function setRemoveRowListener(name) {
        $("span[name='"+name+"']").off('click');
        $("span[name='"+name+"']").click(function () {
            $(this)[0].parentNode.parentNode.className+=' hidden';
        })
    }

    function setAddReplaceLetterRowListener(name, tableId, data) {
        $("span[name='"+name+"']").click(function () {
            addRow(tableId);
        })
    }
    
    function getSourceListData() {
        getListData("/user/matchingSettings/source", sourceTableId);
    }

    function getDestinationListData() {
        getListData("/user/matchingSettings/destination", destinationTableId);
    }

    function getMatchingListData() {
        getListData("/user/matchingSettings/matching", matchingTableId);
    }

    function getListData(url, tableId) {
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: url,
            cache: false,
            timeout: 600000,
            success: function (data) {
                console.log("getListData: ", data);
                if(data.status){
                    if(data.list){
                        removeAllRow(tableId);
                        for(var i = 0; i < data.list.length; i ++){
                            addRowWithData(tableId, data.list[i]);
                        }
                        addRow(tableId);
                    }
                }
            },
            error: function (e) {
                console.error("getListData ERROR : ", e);
            }
        });
    }

    function addRow(tableId) {
        var table = document.getElementById(tableId);
        var body = table.tBodies[0];
        var rowToClone = table.rows[1];
        var clone = rowToClone.cloneNode(true);
        clone.className = undefined;
        body.appendChild(clone);
        setRemoveRowListener("removeConditionRow");
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
            var cellKey = cell.getAttribute("data");
            if(!cellKey) continue;
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if(cellNode){
                if(cellNode.nodeName == "INPUT") {
                    if(cellNode.type == "checkbox"){
                        cellNode.checked = data[cellKey];
                    } else if(cellNode.type == "text"){
                        cellNode.value = data[cellKey];
                    }
                } else if(cellNode.nodeName == "SELECT") {
                    cellNode.value = data[cellKey];
                } else if(cellNode.nodeName == "SPAN") {
                    cellNode.textContent = data[cellKey];
                }
            }
        }
        body.appendChild(row);
        setRemoveRowListener("removeConditionRow");
    }
    
    function removeAllRow(tableId) { //Except header row
        var table = document.getElementById(tableId);
        while(table.rows.length > 2){
            var row = table.rows[2];
            row.parentNode.removeChild(row);
        }
    }

})(jQuery);
