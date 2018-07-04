(function () {
    "use strict";
    var usersTableId = 'usersTable';
    var users = [];
    var userDataTable;
    var selectedRowData;

    var replaceUserHTML = '<tr role="row" class="hidden">' +
        '<td name="userRow" rowspan="1" colspan="4" data="userName"><span></span></td>' +
        '<td name="userRow" rowspan="1" colspan="4" data="name"><span></span></td>' +
        '<td class="fit action" rowspan="1" colspan="1" data="id"><button name="selectUser" type="button">編集</button></td>' +
        '<td class="fit action" rowspan="1" colspan="1" data="id"><button name="deleteUser" type="button">削除</button></td>' +
        '</tr>';
    $(function () {
        loadUserData();
        initStickyHeader();
        addEventListeners();
        setTimeout(function () {
            loadUserData();
        }, 10000)
    });

    function addEventListeners() {
    }

    function enableResizeColums() {
        $("#" + usersTableId).colResizable(
            {
                disable: true,
            }
        );
        $("#" + usersTableId).colResizable(
            {
                resizeMode: 'overflow',
            }
        );
    }

    function loadUserData() {
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
            url: "/admin/userManagementData",
            cache: false,
            timeout: 600000,
            success: function (data) {
                $('body').loadingModal('hide');
                console.log("loadUserData: ", data);
                if (data && data.status) {
                    users = data.list;
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
        showUserData(usersTableId, users);
    }

    function showUserData(tableId, data) {
        destroySortUser();
        removeAllRow(tableId, replaceUserHTML);
        if (data && data.length > 0) {
            var html = replaceUserHTML;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
        }
        setButtonClickListener("selectUser", function () {
            var index = $(this).closest('tr')[0].getAttribute("data");
            var rowData = users[index];
            selectedRow.call(this);
            selectUser(rowData, index);
        });
        setButtonClickListener("deleteUser", function () {
            var index = $(this).closest('tr')[0].getAttribute("data");
            deleteUser(index);
        });
        initSortUser();
        updateUserDataTrigger(tableId);
        enableResizeColums();
    }

    function selectUser(user, index) {
    }

    function deleteUser(index) {
    }

    function updateUserDataTrigger(tableId) {
        $("#" + tableId).trigger("updateAll", [ true, function () {

        } ]);
    }

    function destroySortUser() {
        if (!!userDataTable) {
            userDataTable.destroy();
        }
    }

    function initSortUser() {
        $("#" + usersTableId).tablesorter(
            {
                theme: 'default',
                headers: {
                    2: {
                        sorter: false
                    },
                },
                sortList: []
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

    function setButtonClickListener(name, callback) {
        $("button[name='" + name + "']").off('click');
        $("button[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function selectedRow() {
        $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = users[index];
        selectedRowData = rowData;
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
                    "transform": "translate(0px, " + $(this).scrollTop() + "px)"
                });
        });
    }

})(jQuery);
