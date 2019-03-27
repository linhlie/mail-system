
(function () {

    var sConditionTableId = "sConditionTable";
    var dConditionTableId = "dConditionTable";
    var mConditionTableId = "mConditionTable";

    var importFileType;

    var firstRowHTML = '<tr class="hidden" role="row">' +
        '<td rowspan="1" colspan="1" data="conditionName"><span></span></td>' +
        '<td class="action fit" rowspan="1" colspan="1" data="id"><button class="btn btn-block btn-default" name="removeCondition" type="button">削除</button></td>' +
        '<td class="action fit" rowspan="1" colspan="1" data="id"><button class="btn btn-block btn-default" name="exportCondition" type="button">エクスポート</button></td>' +
        '</tr>';


    var SOURCE_CONDITION = 1;
    var DESTINATION_CONDITION = 2;
    var MATCHING_CONDITION = 3;

    var SOURCE_CONDITION_OLD = "source";
    var DESTINATION_CONDITION_OLD  = "destination";
    var MATCHING_CONDITION_OLD  = "matching";

    var conditionSavedArr = [];

    $(function () {
        addEventListeners();
        initStickyHeader();

        loadConditionSaved();
    });
    
    function addEventListeners() {
        setButtonClickListener("importFile", function () {
            var type = this.getAttribute("data");
            importFileType = type;
            $( "#importFileId" ).click();
        });
        $('#importFileId').change(function(){
            var files = $('#importFileId').prop('files');
            if(files && files.length > 0) {
                var file = files[0];
                if(file.type === "text/plain") {
                    var reader = new FileReader();
                    reader.onload = function(e) {
                        saveConditionSetting(reader.result);
                        $('#importFileId').val(null);
                    };
                    reader.readAsText(file);
                }
            }
        })
    }
    
    function saveConditionSetting(conditionSettingStr) {
        var conditionSetting = JSON.parse(conditionSettingStr);
        var conditionType = "";
        if(conditionSetting.conditionType){
            conditionType = conditionSetting.conditionType;
        }else{
            switch (conditionSetting.type) {
                case SOURCE_CONDITION_OLD:
                    conditionType = SOURCE_CONDITION;
                    break;
                case DESTINATION_CONDITION_OLD:
                    conditionType = DESTINATION_CONDITION;
                    break;
                case MATCHING_CONDITION_OLD:
                    conditionType = MATCHING_CONDITION;
                    break;
                default:
                    conditionType = SOURCE_CONDITION;
                    break;
            }
        }
        if(conditionSetting && conditionType) {
            var isAllow = isAllowType(conditionType);
            if(!isAllow) {
                var incompatibleTypeMessage = getIncompatibleTypeMessage(conditionSetting.conditionType);
                alert(incompatibleTypeMessage);
                return;
            }
            var conditionName = "";
            if(conditionSetting.conditionName){
                conditionName = conditionSetting.conditionName;
            }else{
                conditionName = conditionSetting.name;
            }
            var name = prompt("保存された名前を入力してください:", conditionName);
            if (name == null || name == "") return;
            var conditionStr = null;
            if(conditionSetting.condition){
                conditionStr = JSON.stringify(conditionSetting.condition);
            }else{
                conditionStr = JSON.stringify(conditionSetting.conditions);
            }
            var data = {
                conditionName: name,
                condition: conditionStr,
                conditionType: conditionType
            };

            function onSuccess(response) {
                if(response && response.status) {
                    $.alert({
                        title: "",
                        content: "条件追加が成功しました",
                        onClose: function () {
                            loadConditionSaved();
                        }
                    });
                } else {
                    $.alert("条件追加が失敗しました");
                }
            }

            function onError(response) {
                $.alert("条件追加が失敗しました");
            }

            addConditionSaved(data, onSuccess, onError);

        }
    }
    
    function getIncompatibleTypeMessage(type) {
        var type1 = getTypeMessageInJapanese(importFileType);
        var type2 = getTypeMessageInJapanese(type);
        return "「" + type1 + "」に「" + type2 + "」のデータを取り込むことができません。";
    }
    
    function getTypeMessageInJapanese(type) {
        switch (type) {
            case SOURCE_CONDITION:
                return "比較メール元抽出条件";
            case DESTINATION_CONDITION:
                return "比較メール先抽出条件";
            case MATCHING_CONDITION:
                return "マッチング条件";
            default:
                return "比較メール元抽出条件"
        }
    }

    function isAllowType(type) {
        if(type == SOURCE_CONDITION || type == SOURCE_CONDITION_OLD){
            return (importFileType == SOURCE_CONDITION);
        }
        if(type == DESTINATION_CONDITION || type == DESTINATION_CONDITION_OLD){
            return (importFileType == DESTINATION_CONDITION);
        }
        if(type == MATCHING_CONDITION || type == MATCHING_CONDITION_OLD){
            return (importFileType == MATCHING_CONDITION);
        }
        return false;
    }

    function initStickyHeader() {
        $(".table-container-wrapper").scroll(function () {
            $(this).find("thead.sticky-header")
                .css({
                    "user-select": "none",
                    "position": "relative",
                    "z-index": "10",
                    "transform": "translate(0px, " + $(this).scrollTop() + "px)"
                });
        });
    }

    function initConditionTable(tableId) {
        pushConditionData(tableId);
    }
    
    function getConditionType(tableId) {
        switch (tableId) {
            case sConditionTableId:
                return SOURCE_CONDITION;
            case dConditionTableId:
                return DESTINATION_CONDITION;
            case mConditionTableId:
                return MATCHING_CONDITION;
            default:
                return SOURCE_CONDITION;
        }
    }

    function pushConditionData(tableId) {
        var type = getConditionType(tableId);
        removeAllRow(tableId, firstRowHTML);
        var html = firstRowHTML;
        if (conditionSavedArr.length > 0) {
            for(var i = 0; i < conditionSavedArr.length; i++) {
                if(conditionSavedArr[i].conditionType == type){
                    html = html + addRowWithData(tableId, conditionSavedArr[i], i, type);
                }
            }
            $("#" + tableId + "> tbody").html(html);
            setButtonClickListener("exportCondition", function () {
                var index = $(this).closest('tr')[0].getAttribute("data");
                var rowData = conditionSavedArr[index];
                if(rowData != null && rowData.id != null){
                    exportCondition(rowData);
                }
            });
            setButtonClickListener("removeCondition", function () {
                var index = $(this).closest('tr')[0].getAttribute("data");
                var rowData = conditionSavedArr[index];
                if(rowData != null && rowData.id != null){
                    removeCondition(rowData.id );
                }
            });
        }
    }

    function removeAllRow(tableId, replaceHtml) {
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function addRowWithData(tableId, data, index, type) {
        var table = document.getElementById(tableId);
        if (!table) return "";
        var rowToClone = table.rows[1];
        var row = rowToClone.cloneNode(true);
        row.setAttribute("data", index);
        row.setAttribute("data-type", type);
        row.className = undefined;
        var cells = row.cells;
        for (var i = 0; i < cells.length; i++) {
            var cell = cells.item(i);
            var cellKey = cell.getAttribute("data");
            if (!cellKey) continue;
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if (cellNode) {
                if (cellNode.nodeName == "SPAN") {
                    var cellData = data[cellKey];
                    cellNode.textContent = cellData;
                }
            }
        }
        return row.outerHTML;
    }

    function setButtonClickListener(name, callback) {
        $("button[name='" + name + "']").off('click');
        $("button[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function exportCondition(condition) {
        var conditionJson = JSON.parse(condition.condition);
        var data = {
            conditionType: condition.conditionType,
            conditionName: condition.conditionName,
            condition: conditionJson
        };
        download(JSON.stringify(data), condition.conditionName, "text/plain");
    }

    function removeCondition(id) {
        function onSuccess() {
            loadConditionSaved();
        }
        function onError() {
            $.alert("条件消除が失敗しました");
        }
        $.confirm({
            title: '<b>【削除条件】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除しますか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deleteConditionSaved(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }

    function download(data, filename, type) {
        var file = new Blob([data], {type: type});
        if (window.navigator.msSaveOrOpenBlob) // IE10+
            window.navigator.msSaveOrOpenBlob(file, filename);
        else { // Others
            var a = document.createElement("a"),
                url = URL.createObjectURL(file);
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            setTimeout(function() {
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
            }, 0);
        }
    }

    function loadConditionSaved(){
        function onSuccess(response) {
            if(response && response.status){
                conditionSavedArr = response.list;
                initConditionTable(sConditionTableId);
                initConditionTable(dConditionTableId);
                initConditionTable(mConditionTableId);
            }
        }

        function onError(error) {
            console.error("load condition data fail")
        }

        getConditionSaved(onSuccess, onError);
    }

})(jQuery);
