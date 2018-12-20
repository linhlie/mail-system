(function () {
    "use strict";
    var inboxTableId = 'inboxTable';
    var totalEmailId = 'totalEmail';
    var paginationInboxId = 'paginationInbox';

    var listEmailInbox = null;
    var totalEmail = null;
    var start = null;
    var end = null;
    var totalPages = null;
    var currentPage = null;

    var default_filters = {
        "condition": "AND",
        "rules": [
        ],
        "valid": true
    };

    var replaceBody = '<tr role="row" class="hidden">' +
        '<td class="clickable" name="showEmailInbox" rowspan="1" colspan="1" data="to"><span></span></td>' +
        '<td class="clickable" name="showEmailInbox" rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td class="clickable" name="showEmailInbox" rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '<td class="clickable" name="showEmailInbox" rowspan="1" colspan="1" data="hasAttachment"><i></i></td>' +
        '<td class="clickable" name="showEmailInbox" rowspan="1" colspan="1" data="relativeDate"><span></span></td>' +
        '<td align="center" rowspan="1" colspan="1">' +
        '<input type="checkbox" class="selectEmailInbox"/>' +
        '</td>' +
        '</tr>';

    $(function () {
        loadEmailData(default_filters, 0);
    });

    function loadEmailData(filterRule, page) {
        $('body').loadingModal({
            position: 'auto',
            text: '抽出中...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        function onSuccess(response) {
            $('body').loadingModal('hide');
            if (response && response.status) {
                var data  = response.list[0];
                console.log(data);
                listEmailInbox = data.listEmail;
                totalEmail = data.totalEmail;
                start = data.start;
                end = data.end;
                totalPages = data.totalPages;
                console.log(listEmailInbox.length);
            } else {
                console.error("[ERROR] submit failed: ");
            }
            currentPage = page;
            updateData();
        }

        function onError(error) {
            console.error("[ERROR] submit error: ", error);
            $('body').loadingModal('hide');
            currentPage = page;
            updateData();
        }

        filterInbox({
                filterRule: filterRule,
                page: page,
            }, onSuccess, onError);
    }

    function updateData() {
        if(listEmailInbox){
            showInboxTable(listEmailInbox)
            updateTotalEmail(start, end);
            updatePageActive();
        }
    }

    function showInboxTable(data) {
        removeAllRow(inboxTableId, replaceBody);
        if (data.length > 0) {
            var html = replaceBody;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(data[i], i);
            }
            $("#" + inboxTableId + "> tbody").html(html);
            setRowClickListener("showEmailInbox", function () {
                selectedRow($(this).closest('tr'))
            });
        }
    }

    function updateTotalEmail(start, end) {
        var total = "Showing " + (start+1) + " to " + end + " of " + totalEmail + " entries";
        $('#'+totalEmailId).text(total);
    }

    function updatePageActive(){
        $('#'+paginationInboxId).twbsPagination({
            totalPages: totalPages,
            visiblePages: 5,
            startPage: currentPage+1,
            next: 'Next',
            prev: 'Prev',
            onPageClick: function (event, page) {
                //fetch content and render here
                loadEmailData(default_filters, page-1)
            }
        });
    }

    function addRowWithData(data, index) {
        var table = document.getElementById(inboxTableId);
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

                if (cellNode.nodeName == "I") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    if (cellData) {
                        cellNode.className = 'fa fa-paperclip';
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

})(jQuery);
