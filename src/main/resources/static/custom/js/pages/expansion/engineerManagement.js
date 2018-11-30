
(function () {
    var partnerComboBoxId = "partnerId";
    var partnerNameComboBoxId = "partnerNameComboBox";
    var partnerIdComboBoxId = "partnerIdComboBox";
    var partnerTableNotGoodId = "partnerTable";
    var engineerAddBtnId = "#engineerAdd";
    var engineerUpdateBtnId = "#engineerUpdate";
    var manuallyExtendBtnId = "#extend";
    var autoExtendCheckboxId = "#autoExtend";
    var extendMonthInputId = "#extendMonth";
    var engineerClearBtnId = "#engineerClear";
    var filterEngineerBtnId = "#filterEngineerBtn";
    var lastMonthActiveId = "#lastMonthActive";
    var employmentStatusSelectId = "#employmentStatus";
    var formId = "#engineerForm";
    var checkboxNextSelectId = "#checkboxNext";
    var engineerTableId = "engineer";
    var skillSheetTxtId = "#skillSheetEngineer";
    var engineers = null;
    var updatingEngineerId = null;
    var partners = null;
    var introductionId = "introduction";

    var selectedSourceTableRow;
    var idEngineerSelected = -1;
    var currentSortOrder;

    var formFields = [
        {type: "input", name: "lastName"},
        {type: "input", name: "firstName"},
        {type: "input", name: "kanaLastName"},
        {type: "input", name: "kanaFirstName"},
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
        {type: "input", name: "skillSheet"},
        {type: "input", name: "initial"},
        {type: "tinymce", name: "introduction"},
    ];

    var GroupPartnerRowTypes = {
        ORIGINAL: "original",
        NEW: "add",
    }

    var replaceRow = '<tr role="row" class="hidden">' +
        '<td name="editEngineer" rowspan="1" colspan="1" data="name" style="cursor: pointer;"><span></span></td>' +
        '<td name="editEngineer" rowspan="1" colspan="1" data="partnerName" style="cursor: pointer;"><span></span></td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="active">' +
        '<img class="hidden" style="padding: 2px; width:14px; height: 14px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="autoExtend">' +
        '<img class="hidden" style="padding: 2px; width:14px; height: 14px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="dormant">' +
        '<img class="hidden" style="padding: 2px; width:14px; height: 14px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '<td name="deleteEngineer" class="fit action deleteEngineerRow" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '</tr>';
    
    var partnerReplaceRow = '<tr name="group-partner" data-type="original" role="row" class="hidden">' +
    	'<td rowspan="1" colspan="1" data="name"><span></span></td>' +
    	'<td rowspan="1" colspan="1" data="partnerCode"><span></span></td>' +
    	'<td name="deleteGroupPartner" class="fit action" rowspan="1" colspan="1" data="id">' +
    	'<button type="button">削除</button>' +
    	'</td>' +
    	'</tr>';

    $(function () {
        initIntroductionEditor();
        initLastMonthActive();
        filterTypeChangeListener();
        initStickyHeader();
        partnerNameComboBoxListener();
        partnerIdComboBoxListener();
        setupDatePickers();
        setButtonClickListenter(engineerAddBtnId, addEngineerOnClick);
        setButtonClickListenter(engineerUpdateBtnId, updateEngineerOnClick);
        setButtonClickListenter(engineerClearBtnId, clearEngineerOnClick);
        setButtonClickListenter(manuallyExtendBtnId, manuallyExtendOnClick);
        setButtonClickListenter(filterEngineerBtnId, filterEngineerOnClick);
        draggingSetup();
        loadEngineers();
        loadBusinessPartner();
        $(autoExtendCheckboxId).change(function() {
            updateExtendFields();
        });
        $(employmentStatusSelectId).change(function() {
            updatePartnerComboBox(partners);
        });
    });
    
    function initIntroductionEditor() {
        tinymce.init({
            force_br_newlines : true,
            force_p_newlines : false,
            forced_root_block : '',
            selector: '#' + introductionId,
            language: 'ja',
            theme: 'modern',
            statusbar: false,
            height: 150,
            plugins: [
                'advlist autolink link image lists charmap preview hr anchor pagebreak',
                'searchreplace visualblocks visualchars code insertdatetime nonbreaking',
                'table contextmenu directionality template paste textcolor'
            ],
            menubar: 'edit view insert format table',
            toolbar: 'undo redo | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image',
            init_instance_callback: function (editor) {
                editor.on('Change', function (e) {

                });
            }
        });
    }

    function initSortEngineer() {
        $("#" + engineerTableId).css("transform", "translateY(-10px)");
        $("#" + engineerTableId).trigger("destroy");
        if(currentSortOrder){
            $("#" + engineerTableId).tablesorter(
                {
                    headers: {
                        2: {
                            sorter: false
                        },
                        3: {
                            sorter: false
                        },
                        4: {
                            sorter: false
                        },
                        5: {
                            sorter: false
                        }
                    },
                    sortList: currentSortOrder
                })
                .bind('sortEnd', function(event) {
                    currentSortOrder = event.target.config.sortList;
                    setNextRow(selectedSourceTableRow);
                    console.log("sortEnd");
                });
        }else{
            $("#" + engineerTableId).tablesorter(
                {
                    headers: {
                        2: {
                            sorter: false
                        },
                        3: {
                            sorter: false
                        },
                        4: {
                            sorter: false
                        },
                        5: {
                            sorter: false
                        }
                    },
                    sortList: [[0,0]]
                })
                .bind('sortEnd', function(event) {
                    currentSortOrder = event.target.config.sortList;
                    setNextRow(selectedSourceTableRow);
                    console.log("sortEnd");
                });
        }
    }

    function updateExtendFields() {
        var enable = $(autoExtendCheckboxId).is(":checked");
        disableManuallyExtend(!enable || updatingEngineerId == null);
        $(extendMonthInputId).attr('readonly', !enable);
    }

    function initLastMonthActive() {
        var now = new Date();
        var selectedMonth = now.getFullYear() + "年" + (now.getMonth() + 2) + "月";
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
        });
        $(lastMonthActiveId).MonthPicker('option', 'Disabled', true);
    }

    function filterTypeChangeListener() {
    	$('#filterEngineerFilterTime').change(function(){
    		    var disabled = $(this).is(':checked');
    		    $(lastMonthActiveId).MonthPicker('option', 'Disabled', !disabled);
    	});
    	
    	$('#enableEngineerFilterTime').change(function(){
		    var disabled = $(this).is(':checked');
		    $('#filterEngineerFilterTime').prop('disabled', !disabled);
		    $('#filterEngineerFilterNull').prop('disabled', !disabled);
		    if(!disabled){
			    $('#filterEngineerFilterTime').prop('checked', disabled);
			    $('#filterEngineerFilterNull').prop('checked', disabled);
			    $(lastMonthActiveId).MonthPicker('option', 'Disabled', !disabled);
		    }
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
                partners = response.list;
                updatePartnerComboBox(response.list);
                updatePartnerComboBoxTable(response.list);
            }
        }
        function onError() {}

        getBusinessPartners(onSuccess, onError);
    }

    function updatePartnerComboBox(options) {
        options = options ? options.slice(0) : [];
        var lastSelectedId = $('#' + partnerComboBoxId).val();
        var employmentStatus = $(employmentStatusSelectId).val();
        employmentStatus = parseInt(employmentStatus);
        $('#' + partnerComboBoxId).empty();
        $('#' + partnerComboBoxId).append($('<option>', {
            selected: true,
            disabled: true,
            value: "",
            text : "選んでください",
        }));
        $.each(options, function (i, item) {
            if(isNaN(employmentStatus) || (employmentStatus >= 1 && employmentStatus <= 4 && item.ourCompany)
                || (employmentStatus >= 5 && employmentStatus <= 8 && !item.ourCompany))
            $('#' + partnerComboBoxId).append($('<option>', {
                value: item.id,
                text : item.name,
                selected: item.id.toString() === lastSelectedId
            }).attr('data-id',item.id));
        });
    }

    function addEngineerOnClick() {
        clearFormValidate();
        var validated = engineerFormValidate();
        if(!validated) return;
        var data = getFormData();
        var addRemovePartnerNotGoodIds = getAddRemovePartnerNotGoodIds();
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    title: "",
                    content: "保存に成功しました",
                    onClose: function () {
                        selectedSourceTableRow = null;
                        loadEngineers();
                        clearEngineerOnClick();
                    }
                });
            } else {
                $.alert("保存に失敗しました");
            }
        }

        function onError(response) {
            $.alert("保存に失敗しました");
        }
        addEngineer(
            {
                builder: data,
                groupAddIds: addRemovePartnerNotGoodIds.add,
                groupRemoveIds: addRemovePartnerNotGoodIds.remove,
            },
            onSuccess,
            onError
        );
    }

    function getAddRemovePartnerNotGoodIds() {
        var data = {
            add: [],
            remove: [],
        };
        $('#'+ partnerTableNotGoodId + ' > tbody  > tr').each(function(i, row) {
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
            } else if (field.type == "tinymce") {
                form[field.name] = getTinymceEditorContent(field.name);
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
            } else if (field.type == "tinymce") {
                setTinymceEditorContent(field.name, form[field.name]);
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
        var addRemovePartnerNotGoodIds = getAddRemovePartnerNotGoodIds();
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    title: "",
                    content: "保存に成功しました",
                    onClose: function () {
                        selectedSourceTableRow=null;
                        loadEngineers(selectNextRow);
                    }
                });
            } else {
                $.alert("保存に失敗しました");
            }
        }

        function onError(response) {
            $.alert("保存に失敗しました");
        }

        updateEngineer(
            updatingEngineerId,
            {
                builder: data,
                groupAddIds: addRemovePartnerNotGoodIds.add,
                groupRemoveIds: addRemovePartnerNotGoodIds.remove,
            },
            onSuccess,
            onError
        );
    }

    function engineerFormValidate() {
        var validate1 = engineerLastNameValidate();
        var validate2 = engineerKanaLastNameValidate();
        var validate3 = engineerFirstNameValidate();
        var validate4 = engineerKanaFirstNameValidate();
        var validate5 = engineerMailAddressValidate();
        var validate6 = engineerMonetaryMoneyValidate();
        var validate7 = engineerEmploymentStatusValidate();
        var validate8 = engineerPartnerValidate();
        var validate9 = engineerProjectPeriodValidate();
        var validate10 = engineerExtendMonthValidate();
        return validate1 && validate2 && validate3 && validate4 && validate5
            && validate6 && validate7 && validate8 && validate9 && validate10;
    }

    function engineerLastNameValidate() {
        var input = $("input[name='lastName']");
        if(!input.val()) {
            showError.apply(input, ["必須"]);
            return false;
        }
        return true;
    }

    function engineerFirstNameValidate() {
        var input = $("input[name='firstName']");
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

    function engineerKanaLastNameValidate() {
        var input = $("input[name='kanaLastName']");
        if(!input.val()) {
            showError.apply(input, ["必須"]);
            return false;
        }
        return true;
    }

    function engineerKanaFirstNameValidate() {
        var input = $("input[name='kanaFirstName']");
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

    function engineerProjectPeriodValidate() {
        var inputStart = $("input[name='projectPeriodStart']");
        var inputEnd = $("input[name='projectPeriodEnd']");
        var valueStart = inputStart.val();
        var valueEnd = inputEnd.val();
    	var dateFormat = new RegExp('[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])');

        if(valueStart && !dateFormat.test(valueStart)){
        	showErrorProjectPeriodStart.apply(inputStart, ["「開始」不正確なデータ"]);
            return false;
        }
        if(valueEnd && !dateFormat.test(valueEnd)){
        	showErrorProjectPeriodEnd.apply(inputEnd, ["「終了」不正確なデータ"]);
            return false;
        }
        if(valueStart && valueEnd){
            if(valueStart.localeCompare(valueEnd)>0){
            	showErrorProjectPeriodStart.apply(inputStart, ["案件期間「終了」は案件期間「開始」以上"]);
            	return false;
            }        
        }
        return true;
    }
    
    function showErrorProjectPeriodStart(error, selector) {
        selector = selector || ".engineer-form-field";
        var container = $(this).closest(selector);
        container.addClass("has-error");
        container.find("span.form-error").text(error);
    }
    
    function showErrorProjectPeriodEnd(error, selector) {
        selector = selector || ".engineer-form-field";
        var container = $(this).closest(selector);
        container.addClass("has-error");
        container.find("span.form-error").text(error);
    }

    function engineerExtendMonthValidate() {
        var autoExtend = $(autoExtendCheckboxId).is(":checked");
        var input = $(extendMonthInputId);
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
        $(extendMonthInputId).attr('readonly', true);
        clearUpdatingEngineerId();
        resetEngineeTable();
        updatePartnerComboBox(partners);
        loadBusinessPartnerNotGoodData(partnerTableNotGoodId, []);
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

    function loadEngineers(callback) {
        var form = getFilterForm();
        function onSuccess(response) {
            if(response && response.status){
                if(typeof callback == 'function'){
                    loadEngineersData(engineerTableId, response.list, callback);
                }else{
                    loadEngineersData(engineerTableId, response.list);
                }
            }
        }

        function onError(error) {
        }

        getEngineers(form, onSuccess, onError);
    }

    function loadEngineersData(tableId, data, callback) {
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
                var rowSelected = $(this);
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = engineers[index];
                if (rowData && rowData.id) {
                    function onSuccess(response) {
                        if(response && response.status) {
                            if(response.list && response.list.length > 0) {
                                var data = response.list[0];
                                selectedRow(rowSelected.closest('tr'));
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
            initSortEngineer();
            if(typeof callback == 'function'){
                callback();
            }
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
        setFormData(data);
        updateExtendFields();
        loadBusinessPartnerData(id);
        updatePartnerComboBox(partners);
        disableUpdateEngineer(false);
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
        endInput.val(formatDate(endDate));
    }
    
    function filterEngineerOnClick() {
        loadEngineers();
        clearEngineerOnClick();
    }
    
    function getFilterForm() {
        var filterType = $('input[name=engineerFilter]:checked').val();
        var filterDate = $(lastMonthActiveId + "Alt").val();
        var filterTime = $('#filterEngineerFilterTime').is(":checked");
        var filterTimeNull = $('#filterEngineerFilterNull').is(":checked");
        return {
            filterType: filterType,
            filterDate: filterDate,
            filterTime: filterTime,
        	filterTimeNull: filterTimeNull,
        }
    }
    
    function selectNextRow(){
    	if ($(checkboxNextSelectId).is(":checked") && idEngineerSelected>0){
            selectedSourceTableRow  = null;
            $( ".deleteEngineerRow" ).each(function() {
                var rowSelected = $(this);
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = engineers[index];
                if(rowData){
                    if(rowData.id == idEngineerSelected){
                        selectedSourceTableRow = rowSelected.closest('tr');
                    }
                }
            });
            if(selectedSourceTableRow != null){
                selectNext(selectedSourceTableRow);
            }
    	}else{
    		clearEngineerOnClick();
    	}
    }
    
    function selectNext(rowSelect) {
        var index = rowSelect ? rowSelect.index() : -1;
        if(index>engineers.length) {
        	$.alert("最終行まで更新しました");
        	clearEngineerOnClick();
        } else {
            var findRow = rowSelect.find(".deleteEngineerRow");
            var row = findRow[0].parentNode;
            var index = row.getAttribute("data");
            var rowData = engineers[index];
            
            function onSuccess(response) {
                if(response && response.status) {
                    if(response.list && response.list.length > 0) {
                        var dataEng = response.list[0];
                        selectedRow(rowSelect, rowData.id);
                        doEditEngineer(rowData.id, dataEng);
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
    }
    
    function selectedRow(row) {
        setNextRow(row);
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
    }

    function setNextRow(row){
        if(!row){
            return;
        }
        var indexArray = row.index();
        selectedSourceTableRow = row;
        if(indexArray<engineers.length) {
            var findRow = row.next().find(".deleteEngineerRow");
            var rowtmp = findRow[0].parentNode;
            var index = rowtmp.getAttribute("data");
            var rowData = engineers[index];
            if(rowData){
                idEngineerSelected = rowData.id;
            }else{
                idEngineerSelected = -1;
            }
        }else{
            idEngineerSelected = -1;
        }
    }
    
    function updatePartnerComboBoxTable(options) {
        options = options ? options.slice(0) : [];
        options.sort(comparePartner);
        $('#' + partnerNameComboBoxId).empty();
        $('#' + partnerIdComboBoxId).empty();
        
        $('#' + partnerNameComboBoxId).append($('<option>', {
            selected: true,
            disabled: true,
            value: "",
            text : "選んでください",
        }));
        $('#' + partnerIdComboBoxId).append($('<option>', {
            selected: true,
            disabled: true,
            value: "",
            text : "識別ID",
        }));
        
        $.each(options, function (i, item) {
            $('#' + partnerNameComboBoxId).append($('<option>', {
                value: item.partnerCode,
                text : item.name,
            }).attr('data-id',item.id));
            
            $('#' + partnerIdComboBoxId).append($('<option>', {
                value: item.name,
                text : item.partnerCode,
            }).attr('data-id',item.id));
        });
    }
    
    function partnerNameComboBoxListener() {
        $('#' + partnerNameComboBoxId).off('change');
        $('#' + partnerNameComboBoxId).change(function() {
            var selected = $(this).find("option:selected");
            var name = selected.text();
            var id = selected.attr("data-id");
            var code = this.value;
            addPartnerToGroup.apply(this, [id, name, code]);
            $('#' + partnerNameComboBoxId).prop('selectedIndex',0);
        });
    }
    
    function partnerIdComboBoxListener() {
        $('#' + partnerIdComboBoxId).off('change');
        $('#' + partnerIdComboBoxId).change(function() {
            var selected = $(this).find("option:selected");
            var name = selected.text();
            var id = selected.attr("data-id");
            var code = this.value;
            addPartnerToGroup.apply(this, [id, code, name]);
            $('#' + partnerIdComboBoxId).prop('selectedIndex',0);
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
        setDeletePartnerNotGoodListener();
    }
    
    function addPartnerComboBox() {
        var tr = '<tr role="row">' +
            '<td rowspan="1" colspan="1">' +
            '<select id="partnerNameComboBox" style="width: 100%; border: none; padding: 2px;"></select>' +
            '</td>' +
            '<td rowspan="1" colspan="1">'+
            '<select id="partnerIdComboBox" style="width: 100%; border: none; padding: 2px;"></select>' +
            '</td>' +
            '<td class="fit" rowspan="1" colspan="1">' +
            '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
            '</td>' +
            '</tr>';
        $("#" + partnerTableNotGoodId).append(tr);
    }
    
    function setDeletePartnerNotGoodListener() {
        setRowClickListener("deleteGroupPartner", function () {
            //TODO:
            var tr = $(this).closest('tr');
            var type = tr.attr("data-type");
            if(type == GroupPartnerRowTypes.NEW) {
                tr.remove();
            } else if (type == GroupPartnerRowTypes.ORIGINAL) {
                tr.addClass("hidden");
            }
        });
    }
    
    function loadBusinessPartnerData(engineerId) {
        function onSuccess(response) {
            if(response && response.status) {
            	loadBusinessPartnerNotGoodData(partnerTableNotGoodId, response.list);
            }
        }
        function onError() {}

        getBusinessPartnersNotGood(engineerId, onSuccess, onError);
    }
    
    function loadBusinessPartnerNotGoodData(tableId, data) {
        removeAllRow(tableId, partnerReplaceRow);
        if (data.length > 0) {
            var html = partnerReplaceRow;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i].partner, i);
            }
            $("#" + tableId + "> tbody").html(html);
            setDeletePartnerNotGoodListener();
        }
        addPartnerComboBox();
        updatePartnerComboBoxTable(partners);
        partnerNameComboBoxListener();
        partnerIdComboBoxListener();
    }

    function getTinymceEditorContent(id) {
        var editor = tinymce.get(id);
        return editor.getContent();
    }

    function setTinymceEditorContent(id, content) {
        content = content || "";
        var editor = tinymce.get(id);
        editor.setContent(content);
        editor.undoManager.clear();
        editor.undoManager.add();
    }

})(jQuery);
