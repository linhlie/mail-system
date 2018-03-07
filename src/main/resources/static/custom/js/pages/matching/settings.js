
(function () {
    "use strict";
    var formId = '#matchingConditionSettingsForm';
    var sourceTableId = 'motoMail';
    var destinationTableId = 'sakiMail';
    var matchingTableId = 'matching';
    var saveSourceBtnId = '#saveSourceBtn';
    var getSourceBtnId = '#getSourceBtn';
    var saveDestinationBtnId = '#saveDestinationBtn';
    var getDestinationBtnId = '#getDestinationBtn';
    var submitFormBtnId = '#submitFormBtn';
    var matchingWordsAreaId = '#matchingWordsArea';


    $(function () {
        setAddReplaceLetterRowListener('addMotoRow', sourceTableId, ["group", "combine", "item", "condition", "value"]);
        setAddReplaceLetterRowListener('addSakiRow', destinationTableId, ["group", "combine", "item", "condition", "value"]);
        setAddReplaceLetterRowListener('addMatchRow', matchingTableId, ["group", "combine", "item", "condition", "value"]);
        setRemoveRowListener("removeConditionRow");
        getSourceListData();
        getDestinationListData();
        getMatchingListData();
        setButtonClickListenter(saveSourceBtnId, saveSourceListData);
        setButtonClickListenter(getSourceBtnId, getSourceListData);
        setButtonClickListenter(saveDestinationBtnId, saveDestinationListData);
        setButtonClickListenter(getDestinationBtnId, getDestinationListData);
        setButtonClickListenter(submitFormBtnId, submit);
    });
    
    function setButtonClickListenter(id, callback) {
        $(id).off('click');
        $(id).click(function () {
            if(typeof callback === "function"){
                callback();
            }
        });
    }

    function saveSourceListData(){
        var data = buildDataFromTable(sourceTableId);
        var form = {
            "sourceConditionList": data
        };
        saveListData(
            "/user/matchingSettings/saveSource",
            form,
            function onSuccess() {
                getSourceListData();
            },
            function onError(e) {
                console.error("[ERR] saveSourceListData: ", e);
            }
        )
    }

    function saveDestinationListData(){
        var data = buildDataFromTable(destinationTableId);
        var form = {
            "destinationConditionList": data
        };
        saveListData(
            "/user/matchingSettings/saveDestination",
            form,
            function onSuccess() {
                getDestinationListData();
            },
            function onError(e) {
                console.error("[ERR] saveDestinationListData: ", e);
            }
        )
    }

    function saveListData(url, data, success, error) {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: url,
            data: JSON.stringify(data),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                if(data && data.status){
                    if(typeof success === "function"){
                        success();
                    }
                } else {
                    if(typeof error === "function"){
                        error();
                    }
                }
            },
            error: function (e) {
                if(typeof error === "function"){
                    error(e);
                }
            }
        });
    }

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
        getListData("/user/matchingSettings/source", sourceTableId, getSourceBtnId);
    }

    function getDestinationListData() {
        getListData("/user/matchingSettings/destination", destinationTableId, getDestinationBtnId);
    }

    function getMatchingListData() {
        getListData("/user/matchingSettings/matching", matchingTableId);
    }

    function getListData(url, tableId, buttonId) {
        disableButton(buttonId, true);
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: url,
            cache: false,
            timeout: 600000,
            success: function (data) {
                disableButton(buttonId, false);
                if(data.status){
                    if(data.list){
                        removeAllRow(tableId);
                        if(data.list.length > 0){
                            for(var i = 0; i < data.list.length; i ++){
                                addRowWithData(tableId, data.list[i]);
                            }
                        } else {
                            addRow(tableId);
                        }
                    }
                }
            },
            error: function (e) {
                console.error("getListData ERROR : ", e);
                disableButton(buttonId, false);
            }
        });
    }

    function disableButton(buttonId, disabled) {
        if(buttonId && buttonId.length > 0){
            $(buttonId).prop("disabled", disabled);
        }
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
    
    function buildDataFromTable(tableId) {
        var data = [];
        var table = document.getElementById(tableId);
        for (var i = 1; i < table.rows.length; i++) {
            var row = table.rows.item(i);
            if(isSkipRow(row)) continue;
            var cells = row.cells;
            var rowData = {};
            for (var j = 0; j < cells.length; j++) {
                var cell = cells.item(j);
                var cellKey = cell.getAttribute("data");
                if(!cellKey) continue;

                var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
                if(cellNode){
                    if(cellNode.nodeName == "INPUT") {
                        if(cellNode.type == "checkbox"){
                            rowData[cellKey] = cellNode.checked;
                        } else if(cellNode.type == "text"){
                            rowData[cellKey] = cellNode.value;
                        }
                    } else if(cellNode.nodeName == "SELECT") {
                        rowData[cellKey] = cellNode.value;
                    } else if(cellNode.nodeName == "SPAN") {
                        rowData[cellKey] = cellNode.textContent;
                    }
                }
            }
            rowData["remove"] = row.className.indexOf('hidden') >= 0 ? 1 : 0;
            data.push(rowData);
        }
        return data;
    }
    
    function removeEmptyRowData(data) {
        var result = [];
        for (var i = 0; i < data.length; i++) {
            var rowData = data[i];
            if(!isEmptyRowData(rowData)) {
                result.push(rowData);
            }
        }
        return result;
    }
    
    function isEmptyRowData(rowData) {
        return (rowData["item"] === '-1' || rowData["item"] === -1);
    }

    function isSkipRow(row) {
        return row && row.className && row.className.indexOf("skip") >= 0;
    }
    
    function submit() {
        var form = {
            "sourceConditionList" : buildDataFromTable(sourceTableId),
            "destinationConditionList" : buildDataFromTable(destinationTableId),
            "matchingConditionList" : buildDataFromTable(matchingTableId),
            "matchingWords": $(matchingWordsAreaId).val(),
            "distinguish": $('input[name=distinguish]:checked', formId).val() === "true"
        };
        disableButton(submitFormBtnId, true);
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/user/matchingSettings/submitForm",
            data: JSON.stringify(form),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                disableButton(submitFormBtnId, false);
                if(data && data.status){
                    console.log("Submit success");
                } else {
                    console.error("[ERROR] submit failed: ", e);
                }
            },
            error: function (e) {
                console.error("[ERROR] submit error: ", e);
                disableButton(submitFormBtnId, false);
            }
        });
    }

})(jQuery);
