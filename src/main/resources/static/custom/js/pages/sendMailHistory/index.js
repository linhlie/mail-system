(function () {
    "use strict";
    var sendMailHistoryTableId = 'sendMailHistory';
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailAttachmentDivId = 'mailAttachment';
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
        '</tr>';

    $(function () {
        loadHistoryData();
        initStickyHeader();
    });

    function enableResizeColums() {
        $("#" + sendMailHistoryTableId).colResizable(
            {
                resizeMode: 'overflow',
            }
        );
    }

    function loadHistoryData() {
        $('body').loadingModal({
            position: 'auto',
            text: 'ローディング...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/user/sendMailHistoryData",
            cache: false,
            timeout: 600000,
            success: function (data) {
                $('body').loadingModal('hide');
                if (data && data.status) {
                    histories = data.list;
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
        initSortHistory();
        selectFirstRow();
        enableResizeColums();
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
        if (rowData && rowData && rowData.messageId) {
            showMail(rowData.messageId, function (result) {
                showMailContent(result);
            }, rowData.range);
        }
    }

    function selectFirstRow() {
        if (histories && histories.length > 0) {
            var firstTr = $('#' + sendMailHistoryTableId).find(' tbody tr:first');
            firstTr.addClass('highlight-selected').siblings().removeClass('highlight-selected');
            var index = firstTr[0].getAttribute("data");
            var rowData = histories[index];
            if (rowData && rowData.messageId) {
                showMail(rowData.messageId, function (result) {
                    showMailContent(result);
                }, rowData.range);
            }
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

    function showMail(messageId, callback, matchRange) {
        messageId = messageId.replace(/\+/g, '%2B');
        var url = "/user/matchingResult/email?messageId=" + messageId;
        if(matchRange && matchRange.length > 0) {
            url = url + "&matchRange=" + matchRange
        }
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: url,
            cache: false,
            timeout: 600000,
            success: function (data) {
                var result;
                if (data.status) {
                    if (data.list && data.list.length > 0) {
                        result = data.list[0];
                    }
                }
                if (typeof callback === "function") {
                    callback(result);
                }
            },
            error: function (e) {
                console.error("getMail ERROR : ", e);
                if (typeof callback === "function") {
                    callback();
                }
            }
        });
    }

    function showMailContent(data) {
        var mailSubjectDiv = document.getElementById(mailSubjectDivId);
        var mailBodyDiv = document.getElementById(mailBodyDivId);
        var mailAttachmentDiv = document.getElementById(mailAttachmentDivId);
        mailSubjectDiv.innerHTML = "";
        showMailBodyContent({originalBody: ""});
        mailAttachmentDiv.innerHTML = "";
        if (data) {
            mailSubjectDiv.innerHTML = '<div class="mailbox-read-info">' +
                '<h5><b>' + data.subject + '</b></h5>' +
                '<h6>送信者: ' + data.from + '<span class="mailbox-read-time pull-right">' + data.receivedAt + '</span></h6>' +
                '</div>';
            showMailBodyContent(data);
            mailAttachmentDiv.innerHTML = "添付ファイル";
        }
    }

    function showMailBodyContent(data) {
        data.originalBody = data.originalBody.replace(/(?:\r\n|\r|\n)/g, '<br />');
        var mailBodyDiv = document.getElementById(mailBodyDivId);
        mailBodyDiv.innerHTML = data.originalBody;
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
