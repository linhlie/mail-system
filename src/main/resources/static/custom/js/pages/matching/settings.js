
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
    var extractSourceBtnId = '#extractSourceBtn';
    var extractDestinationBtnId = '#extractDestinationBtn';
    var matchingWordsAreaId = '#matchingWordsArea';


    $(function () {
        setAddReplaceLetterRowListener('addMotoRow', sourceTableId, ["group", "combine", "item", "condition", "value"]);
        setAddReplaceLetterRowListener('addSakiRow', destinationTableId, ["group", "combine", "item", "condition", "value"]);
        setAddReplaceLetterRowListener('addMatchRow', matchingTableId, ["group", "combine", "item", "condition", "value"]);
        setRemoveRowListener("removeConditionRow");
        getDefaultSourceListData();
        getDefaultDestinationListData();
        setButtonClickListenter(saveSourceBtnId, saveSourceListData);
        setButtonClickListenter(getSourceBtnId, getSourceListData);
        setButtonClickListenter(saveDestinationBtnId, saveDestinationListData);
        setButtonClickListenter(getDestinationBtnId, getDestinationListData);
        setButtonClickListenter(submitFormBtnId, submit);
        setButtonClickListenter(extractSourceBtnId, extractSource);
        setButtonClickListenter(extractDestinationBtnId, extractDestination);
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
        var datalistKey = "/user/matchingSettings/listSourceKey";
        var datalistStr = localStorage.getItem(datalistKey);
        var datalist = JSON.parse(datalistStr);
        datalist = datalist || [];
        showNamePrompt(datalist, function (name) {
            if (name != null && name.length > 0) {
                if(datalist.indexOf(name) < 0){
                    datalist.push(name);
                }
                localStorage.setItem(datalistKey, JSON.stringify(datalist));
                var data = buildDataFromTable(sourceTableId);
                saveListData(
                    "/user/matchingSettings/source",
                    name,
                    data
                )
            }
        })
    }
    
    function showNamePrompt(datalist, callback) {
        $('#dataModal').modal();
        datalist = datalist || [];
        $( '#dataModalName').val('');
        $('#keylist').html('');
        for(var i = 0; i < datalist.length; i++){
            $('#keylist').append("<option value='" + datalist[i] + "'>");
        }
        setInputAutoComplete("dataModalName");
        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {
            var name = $( '#dataModalName').val();
            $('#dataModal').modal('hide');
            if(typeof callback === "function"){
                callback(name);
            }
        });
        $('#dataModalCancel').off('click');
        $("#dataModalCancel").click(function () {
            $('#dataModal').modal('hide');
            if(typeof callback === "function"){
                callback();
            }
        });
    }

    function saveDestinationListData(){
        var datalistKey = "/user/matchingSettings/listDestinationKey";
        var datalistStr = localStorage.getItem(datalistKey);
        var datalist = JSON.parse(datalistStr);
        datalist = datalist || [];
        showNamePrompt(datalist, function (name) {
            if (name != null && name.length > 0) {
                if(datalist.indexOf(name) < 0){
                    datalist.push(name);
                }
                localStorage.setItem(datalistKey, JSON.stringify(datalist));
                var data = buildDataFromTable(destinationTableId);
                saveListData(
                    "/user/matchingSettings/destination",
                    name,
                    data
                )
            }
        })
    }

    function saveListData(url, name,  data) {
        var key = url + "@" + name;
        localStorage.setItem(key, JSON.stringify(data));
    }

    function setRemoveRowListener(name) {
        $("span[name='"+name+"']").off('click');
        $("span[name='"+name+"']").click(function () {
            $(this)[0].parentNode.parentNode.className+=' hidden';
        })
    }
    
    function setInputAutoComplete(name) {
        $("input[name='"+name+"']").off('click');
        $("input[name='"+name+"']").off('mouseleave');
        $("input[name='"+name+"']").on('click', function() {
            $(this).attr('placeholder',$(this).val());
            $(this).val('');
        });
        $("input[name='"+name+"']").on('mouseleave', function() {
            if ($(this).val() == '') {
                $(this).val($(this).attr('placeholder'));
            }
        });
    }

    function setAddReplaceLetterRowListener(name, tableId, data) {
        $("span[name='"+name+"']").click(function () {
            addRow(tableId);
        })
    }
    
    function getSourceListData(skip) {
        if(skip){
            getListData("/user/matchingSettings/source", null, sourceTableId, getSourceBtnId);
        } else {
            var datalistKey = "/user/matchingSettings/listSourceKey";
            var datalistStr = localStorage.getItem(datalistKey);
            var datalist = JSON.parse(datalistStr);
            datalist = datalist || [];
            showNamePrompt(datalist, function (name) {
                if (name != null && name.length > 0) {
                    getListData("/user/matchingSettings/source", name, sourceTableId, getSourceBtnId);
                }
            })
        }
    }

    function getDestinationListData(skip) {
        if(skip){
            getListData("/user/matchingSettings/destination", null, destinationTableId, getDestinationBtnId);
        } else {
            var datalistKey = "/user/matchingSettings/listDestinationKey";
            var datalistStr = localStorage.getItem(datalistKey);
            var datalist = JSON.parse(datalistStr);
            datalist = datalist || [];
            showNamePrompt(datalist, function (name) {
                if (name != null && name.length > 0) {
                    getListData("/user/matchingSettings/destination", name, destinationTableId, getDestinationBtnId);
                }
            })
        }
    }

    // function getMatchingListData() {
    //     var name = prompt("Please enter saved name", "");
    //     if (name != null) {
    //         getListData("/user/matchingSettings/matching", name, matchingTableId);
    //     }
    // }
    
    function getDefaultSourceListData() {
        var data = [];
        var condition = {
            combine: "-1",
            condition: "-1",
            group: false,
            id: "",
            item: "7",
            remove: 0,
            value: "",
        };
        data.push(condition);
        showDefaultListData(data, sourceTableId);
    }

    function getDefaultDestinationListData() {
        var data = [];
        var condition = {
            combine: "-1",
            condition: "-1",
            group: false,
            id: "",
            item: "8",
            remove: 0,
            value: "",
        };
        data.push(condition);
        showDefaultListData(data, destinationTableId);
    }

    function showDefaultListData(data, tableId) {
        data = addDefaultReceiveDateRow(data);
        for(var i = 0; i < data.length; i ++){
            addRowWithData(tableId, data[i]);
        }
    }

    function getListData(url, name, tableId) {
        var data = null;
        if(name && name.length > 0){
            var key = url + "@" + name;
            data = localStorage.getItem(key) != null ? JSON.parse(localStorage.getItem(key)) : null;
        }
        if(data != null){
            console.log("getListData: ", data);
            removeAllRow(tableId);
            data = addDefaultReceiveDateRow(data);
            for(var i = 0; i < data.length; i ++){
                addRowWithData(tableId, data[i]);
            }
        } else {
            alert("見つけませんでした。");
        }
    }

    function addDefaultReceiveDateRow(data) {
        var receivedDateCondition = null;
        for(var i = 0; i < data.length; i ++){
            var condition = data[i];
            if(condition.item == "9"){
                receivedDateCondition = condition;
            }
        }
        if(receivedDateCondition == null){
            // var sevenDayAgo = new Date();
            // sevenDayAgo.setDate(sevenDayAgo.getDate() - 7);
            // var month = '' + (sevenDayAgo.getMonth() + 1);
            // var day = '' + sevenDayAgo.getDate();
            // var year = sevenDayAgo.getFullYear();
            // if (month.length < 2) month = '0' + month;
            // if (day.length < 2) day = '0' + day;

            receivedDateCondition = {
                combine: "0",
                condition: "4",
                group: true,
                id: "",
                item: "9",
                remove: 0,
                value: "-7",
            }
            data.push(receivedDateCondition);
        }
        return data;
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
        setInputAutoComplete("matchingValue");
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
    
    function extractSource() {
        var data = {
            "conditionList" : buildDataFromTable(sourceTableId),
            "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
        };
        sessionStorage.setItem("extractSourceData", JSON.stringify(data));
        var win = window.open('/user/extractSource', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('Please allow popups for this website');
        }
    }
    
    function extractDestination() {
        var data = {
            "conditionList" : buildDataFromTable(destinationTableId),
            "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
        };
        sessionStorage.setItem("extractDestinationData", JSON.stringify(data));
        var win = window.open('/user/extractDestination', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('Please allow popups for this website');
        }
    }
    
    function submit() {
        var matchingWords = $(matchingWordsAreaId).val();
        matchingWords = matchingWords.toLocaleLowerCase();
        matchingWords = matchingWords.trim();
        var form = {
            "sourceConditionList" : buildDataFromTable(sourceTableId),
            "destinationConditionList" : buildDataFromTable(destinationTableId),
            "matchingConditionList" : buildDataFromTable(matchingTableId),
            "matchingWords": matchingWords,
            "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
            "spaceEffective": $('input[name=spaceEffective]:checked', formId).val() === "true"
        };
        console.log("form: ", form);
        disableButton(submitFormBtnId, true);
        sessionStorage.setItem("matchingConditionData", JSON.stringify(form));
        // window.location = '/user/matchingResult';
        var win = window.open('/user/matchingResult', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('Please allow popups for this website');
        }
    }

})(jQuery);
