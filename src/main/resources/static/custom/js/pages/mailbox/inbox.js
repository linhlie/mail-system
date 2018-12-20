(function () {
    "use strict";
    var inboxTableId = 'inboxTable';
    var totalEmailId = 'totalEmail';
    var paginationInboxId = 'paginationInbox';
    var inboxBuilderId = 'inbox-builder';
    var btnFilterId = "#btnFilter";

    var listEmailInbox = null;
    var totalEmail = null;
    var start = null;
    var end = null;
    var totalPages = null;
    var currentPage = null;

    var filterConditionKey = 'filterConditionInboxEmail';
    var filterCondition = null;

    var default_filter_condition = {
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
        var default_plugins = [
            'sortable',
            'filter-description',
            'unique-filter',
            'bt-tooltip-errors',
            'bt-selectpicker',
            'bt-checkbox',
            'invert',
        ];

        var default_filters = [{
            id: '0',
            label: '送信者',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '1',
            label: '受信者',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '9',
            label: 'CC',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '10',
            label: 'BCC',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '11',
            label: '全て(受信者・CC・BCC)',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '12',
            label: 'いずれか(受信者・CC・BCC)',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '2',
            label: '件名',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '3',
            label: '本文',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '13',
            label: '全て(件名・本文)',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '14',
            label: 'いずれか(件名・本文)',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '4',
            label: '数値',
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
            validation: {
                callback: numberValidator
            },
        }, {
            id: '5',
            label: '数値(上代)',
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
            validation: {
                callback: numberValidator
            },
        }, {
            id: '6',
            label: '数値(下代)',
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
            validation: {
                callback: numberValidator
            },
        }, {
            id: '7',
            label: '添付ファイル',
            type: 'integer',
            input: 'radio',
            values: {
                1: '有り',
                0: '無し'
            },
            colors: {
                1: 'success',
                0: 'danger'
            },
            operators: ['equal']
        }, {
            id: '8',
            label: '受信日',
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less']
        }, {
            id: '15',
            label: 'マーク',
            type: 'string',
            operators: ['equal', 'not_equal']
        }];

        var default_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: null,
            lang: globalConfig.default_lang,
        };


        $('#'+inboxBuilderId).queryBuilder(default_configs);
        loadEmailData(0);
        setButtonClickListenter(btnFilterId, showSettingCondition);
    });

    function loadEmailData(page) {
        filterCondition = getBeforeFilterCondition();
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
                filterRule: filterCondition,
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
                loadEmailData(page-1)
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

    function numberValidator(value, rule) {
        if (!value || value.trim().length === 0) {
            return "Value can not be empty!";
        } else if (rule.operator.type !== 'in') {
            value = fullWidthNumConvert(value);
            value = value.replace(/，/g, ",");
            var pattern = /^\d+(,\d{3})*(\.\d+)?$/;
            var match = pattern.test(value);
            if(!match){
                return "Value must be a number greater than or equal to 0";
            }
        }
        return true;
    }
    
    function showSettingCondition() {
        showModal(loadEmailData);
    }

    function showModal(callback) {
        $('#dataModal').modal();

        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {
            var word = $( '#word').val();
            var wordExclusion = $( '#wordExclusion').val();
            if(typeof callback === "function"){
                var condition = $('#'+inboxBuilderId).queryBuilder('getRules');
                console.log(condition);
                if(condition != null){
                    //do to
                    $('#dataModal').modal('hide');
                }
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

    function getBeforeFilterCondition() {
        var condition = sessionStorage.getItem(filterConditionKey);
        if(condition){
            return condition;
        }
        return default_filter_condition;
    }

    function saveFilterCondition(condition) {
        sessionStorage.setItem(filterConditionKey, condition);
    }

})(jQuery);
