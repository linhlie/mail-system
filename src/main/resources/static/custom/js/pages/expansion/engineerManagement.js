
(function () {
    var partnerComboBoxId = "partnerId";
    var engineerAddBtnId = "#engineerAdd";
    var engineerUpdateBtnId = "#engineerUpdate";
    var manuallyExtendBtnId = "#extend";
    var engineerClearBtnId = "#engineerClear";
    var filterEngineerBtnId = "#filterEngineerBtn";
    var lastMonthActiveId = "#lastMonthActive";
    var formId = "#engineerForm";
    var engineerTableId = "engineer";
    var engineers = null;
    var updatingEngineerId = null;

    var formFields = [
        {type: "input", name: "name"},
        {type: "input", name: "kanaName"},
        {type: "input", name: "mailAddress"},
        {type: "select", name: "employmentStatus"},
        {type: "select", name: "partnerId"},
        {type: "input", name: "projectPeriodStart"},
        {type: "input", name: "projectPeriodEnd"},
        {type: "checkbox", name: "autoExtend"},
        {type: "input", name: "extendMonth"},
        {type: "textarea", name: "matchingWord"},
        {type: "textarea", name: "notGoodWord"},
        {type: "input", name: "monetaryMoney"},
        {type: "input", name: "stationLine"},
        {type: "input", name: "stationNearest"},
        {type: "input", name: "commutingTime"},
        {type: "checkbox", name: "dormant"},
    ];

    var GroupPartnerRowTypes = {
        ORIGINAL: "original",
        NEW: "add",
    }

    var replaceRow = '<tr role="row" class="hidden">' +
        '<td rowspan="1" colspan="1" data="name"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="partnerName"><span></span></td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="active">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="autoExtend">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="dormant">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '<td name="editEngineer" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">編集</button>' +
        '</td>' +
        '<td name="deleteEngineer" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '</tr>';

    $(function () {
        initLastMonthActive();
        filterTypeChangeListener();
        initStickyHeader();
        setupDatePickers();
        setButtonClickListenter(engineerAddBtnId, addEngineerOnClick);
        setButtonClickListenter(engineerUpdateBtnId, updateEngineerOnClick);
        setButtonClickListenter(engineerClearBtnId, clearEngineerOnClick);
        setButtonClickListenter(manuallyExtendBtnId, manuallyExtendOnClick);
        setButtonClickListenter(filterEngineerBtnId, filterEngineerOnClick);
        draggingSetup();
        loadEngineers();
        loadBusinessPartner();
    });

    function initLastMonthActive() {
        var now = new Date();
        var selectedMonth = now.getFullYear() + "年" + (now.getMonth() + 1) + "月";
        $(lastMonthActiveId).MonthPicker({
            Button: false,
            i18n: {
                year: '年',
                prevYear: '前年',
                nextYear: '次年',
                next12Years: '12年間ジャンプフォワード',
                prev12Years: '12年間ジャンプバック',
                nextLabel: '次',
                prevLabel: '前',
                buttonText: '月のセレクタを開く',
                jumpYears: '年ジャンプ',
                backTo: '年に戻る',
                months: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
            },
            SelectedMonth: selectedMonth,
            MonthFormat: 'yy年m月',
            AltFormat: '@',
            AltField: lastMonthActiveId + "Alt",
            Disabled: true,
        });
    }

    function filterTypeChangeListener() {
        $("input[name='engineerFilter']").click(function() {
            var disabled = this.value !== "4";
            $(lastMonthActiveId).MonthPicker('option', 'Disabled', disabled);
        });
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
    
    function loadBusinessPartner() {
        function onSuccess(response) {
            if(response && response.status) {
                updatePartnerComboBox(response.list);
            }
        }
        function onError() {}

        getBusinessPartnersForEngineer(onSuccess, onError);
    }

    function updatePartnerComboBox(options) {
        options = options ? options.slice(0) : [];
        $('#' + partnerComboBoxId).empty();
        $('#' + partnerComboBoxId).append($('<option>', {
            selected: true,
            disabled: true,
            value: "",
            text : "選んでください",
        }));
        $.each(options, function (i, item) {
            $('#' + partnerComboBoxId).append($('<option>', {
                value: item.id,
                text : item.name,
            }).attr('data-id',item.id));
        });
    }

    function partnerComboBoxListener() {
        $('#' + partnerComboBoxId).off('change');
        $('#' + partnerComboBoxId).change(function() {
            var selected = $(this).find("option:selected");
            var name = selected.text();
            var id = selected.attr("data-id");
            var code = this.value;
            addPartnerToGroup.apply(this, [id, name, code]);
            $('#' + partnerComboBoxId).prop('selectedIndex',0);
        });
    }

    function addPartnerToGroup(id, name, code) {
        var tr = '<tr name="group-partner" data-id="' + id + '" data-type="add" role="row">' +
            '<td rowspan="1" colspan="1" data="name"><span>' + name + '</span></td>' +
            '<td rowspan="1" colspan="1" data="partnerCode"><span>' + code + '</span></td>' +
            '<td name="deleteGroupPartner" class="fit action" rowspan="1" colspan="1" data="id">' +
            '<button type="button">削除</button>' +
            '</td> </tr>';
        $(this).closest('table').find('tr:last').before(tr);
        setDeleteGroupPartnerListener();
    }

    function addEngineerOnClick() {
        console.log("addEngineerOnClick");
        clearFormValidate();
        var validated = engineerFormValidate();
        if(!validated) return;
        var data = getFormData();
        console.log("Add engineer: ", data);
        function onSuccess(response) {
            if(response && response.status) {
                $.alert("保存に成功しました");
                loadEngineers();
                clearEngineerOnClick();
            } else {
                $.alert("保存に失敗しました");
            }
        }

        function onError(response) {
            $.alert("保存に失敗しました");
        }
        addEngineer(data, onSuccess, onError)
    }

    function getAddRemoveGroupPartnerIds() {
        var data = {
            add: [],
            remove: [],
        };
        $('#'+ partnerGroupTableId + ' > tbody  > tr').each(function(i, row) {
            var $row = $(row)
            var type = $row.attr("data-type");
            var id = $row.attr("data-id");
            id = parseInt(id);
            if(type == GroupPartnerRowTypes.NEW) {
                if(!isNaN(id) && data.add.indexOf(id) < 0) {
                    data.add.push(id);
                }
            } else if (type == GroupPartnerRowTypes.ORIGINAL) {
                if($row.hasClass("hidden")) {
                    if(!isNaN(id) && data.remove.indexOf(id) < 0) {
                        data.remove.push(id);
                    }
                }
            }
        });
        return data;
    }

    function getFormData() {
        var form = {};
        for (var i = 0; i < formFields.length; i++) {
            var field = formFields[i];
            if(field.type == "checkbox"){
                form[field.name] = $("input" + "[name='" + field.name + "']").is(':checked');
            } else if (field.type == "radio") {
                form[field.name] = $('input[name=' + field.name + ']:checked', formId).val()
            } else {
                form[field.name] = $("" + field.type + "[name='" + field.name + "']").val();
            }
        }
        return form;
    }

    function setFormData(form) {
        for (var i = 0; i < formFields.length; i++) {
            var field = formFields[i];
            if(field.type == "checkbox"){
                $("input" + "[name='" + field.name + "']").prop('checked', form[field.name]);
            } else if (field.type == "radio") {
                $("input[name=" + field.name + "][value=" + form[field.name] + "]").prop('checked', true);
            } else {
                $("" + field.type + "[name='" + field.name + "']").val(form[field.name]);
            }
        }
    }

    function updateEngineerOnClick() {
        clearFormValidate();
        var validated = engineerFormValidate();
        if(!validated) return;
        var data = getFormData();
        function onSuccess(response) {
            if(response && response.status) {
                $.alert("保存に成功しました");
                loadEngineers();
                clearEngineerOnClick();
            } else {
                $.alert("保存に失敗しました");
            }
        }

        function onError(response) {
            $.alert("保存に失敗しました");
        }

        updateEngineer(
            updatingEngineerId,
            data,
            onSuccess,
            onError
        );
    }

    function engineerFormValidate() {
        var validate1 = engineerNameValidate();
        var validate2 = engineerKanaNameValidate();
        var validate3 = engineerMailAddressValidate();
        var validate4 = engineerMonetaryMoneyValidate();
        var validate5 = engineerEmploymentStatusValidate();
        var validate6 = engineerPartnerValidate();
        var validate7 = engineerProjectPeriodStartValidate();
        var validate8 = engineerProjectPeriodEndValidate();
        var validate9 = engineerExtendMonthValidate();
        return validate1 && validate2 && validate3 && validate4 && validate5
            && validate6 && validate7 && validate8 && validate9;
    }

    function engineerNameValidate() {
        var input = $("input[name='name']");
        if(!input.val()) {
            showError.apply(input, ["必須"]);
            return false;
        }
        return true;
    }

    function showError(error, selector) {
        selector = selector || "div.form-group.row";
        var container = $(this).closest(selector);
        container.addClass("has-error");
        container.find("span.form-error").text(error);
    }

    function engineerKanaNameValidate() {
        var input = $("input[name='kanaName']");
        if(!input.val()) {
            showError.apply(input, ["必須"]);
            return false;
        }
        return true;
    }
    
    function engineerMailAddressValidate() {
        var input = $("input[name='mailAddress']");
        var email = input.val()
        if( email && !validateEmail(email)) {
            showError.apply(input, ["無効なメールアドレス"]);
            return false;
        }
        return true;
    }
    
    function engineerMonetaryMoneyValidate() {
        var input = $("input[name='monetaryMoney']");
        var value = input.val();
        if(value && !numberValidator(value)) {
            showError.apply(input, ["無効入力"]);
            return false;
        }
        return true;
    }

    function engineerEmploymentStatusValidate() {
        var input = $("select[name='employmentStatus']");
        var value = input.val();
        if(!value) {
            showError.apply(input, ["選んでください"]);
            return false;
        }
        return true;
    }

    function engineerPartnerValidate() {
        var input = $("select[name='partnerId']");
        var value = input.val();
        if(!value) {
            showError.apply(input, ["選んでください"]);
            return false;
        }
        return true;
    }

    function engineerProjectPeriodStartValidate() {
        var input = $("input[name='projectPeriodStart']");
        var value = input.val();
        if(!value) {
            showError.apply(input, ["必須", "div.engineer-form-field"]);
            return false;
        }
        return true;
    }

    function engineerProjectPeriodEndValidate() {
        var input = $("input[name='projectPeriodEnd']");
        var value = input.val();
        if(!value) {
            showError.apply(input, ["必須", "div.engineer-form-field"]);
            return false;
        }
        return true;
    }

    function engineerExtendMonthValidate() {
        var autoExtend = $('#autoExtend').is(":checked");
        var input = $("input[name='extendMonth']");
        var value = input.val();
        if(autoExtend && !value) {
            showError.apply(input, ["必須", "div.engineer-form-field"]);
            return false;
        }
        return true;
    }

    function clearEngineerOnClick() {
        resetForm();
        clearFormValidate();
        disableUpdateEngineer(true);
        disableManuallyExtend(true);
        clearUpdatingEngineerId();
        resetEngineeTable();
    }

    function resetEngineeTable() {
        $("#" + engineerTableId).find('tr.highlight-selected').removeClass('highlight-selected');
    }

    function clearUpdatingEngineerId() {
        updatingEngineerId = null;
    }

    function resetForm() {
        $(formId).trigger("reset");
    }

    function clearFormValidate() {
        $(formId).find(".has-error").removeClass('has-error');
    }

    function loadEngineers() {
        var form = getFilterForm();
        function onSuccess(response) {
            if(response && response.status){
                loadEngineersData(engineerTableId, response.list);
            }
        }

        function onError(error) {

        }

        getEngineers(form, onSuccess, onError);
    }

    function loadEngineersData(tableId, data) {
        engineers = data;
        removeAllRow(tableId, replaceRow);
        if (engineers.length > 0) {
            var html = replaceRow;
            for (var i = 0; i < engineers.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("deleteEngineer", function () {

                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = engineers[index];
                if (rowData && rowData.id) {
                    doDeleteEngineer(rowData.id);
                }
            });
            setRowClickListener("editEngineer", function () {
                var $this = $(this);
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = engineers[index];
                if (rowData && rowData.id) {
                    function onSuccess(response) {
                        if(response && response.status) {
                            if(response.list && response.list.length > 0) {
                                var data = response.list[0];
                                $this.closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
                                doEditEngineer(rowData.id, data);
                            } else {
                                $.alert("技術者が存在しません。");
                            }
                        } else {
                            $.alert("技術者情報のロードに失敗しました。");
                        }
                    }
                    
                    function onError() {
                        $.alert("技術者情報のロードに失敗しました。");
                    }
                    getEngineer(rowData.id, onSuccess, onError);
                }
            });
        }
    }

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function addRowWithData(tableId, data, index) {
        var table = document.getElementById(tableId);
        if (!table) return "";
        var rowToClone = table.rows[1];
        var row = rowToClone.cloneNode(true);
        row.setAttribute("data-id", data.id);
        row.setAttribute("data", index);
        row.className = undefined;
        var cells = row.cells;
        for (var i = 0; i < cells.length; i++) {
            var cell = cells.item(i);
            var cellKey = cell.getAttribute("data");
            if (!cellKey) continue;
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if (cellNode) {
                if(cellNode.nodeName == "IMG") {
                    var cellData = data[cellKey];
                    cellNode.className = !!cellData ? undefined : cellNode.className;
                } else if (cellNode.nodeName == "SPAN") {
                    var cellData = data[cellKey];
                    cellNode.textContent = cellData;
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

    function doDeleteEngineer(id) {
        function onSuccess() {
            loadEngineers();
            clearEngineerOnClick();
        }
        function onError() {
            $.alert("技術者の削除に失敗しました。");
        }
        $.confirm({
            title: '<b>【技術者の削除】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deleteEngineer(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }

    function doEditEngineer(id, data) {
        clearFormValidate();
        updatingEngineerId = id;
        disableUpdateEngineer(false);
        disableManuallyExtend(false);
        setFormData(data);
    }

    function disableUpdateEngineer(disable) {
        $(engineerUpdateBtnId).prop('disabled', disable);
    }

    function disableManuallyExtend(disable) {
        $(manuallyExtendBtnId).prop('disabled', disable);
    }

    function draggingSetup() {
        var dragging = false;
        $('#dragbar2').mousedown(function(e){
            e.preventDefault();

            dragging = true;
            var dragbar = $('#dragbar2');
            var ghostbar = $('<div>',
                {id:'ghostbar2',
                    css: {
                        width: dragbar.outerWidth(),
                        top: dragbar.offset().top,
                        left: dragbar.offset().left
                    }
                }).appendTo('body');

            $(document).mousemove(function(e){
                ghostbar.css("top",e.pageY);
            });

        });

        $(document).mouseup(function(e){
            if (dragging)
            {
                var container = $('#engineerBox');
                var topHeight = (e.pageY - container.offset().top);
                var tableHeight = Math.floor(topHeight - 10);
                tableHeight = tableHeight > 60 ? tableHeight : 60;
                tableHeight = tableHeight < 400 ? tableHeight : 400;
                $('#engineerBox').css("height", tableHeight + "px");
                $('#ghostbar2').remove();
                $(document).unbind('mousemove');
                dragging = false;
            }
        });
    }

    function setupDatePickers() {
        var datepicker = $.fn.datepicker.noConflict();
        $.fn.bootstrapDP = datepicker;
        $('#projectPeriodStart').datepicker({
            dateFormat: 'yy-mm-dd',
            beforeShow: function() {
                setTimeout(function(){
                    $('.ui-datepicker').css('z-index', 99999999999999);
                }, 0);
            }
        });
        $('#projectPeriodEnd').datepicker({
            dateFormat: 'yy-mm-dd',
            beforeShow: function() {
                setTimeout(function(){
                    $('.ui-datepicker').css('z-index', 99999999999999);
                }, 0);
            }
        });
    }
    
    function manuallyExtendOnClick() {
        var startInput = $("input[name='projectPeriodStart']");
        var endInput = $("input[name='projectPeriodEnd']");
        var start = startInput.val();
        var end = endInput.val();
        if(!end) return;
        var startDate = new  Date(start);
        var endDate = new  Date(end);
        startDate = addDaysToDate(endDate, 1);
        endDate = addMonthsToDate(startDate, 3);
        endDate = addDaysToDate(endDate, -1);
        // startInput.val(formatDate(startDate));
        endInput.val(formatDate(endDate));
    }
    
    function filterEngineerOnClick() {
        loadEngineers();
        clearEngineerOnClick();
    }
    
    function getFilterForm() {
        var filterType = $('input[name=engineerFilter]:checked').val();
        var filterDate = $(lastMonthActiveId + "Alt").val();
        return {
            filterType: filterType,
            filterDate: filterDate,
        }
    }

})(jQuery);
