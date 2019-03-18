(function () {
    "use strict";
    var sendMailHistoryTableId = 'sendMailHistory';
    var showSchedulerKey = "show-detail-scheduler-send-email";

    var schedulers = [];
    var historyDataTable;


    var replaceHistoryHTML = '<tr role="row" class="hidden">' +
        '<td rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="typeSendEmail"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="status"><span></span></td>' +
        '<td class="clickable table-text-center" rowspan="1" colspan="1" name="action" data="status"><button type="button" class="btn btn-xs btn-default">アクション</button></td>' +
        '<td class="clickable table-text-center" rowspan="1" colspan="1" name="detail"><button type="button" class="btn btn-xs btn-default">詳細</button></td>' +
        '<td class="clickable table-text-center" rowspan="1" colspan="1" name="delete"><button type="button" class="btn btn-xs btn-default">削除</button></td>' +
    '</tr>';

    $(function () {
        loadSchedulerData();
        initSortHistory();
        initStickyHeader();
    });

    function enableResizeColums() {
        $("#" + sendMailHistoryTableId).colResizable(
            {
                disable: true,
            }
        );
        $("#" + sendMailHistoryTableId).colResizable(
            {
                resizeMode: 'overflow',
            }
        );
    }

    function loadSchedulerData() {
        function onSuccess(response) {
            if(response && response.status){
                schedulers = response.list;
                if(schedulers && schedulers.length>0){
                    showDataTable(sendMailHistoryTableId, schedulers);
                }
            }
        }

        function onError(error) {
            console.error("create scheduler fail");
        }
        getSchedulerData(onSuccess, onError);
    }


    function showDataTable(tableId, data) {
        destroySortHistory();
        removeAllRow(tableId, replaceHistoryHTML);
        if (data && data.length > 0) {
            var html = replaceHistoryHTML;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> tbody").html(html);

            setRowClickListener("action", function () {
                var action = $(this).find('button').text();
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = schedulers[index];
                if (rowData && rowData.id) {
                    if(action == "中止" && rowData.status == 1 ){
                        rowData.status = 0;
                        changeStatusScheduler(rowData);
                    }

                    if(action == "アクティブ" && rowData.status == 0){
                        rowData.status = 1;
                        changeStatusScheduler(rowData);
                    }
                }
            });

            setRowClickListener("detail", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = schedulers[index];
                if (rowData && rowData.id) {
                    showDetailScheduler(rowData.id);
                }
            });

            setRowClickListener("delete", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = schedulers[index];
                if (rowData && rowData.id) {
                    deleteScheduler(rowData.id);
                }
            });
        }
        updateHistoryDataTrigger(tableId);
        enableResizeColums();
    }

    function updateHistoryDataTrigger(tableId) {
        $("#" + tableId).trigger("updateAll", [ true, function () {

        } ]);
    }

    function destroySortHistory() {
        if (!!historyDataTable) {
            historyDataTable.destroy();
        }
    }

    function initSortHistory() {
        $("#" + sendMailHistoryTableId).tablesorter(
            {
                theme: 'default',
                headers : { 9 : { sorter: false } },
            });
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
            var cellKeysData = cell.getAttribute("data");
            if (!cellKeysData || cellKeysData.length == 0) continue;
            var cellKeys = cellKeysData.split(".");
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if (cellNode) {
                if (cellNode.nodeName == "SPAN") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    if (Array.isArray(cellData)) {
                        cellNode.textContent = cellData.length;
                    } else {
                        if(cellKeysData === "typeSendEmail"){
                            switch (cellData) {
                                case 0:cellData = "即時に送信";
                                    break;
                                case 1:cellData = "指定時間に送信";
                                    break;
                                case 2:cellData = "毎日送信";
                                    break;
                                case 3:cellData = "毎月送信";
                                    break;
                                default: cellData="Unknow";
                            }
                        }
                        if(cellKeysData === "status"){
                            switch (cellData) {
                                case 0:cellData = "インアクティブ";
                                    break;
                                case 1:cellData = "アクティブ";
                                    break;
                                case 2:cellData = "送信";
                                    break;
                                case 3:cellData = "誤差";
                                    break;
                                default: cellData="Unknow";
                            }
                        }
                        cellNode.textContent = cellData;
                    }
                }
                if (cellNode.nodeName == "BUTTON") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    if (Array.isArray(cellData)) {
                        cellNode.textContent = cellData.length;
                    } else {
                        if(cellKeysData === "status"){
                            switch (cellData) {
                                case 0:cellData = "アクティブ";
                                    break;
                                case 1:cellData = "中止";
                                    break;
                                case 2:cellData = "none";
                                    break;
                                case 3:cellData = "none";
                                    break;
                                default: cellData="none";
                            }
                        }
                        cellNode.textContent = cellData;
                    }
                }
            }
        }
        return row.outerHTML;
    }

    function setRowClickListener(name, callback) {
        $("td[name='" + name + "']").off('click');
        $("td[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function initStickyHeader() {
        $(".table-container-wrapper").scroll(function () {
            $(this).find("thead.sticky-header")
                .css({
                    "user-select": "none",
                    "position": "relative",
                    "z-index": "10",
                    "transform": "translate(0px, " + $(this).scrollTop() + "px)",
                });
        });
    }

    function showDetailScheduler(id) {
        var data = {
            type: "update-scheduler",
            id: id
        }
        localStorage.setItem(showSchedulerKey, JSON.stringify(data));
        var win = window.open('/user/schedulerSendEmail', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('このサイトのポップアップを許可してください');
        }
    }

    function deleteScheduler(id) {
        function onSuccess() {
            loadSchedulerData()
        }
        function onError() {
            $.alert("スケジュール消除が失敗しました。");
        }
        $.confirm({
            title: '<b>スケジュール消除</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deleteSchedulerEmail(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }

    function changeStatusScheduler(data) {
        function onSuccess() {
            loadSchedulerData()
        }
        function onError() {
            loadSchedulerData();
            $.alert("スケジュールのステータス変更が失敗しました");
        }
        $.confirm({
            title: '<b>【スケジュールのステータス変更</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">本当にステータスを変更したいですか<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        changeStatusSchedulerEmail(data, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }

})(jQuery);