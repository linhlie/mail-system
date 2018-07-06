
(function () {

    var sConditionTableId = "sConditionTable";
    var dConditionTableId = "dConditionTable";
    var mConditionTableId = "mConditionTable";

    var sourceListKey = "/user/matchingSettings/listSourceKey";
    var sourcePrefixUrlKey = "/user/matchingSettings/source";
    var destinationListKey = "/user/matchingSettings/listDestinationKey";
    var destinationPrefixUrlKey = "/user/matchingSettings/destination";
    var matchingListKey = "/user/matchingSettings/listMatchingKey";
    var matchingPrefixUrlKey = "/user/matchingSettings/matching";

    var currentListKey;
    var currentPrefixUrlKey;
    var firstRowHTML = '<tr class="hidden" role="row">' +
        '<td rowspan="1" colspan="1" data="key"><span></span></td>' +
        '<td class="action fit" rowspan="1" colspan="1" data="id"><button class="btn btn-block btn-default" name="removeCondition" type="button">削除</button></td>' +
        '<td class="action fit" rowspan="1" colspan="1" data="id"><button class="btn btn-block btn-default" name="exportCondition" type="button">エクスポート</button></td>' +
        '</tr>';

    var conditionData = {
        "source": [],
        "destination": [],
        "matching": [],
    };

    $(function () {
        addEventListeners();
        initStickyHeader();
        initConditionTable(sConditionTableId);
        initConditionTable(dConditionTableId);
        initConditionTable(mConditionTableId);
    });
    
    function addEventListeners() {
        setButtonClickListener("importFile", function () {
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
        if(conditionSetting && conditionSetting.type && conditionSetting.prefixUrlKey && conditionSetting.listKey) {
            var tableId = getTableIdFromType(conditionSetting.type);
            var datalistStr = localStorage.getItem(conditionSetting.listKey);
            var datalist = JSON.parse(datalistStr);
            datalist = datalist || [];
            var name = prompt("保存された名前を入力してください:", conditionSetting.name);
            if (name == null || name == "") return;
            if(datalist.indexOf(name) < 0){
                datalist.push(name);
            }
            var key = conditionSetting.prefixUrlKey + "@" + name;
            localStorage.setItem(conditionSetting.listKey, JSON.stringify(datalist));
            localStorage.setItem(key, JSON.stringify(conditionSetting.conditions));
            initConditionTable(tableId);
        }
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
        loadConditionData(tableId);
        pushConditionData(tableId);
    }
    
    function getConditionType(tableId) {
        switch (tableId) {
            case sConditionTableId:
                return "source";
            case dConditionTableId:
                return "destination";
            case mConditionTableId:
                return "matching";
            default:
                return "source";
        }
    }
    
    function getTableIdFromType(type) {
        switch (type) {
            case "source":
                return sConditionTableId;
            case "destination":
                return dConditionTableId;
            case "matching":
                return mConditionTableId;
            default:
                return sConditionTableId;
        }
    }
    
    function getListKey(type) {
        switch (type) {
            case "source":
                return sourceListKey;
            case "destination":
                return destinationListKey;
            case "matching":
                return matchingListKey;
            default:
                return sourceListKey;
        }
    }

    function getPrefixUrlKey(type) {
        switch (type) {
            case "source":
                return sourcePrefixUrlKey;
            case "destination":
                return destinationPrefixUrlKey;
            case "matching":
                return matchingPrefixUrlKey;
            default:
                return sourcePrefixUrlKey;
        }
    }

    function getKeys(type){
        switch (type) {
            case "source":
                currentListKey = sourceListKey;
                currentPrefixUrlKey = sourcePrefixUrlKey;
                break;
            case "destination":
                currentListKey = destinationListKey;
                currentPrefixUrlKey = destinationPrefixUrlKey;
                break;
            case "matching":
                currentListKey = matchingListKey;
                currentPrefixUrlKey = matchingPrefixUrlKey;
                break;
            default:
                currentListKey = sourceListKey;
                currentPrefixUrlKey = sourcePrefixUrlKey;
                break;
        }
    }

    function loadConditionData(tableId) {
        var type = getConditionType(tableId);
        var listKey = getListKey(type);
        var conditionDataStr = localStorage.getItem(listKey);
        conditionData[type] = JSON.parse(conditionDataStr);
        conditionData[type] = Array.isArray(conditionData[type]) ? conditionData[type] : [];
        return conditionData[type];
    }

    function pushConditionData(tableId) {
        var type = getConditionType(tableId);
        removeAllRow(tableId, firstRowHTML);
        var html = firstRowHTML;
        if (conditionData[type].length > 0) {
            for(var i = 0; i < conditionData[type].length; i++) {
                html = html + addRowWithData(tableId, conditionData[type][i], i, type);
            }
            $("#" + tableId + "> tbody").html(html);
            setButtonClickListener("exportCondition", function () {
                var index = $(this).closest('tr')[0].getAttribute("data");
                var type = $(this).closest('tr')[0].getAttribute("data-type");
                var rowData = conditionData[type][index];
                exportCondition(rowData, type, index);
            });
            setButtonClickListener("removeCondition", function () {
                var index = $(this).closest('tr')[0].getAttribute("data");
                var type = $(this).closest('tr')[0].getAttribute("data-type");
                removeCondition(index, type);
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
                    var cellData = data;
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

    function exportCondition(conditionName, type, index) {
        var listKey = getListKey(type);
        var prefixUrlKey = getPrefixUrlKey(type);
        var key = prefixUrlKey + "@" + conditionName;
        var conditionData = localStorage.getItem(key) != null ? JSON.parse(localStorage.getItem(key)) : null;
        var data = {
            type: type,
            name: conditionName,
            listKey: listKey,
            prefixUrlKey: prefixUrlKey,
            conditions: conditionData
        };
        download(JSON.stringify(data), conditionName, "text/plain");
    }

    function removeCondition(index, type) {
        console.log("removeCondition: ", index, type);
        var conditionName = conditionData[type][index];
        conditionData[type].splice(index, 1);
        var listKey = getListKey(type);
        var prefixUrlKey = getPrefixUrlKey(type);
        var tableId = getTableIdFromType(type);
        localStorage.setItem(listKey, JSON.stringify(conditionData[type]));
        localStorage.removeItem(prefixUrlKey + "@" + conditionName);
        initConditionTable(tableId);
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

})(jQuery);
