(function () {
    "use strict";
    var sendMailHistoryTableId = 'sendMailHistory';
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailAttachmentDivId = 'mailAttachment';
    var historyQuickFilterId = 'historyQuickFilter';
    var fromDateId = 'historyFromDate';
    var toDateId = 'historyToDate';
    var historySearchBtnId = 'historySearchBtn';
    var histories = null;
    var historyDataTable;
    var selectedRowData;

    var replaceHistoryHTML = '<tr role="row" class="hidden">' +
        '<td class="clickable fit" name="historyRow" rowspan="1" colspan="1" data="sentAt"><span></span></td>' +
        '<td class="clickable fit" name="historyRow" rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td class="clickable fit" name="historyRow" rowspan="1" colspan="1" data="to"><span></span></td>' +
        '<td class="clickable" name="historyRow" rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '<td class="clickable fit" name="historyRow" rowspan="1" colspan="1" data="sendType"><span></span></td>' +
        '<td class="clickable fit" name="historyRow" rowspan="1" colspan="1" data="originalReceivedAt"><span></span></td>' +
        '<td class="clickable fit" name="historyRow" rowspan="1" colspan="1" data="matchingReceivedAt"><span></span></td>' +
        '<td class="clickable fit" name="historyRow" rowspan="1" colspan="1" data="matchingMailAddress"><span></span></td>' +
        '<td class="clickable fit" name="historyRow" rowspan="1" colspan="1" data="username"><span></span></td>' +
        '</tr>';
    $(function () {
        initSortHistory();
        setupDatePickers();
        updateDisableDatePickers($('#' + historyQuickFilterId).val());
        var payload = getSearchPayload();
        loadHistoryData(payload);
        initStickyHeader();
        addEventListeners();
    });
    
    function addEventListeners() {
        addHistorySearchButtonClickListener()
    }
    
    function addHistorySearchButtonClickListener() {
        $('#' + historySearchBtnId).off('click');
        $('#' + historySearchBtnId).click(function () {
            var payload = getSearchPayload();
            loadHistoryData(payload);
        });
    }
    
    function getSearchPayload() {
        var payload = {
            filterType: $("#" + historyQuickFilterId).val(),
            fromDateStr: $("#" + fromDateId).val(),
            toDateStr: $("#" + toDateId).val(),
        };
        return payload;
    }

    function setupDatePickers() {
        var datepicker = $.fn.datepicker.noConflict();
        $.fn.bootstrapDP = datepicker;
        $('#' + fromDateId).datepicker({
            beforeShow: function() {
                setTimeout(function(){
                    $('.ui-datepicker').css('z-index', 99999999999999);
                }, 0);
            }
        });
        $('#' + toDateId).datepicker({
            beforeShow: function() {
                setTimeout(function(){
                    $('.ui-datepicker').css('z-index', 99999999999999);
                }, 0);
            }
        });
        $('#' + historyQuickFilterId).change(function() {
           console.log("historyQuickFilterId: ", this.value);
           updateDisableDatePickers(this.value);
        });
    }

    function updateDisableDatePickers(type) {
        var disabled = type !== "期間";
        $('#' + fromDateId).datepicker("option", "disabled", disabled);
        $('#' + toDateId).datepicker("option", "disabled", disabled);
    }

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

    function loadHistoryData(payload) {
        payload = payload ? payload : {};
        var payloadStr = JSON.stringify(payload);
        $('body').loadingModal({
            position: 'auto',
            text: 'ローディング...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/user/sendMailHistoryData",
            data: payloadStr,
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                $('body').loadingModal('hide');
                if (data && data.status) {
                    histories = data.list;
                    console.log(data.list);
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
    }

    function updateData() {
        showHistoryData(sendMailHistoryTableId, histories);
    }

    function showHistoryData(tableId, data) {
        destroySortHistory();
        removeAllRow(tableId, replaceHistoryHTML);
        if (data && data.length > 0) {
            var html = replaceHistoryHTML;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("historyRow", selectedRow);
        }
        selectFirstRow();
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
                sortList: [[0, 1], [1, 0]]
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

    function showHistory() {
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = histories[index];
        showMailContent(rowData);
    }

    function selectFirstRow() {
        if (histories && histories.length > 0) {
            var firstTr = $('#' + sendMailHistoryTableId).find(' tbody tr:first');
            firstTr.addClass('highlight-selected').siblings().removeClass('highlight-selected');
            var index = firstTr[0].getAttribute("data");
            var rowData = histories[index];
            showMailContent(rowData);
            selectedRowData = rowData;
        }
    }

    function selectedRow() {
        $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
        showHistory.call(this);
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = histories[index];
        selectedRowData = rowData;
    }

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function showMailContent(data) {
        var mailSubjectDiv = document.getElementById(mailSubjectDivId);
        var mailAttachmentDiv = document.getElementById(mailAttachmentDivId);
        mailSubjectDiv.innerHTML = "";
        showMailBodyContent({body: ""});
        mailAttachmentDiv.innerHTML = "";
        if (data) {
            mailSubjectDiv.innerHTML = '<div class="mailbox-read-info">' +
                '<h5><b>' + data.subject + '</b></h5>' +
                '<h6>送信者:&nbsp;' + data.from + '&nbsp;&nbsp;&nbsp;&nbsp;受信者:&nbsp;' + data.to + '<span class="mailbox-read-time pull-right">' + data.sentAt + '</span></h6>' +
                '</div>';
            showMailBodyContent(data);
            mailAttachmentDiv.innerHTML = "添付ファイル";
        }
    }

    function showMailBodyContent(data) {
        data.body = data.body.replace(/(?:\r\n|\r|\n)/g, '<br />');
        var mailBodyDiv = document.getElementById(mailBodyDivId);
        mailBodyDiv.innerHTML = data.body;
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

})(jQuery);
