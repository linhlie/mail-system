
(function () {

    var conditionTableId = "conditionTable";

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

    var conditionData = [];

    $(function () {
        addEventListeners();
        initStickyHeader();
        initConditionTable();
    });
    
    function addEventListeners() {
        $('#conditionSelectType').change(initConditionTable);
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
                    };
                    reader.readAsText(file);
                }
            }
        })
    }
    
    function saveConditionSetting(conditionSettingStr) {
        var conditionSetting = JSON.parse(conditionSettingStr);
        if(conditionSetting && conditionSetting.name && conditionSetting.key && conditionSetting.listKey) {
            var datalistStr = localStorage.getItem(conditionSetting.listKey);
            var datalist = JSON.parse(datalistStr);
            datalist = datalist || [];
            if(datalist.indexOf(conditionSetting.name) < 0){
                datalist.push(conditionSetting.name);
            }
            localStorage.setItem(conditionSetting.listKey, JSON.stringify(datalist));
            localStorage.setItem(conditionSetting.key, JSON.stringify(conditionSetting.conditions));
        }
        initConditionTable();
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

    function initConditionTable() {
        var type = getConditionType();
        getKeys(type);
        loadConditionData();
        pushConditionData(conditionTableId);
    }
    
    function getConditionType() {
        return $("#conditionSelectType").val();
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

    function loadConditionData() {
        var conditionDataStr = localStorage.getItem(currentListKey);
        conditionData = JSON.parse(conditionDataStr);
        console.log("loadConditionData: ", conditionData);
        conditionData = Array.isArray(conditionData) ? conditionData : [];
        return conditionData;
    }

    function pushConditionData(tableId) {
        removeAllRow(tableId, firstRowHTML);
        var html = firstRowHTML;
        if (conditionData.length > 0) {
            for(var i = 0; i < conditionData.length; i++) {
                html = html + addRowWithData(tableId, conditionData[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
            setButtonClickListener("exportCondition", function () {
                var index = $(this).closest('tr')[0].getAttribute("data");
                var rowData = conditionData[index];
                exportCondition(rowData, index);
            });
            setButtonClickListener("removeCondition", function () {
                var index = $(this).closest('tr')[0].getAttribute("data");
                removeCondition(index);
            });
        }
    }

    function removeAllRow(tableId, replaceHtml) {
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function addRowWithData(tableId, data, index) {
        var table = document.getElementById(tableId);
        if (!table) return "";
        var rowToClone = table.rows[1];
        var row = rowToClone.cloneNode(true);
        row.setAttribute("data", index);
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

    function exportCondition(conditionName, index) {
        var key = currentPrefixUrlKey + "@" + conditionName;
        var conditionData = localStorage.getItem(key) != null ? JSON.parse(localStorage.getItem(key)) : null;
        var data = {
            name: conditionName,
            listKey: currentListKey,
            key: key,
            conditions: conditionData
        };
        download(JSON.stringify(data), conditionName, "text/plain");
    }

    function removeCondition(index) {
        var conditionName = conditionData[index];
        conditionData.splice(index);
        localStorage.setItem(currentListKey, JSON.stringify(conditionData));
        localStorage.removeItem(currentPrefixUrlKey + "@" + conditionName);
        initConditionTable();
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
