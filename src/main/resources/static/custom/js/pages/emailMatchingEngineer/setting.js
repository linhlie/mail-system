(function () {
    var engineerTableId = "engineerTable";
    var selectAllCheckBoxId = "#selectAll";
    var extendMonthInputId = "#extendMonth";
    var applyConditionBtnId = "#applyConditionEngineer"
    var engineerClearBtnId = "#clearConditionEngineer";
    var saveDestinationBtnId = '#saveDestinationBtn';
    var getDestinationBtnId = '#getDestinationBtn';
    var extractDestinationBtnId = '#extractDestinationBtn';
    var checkboxNextSelectId = "#checkboxNext";
    var filterEngineerBtnId = "#filterEngineerBtn";
    var removeDatalistItemBtnId = "#dataRemoveItem";
    var submitFormBtnId = '#submitFormBtn';
    var lastMonthActiveId = "#lastMonthActive";
    var formId = "#engineerForm";
    var destinationBuilderId = '#destination-builder';
    var hourlyMoneyBuilderId = '#hourlyMoney-builder';
    var destinationConditionNameId = "#destination-condition-name";
    var engineers = null;
    var selectedSourceTableRow=-1;
    var engineerCondition = null;

    var collapsedPrefixKey = "/user/emailMatchingEngineer/collapsed";
    var destinationPrefixUrlKey = "/user/emailMatchingEngineer/destination";
    var destinationConditionKey = "destinationCondition-email-matching-engineer";
    var destinationConditionNameKey = "destinationConditionName-email-matching-engineer";
    var matchingConditionEmailMatchingEngineerKey = "matchingConditionData-email-matching-engineer";

    var collapseViewPostfix = "-collapse-view-email-matching-engineer";

    var formFields = [
        {type: "input", name: "id"},
        {type: "input", name: "name"},
        {type: "textarea", name: "matchingWord"},
        {type: "textarea", name: "notGoodWord"},
    ];

    var destinationNotificationId = "destination-notification";
    var destinationNotificationNewId = "destination-notification-new";
    var destinationNotificationAccountId = "destination-notification-account";
    var destinationNotificationSentBtnId = "#destination-notification-sent";

    var ENGINEER_MATCHING_CONDITION = 3;

    var NOTIFICATION_NEW = 0;
    var NOTIFICATION_ACCEPT = 1;
    var NOTIFICATION_REJECT = 2;

    var destinationNotificationList = [];

    var default_destination_configs = {};

    var ruleInvalidateIds = [];

    var RULE_NUMBER_ID = 4;
    var RULE_NUMBER_UP_RATE_ID = 5;
    var RULE_NUMBER_DOWN_RATE_ID = 6;

    var ruleNumberId = "ruleNumber";
    var ruleNumberUpRateId = "ruleNumberUpRate";
    var ruleNumberDownRateId = "ruleNumberDownRate";

    var ruleNumberDownRateName = "";
    var ruleNumberUpRateName = "";
    var ruleNumberName = "";

    var ENGINEER_CONDITIONTYPE=4;

    var replaceRow = '<tr role="row" class="hidden">' +
        '<td style= "cursor: pointer;" class="clickable" rowspan="1" colspan="1" data="name" name="engineerRow"><span></span></td>' +
        '<td style= "cursor: pointer;" class="clickable" rowspan="1" colspan="1" data="partnerName" name="engineerRow"><span></span></td>' +
        '<td style= "cursor: pointer;" class="fit" style="text-align: center" rowspan="1" colspan="1" data="active">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '<td class="fit" style="text-align: center; cursor: pointer;" rowspan="1" colspan="1" data="autoExtend" name="engineerRow">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '<td class="fit" style="text-align: center; cursor: pointer;" rowspan="1" colspan="1" data="dormant" name="engineerRow">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '<td style= "cursor: pointer;" align="center" rowspan="1" colspan="1" data="id"><input type="checkbox" class="selectEngineer" name="selectEngineer" checked value="id"/></td>' +
        '</tr>';

    var default_hourlyMoney_rules = {
        condition: "AND",
        rules: [
            {
                id: "4",
                operator: "greater_or_equal",
                type: "string",
                value: "",
            }
        ]
    };

    $(function () {

        var default_filters_houtlyMoney = [];

        ruleNumberDownRateName = $('#'+ruleNumberDownRateId).text();
        ruleNumberUpRateName = $('#'+ruleNumberUpRateId).text();
        ruleNumberName = $('#'+ruleNumberId).text();

        if(!ruleNumberDownRateName || ruleNumberDownRateName==null){
            ruleInvalidateIds.push(RULE_NUMBER_DOWN_RATE_ID);
        }


        if(!ruleNumberUpRateName || ruleNumberUpRateName==null){
            ruleInvalidateIds.push(RULE_NUMBER_UP_RATE_ID);
        }


        if(!ruleNumberName || ruleNumberName==null){
            ruleInvalidateIds.push(RULE_NUMBER_ID);
        }else{
            default_filters_houtlyMoney.splice(0,0,{
                id: RULE_NUMBER_ID,
                label: ruleNumberName,
                type: 'string',
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: numberValidator
                },
            })
        }


        default_destination_configs = getDefaultConditionConfig(ruleNumberDownRateName, ruleNumberUpRateName, ruleNumberName);

        var default_hourlyMoney_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters_houtlyMoney,
            rules: null,
            lang: globalConfig.default_lang,
        };

        $(destinationBuilderId).queryBuilder(default_destination_configs);
        if(ruleNumberName != null && ruleNumberName != ""){
            $(hourlyMoneyBuilderId).queryBuilder(default_hourlyMoney_configs);
        }else{
            $(".engineer-condition-filter").addClass('hidden');
        }

        initLastMonthActive();
        initDuplicateHandle();
        initDomainHandle();
        filterTypeChangeListener();
        initStickyHeader();
        setupDatePickers();
        setButtonClickListenerByName("builder-ec", onExpandCollapseBuilder);
        setButtonClickListenter(applyConditionBtnId, applyConditionOnClick);
        setButtonClickListenter(filterEngineerBtnId, filterEngineerOnClick);
        setButtonClickListenter(engineerClearBtnId, clearEngineerOnClick);
        setButtonClickListenter(saveDestinationBtnId, saveDestinationListData);
        setButtonClickListenter(getDestinationBtnId, getDestinationListData);
        setButtonClickListenter(extractDestinationBtnId, extractDestination);
        setButtonClickListenter(submitFormBtnId, submit);
        setButtonClickListenter(destinationNotificationSentBtnId, sendDestinationConditions);
        draggingSetup();
        loadDefaultSettings();
        loadEngineers();
        loadConditionNotification();
        $(window).on('beforeunload', saveDefaultSettings);
    });

    function sendDestinationConditions() {
        var toAccount = $('#' + destinationNotificationAccountId).val();
        if(!toAccount || toAccount==null){
            $.alert("アカウントを最初に選択してください。");
            return;
        }
        var destinationConditions = $(destinationBuilderId).queryBuilder('getRules');
        if(!destinationConditions) return;
        sendConditions(toAccount, destinationConditions, ENGINEER_MATCHING_CONDITION);
    }

    function sendConditions(toAccount, conditions, conditionType) {
        var condition = JSON.stringify(conditions);
        var sendBtn = $(destinationNotificationSentBtnId);
        sendBtn.button('loading');
        function onSuccess(response) {
            if(response && response.status) {
                $.alert("条件送信に成功しました。");
            } else {
                $.alert("条件送信に失敗しました。");
            }
            sendBtn.button('reset');
        }

        function onError(response) {
            $.alert("条件送信に失敗しました。");
            sendBtn.button('reset');
        }

        addConditionNotification({
            toAccountId: toAccount,
            condition: condition,
            conditionType: conditionType,
        }, onSuccess, onError)
    }

    function loadConditionNotification(){
        $('#'+destinationNotificationId).off('click');
        function onSuccess(response) {
            if(response && response.status) {
                var destinationNotification = response.destinationNotification;
                destinationNotificationList = response.destinationNotificationList;
                updateNotification(destinationNotification);

                $('#'+destinationNotificationId).click(function () {
                    showNotificationModal("比較メール先抽出条件", destinationNotificationList);
                });
            }
        }
        function onError() {
            alert('マッチング条件ロードに失敗しました。');
        }

        getEngineerMatchingConditionNotification(onSuccess, onError);
    }

    function updateNotification(notificationNumber) {
        var notification = $('#' + destinationNotificationNewId);
        if(notificationNumber>0){
            notificationNumber = notificationNumber>99? 99 : notificationNumber;
            notification.text(notificationNumber);
            notification.removeClass('hidden');
        }else{
            notification.addClass('hidden');
        }
    }

    function initDuplicateHandle() {
        var duplicateSettingData = getCachedDuplicationSettingData();
        $('#enable-duplicate-handle').prop('checked', duplicateSettingData.enable);
        duplicateSettingData.enable ? $('.duplicate-control.duplicate-control-option').show() : $('.duplicate-control.duplicate-control-option').hide();
        $('#duplicate-sender').prop('checked', duplicateSettingData.sender);
        $('#duplicate-subject').prop('checked', duplicateSettingData.subject);
        $('#enable-duplicate-handle').change(function() {
            var enable = $(this).is(":checked");
            enable ? $('.duplicate-control.duplicate-control-option').show() : $('.duplicate-control.duplicate-control-option').hide();
            localStorage.setItem("enableDuplicateHandle-email-matching-engineer", enable);
        });

        $('#duplicate-sender').change(function() {
            var senderEnable = $(this).is(":checked");
            localStorage.setItem("handleDuplicateSender-email-matching-engineer", senderEnable);
        });

        $('#duplicate-subject').change(function() {
            var subjectEnable = $(this).is(":checked");
            localStorage.setItem("handleDuplicateSubject-email-matching-engineer", subjectEnable);
        });
    }

    function initDomainHandle() {
        var domainSettingData = getCachedDomainSettingData();
        $('#domain-partner-current').prop('checked', domainSettingData.handleDomainPartnerCurrent);
        domainSettingData.handleDomainPartnerCurrent ? $('.domain-control.domain-control-option').show() : $('.domain-control.domain-control-option').hide();
        $('#domain-partner-group').prop('checked', domainSettingData.handleDomainPartnerGroup);

        $('#domain-partner-current').change(function() {
            var enable = $(this).is(":checked");
            enable ? $('.domain-control.domain-control-option').show() : $('.domain-control.domain-control-option').hide();
            localStorage.setItem("handleDomainPartnerCurrent-email-matching-engineer", enable);
        });

        $('#domain-partner-group').change(function() {
            var enable = $(this).is(":checked");
            localStorage.setItem("handleDomainPartnerGroup-email-matching-engineer", enable);
        });
    }

    function setButtonClickListenerByName(name, callback) {
        $("button[name='"+name+"']").off('click');
        $("button[name='"+name+"']").click(function () {
            if(typeof callback == "function"){
                callback.apply(this);
            }
        })
    }

    function setupSelectBoxes() {
        $(selectAllCheckBoxId).click(function () {
            $('input[name="caseSelect"]').prop('checked', this.checked);
        });

        $('input[name=caseSelect]').click(function(){
            if($("input[name=caseSelect]").length == $("input[name=caseSelect]:checked").length) {
                $(selectAllCheckBoxId).prop("checked", true);
            } else {
                $(selectAllCheckBoxId).prop("checked", false);
            }
        });
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

    function setFormData(id, form) {
        for (var i = 0; i < formFields.length; i++) {
            var field = formFields[i];
            if(field.name=="id"){
                $("input[name='id']").val(id);
            }else if(field.type == "checkbox"){
                $("input" + "[name='" + field.name + "']").prop('checked', form[field.name]);
            } else if (field.type == "radio") {
                $("input[name=" + field.name + "][value=" + form[field.name] + "]").prop('checked', true);
            } else {
                $("" + field.type + "[name='" + field.name + "']").val(form[field.name]);
            }
        }
    }

    function setHourlyMoneyBuilder(data){
        if(ruleNumberName != null && ruleNumberName != ""){
            if(data.moneyCondition == null){
                var money = data.monetaryMoney;
                if(money==null || money.trim()==""){
                    var rules = {
                        "condition":"AND",
                        "rules":[],
                        "valid":true};
                    $(hourlyMoneyBuilderId).queryBuilder('setRules', rules);
                }else{
                    var rules = {
                        "condition":"AND",
                        "rules":[{
                            "id":"4",
                            "field":"1",
                            "type":"string",
                            "input":"text",
                            "operator":"greater_or_equal",
                            "value":money
                        }],
                        "valid":true};
                    $(hourlyMoneyBuilderId).queryBuilder('setRules', rules);
                }
            }else{
                $(hourlyMoneyBuilderId).queryBuilder('setRules', data.moneyCondition);
            }
        }
    }

    function setMoneyCondition(listData){
        for(var i=0;i<listData.length;i++){
            if(listData[i].moneyCondition == null){
                var money = listData[i].monetaryMoney;
                if(money!=null && money.trim()!=""){
                    var rules = {
                        "condition":"AND",
                        "rules":[{
                            "id":"4",
                            "field":"1",
                            "type":"string",
                            "input":"text",
                            "operator":"greater_or_equal",
                            "value":money
                        }],
                        "valid":true};
                    listData[i].moneyCondition = rules;
                }
            }
        }
    }

    function getListEngineerMatching(){
        var listEng = [];
        $("input[name=caseSelect]:checked").each(function () {
            var engineerId = $(this).attr("value");
            for(var i=0;i<engineers.length;i++){
                if(engineerId == engineers[i].id){
                    listEng.push(engineers[i]);
                }
            }
        });
        return listEng;
    }

    function submit() {
        var destinationConditionData = buildDataFromBuilder(destinationBuilderId);
        if(!destinationConditionData) return;
        var spaceEffective = false;
        var distinguish = false;
        var listEngineerCondition = getListEngineerMatching();
        var duplicateSettingData = getCachedDuplicationSettingData();
        var domainSettingData = getCachedDomainSettingData();
        var form = {
            "destinationConditionData" : destinationConditionData,
            "listEngineerMatchingDTO": listEngineerCondition,
            "distinguish": distinguish,
            "spaceEffective": spaceEffective,
            "handleDuplicateSender": duplicateSettingData.handleDuplicateSender,
            "handleDuplicateSubject": duplicateSettingData.handleDuplicateSubject,
            "handleSameDomain": getCachedSameDomainSettingData(),
            "handleDomainPartnerCurrent": domainSettingData.handleDomainPartnerCurrent,
            "handleDomainPartnerGroup": domainSettingData.handleDomainPartnerGroup,
        };
        sessionStorage.setItem("distinguish-email-matching-engineer", distinguish);
        sessionStorage.setItem("spaceEffective-email-matching-engineer", spaceEffective);
        sessionStorage.setItem(matchingConditionEmailMatchingEngineerKey, JSON.stringify(form));
        saveDefaultSettings();
        var win = window.open('/user/emailMatchingEngineerResult', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('このサイトのポップアップを許可してください。');
        }
    }

    function getCachedSameDomainSettingData() {
        var enableSameDomainHandleData = localStorage.getItem("enableSameDomainHandle-email-matching-engineer");
        var enableSameDomainHandle = typeof enableSameDomainHandleData !== "string" ? false : !!JSON.parse(enableSameDomainHandleData);
        return enableSameDomainHandle;
    }

    function updateListEngineer(engineerCondition){
        if(engineerCondition==null) return;
        for(var i=0;i<engineers.length;i++){
            if(engineers[i]!=null){
                if(engineerCondition.id == engineers[i].id){
                    engineers[i].matchingWord = engineerCondition.matchingWord;
                    engineers[i].notGoodWord = engineerCondition.notGoodWord;
                    if(ruleNumberName != null && ruleNumberName != ""){
                        var monneyCondition = $(hourlyMoneyBuilderId).queryBuilder('getRules');
                        engineers[i].moneyCondition = monneyCondition;
                    }
                }
            }
        }
    }

    function applyConditionOnClick() {
        clearFormValidate();
        var validated = engineerFormValidate();
        if(!validated) return;
        if(ruleNumberName != null && ruleNumberName != ""){
            var validatedRule = $(hourlyMoneyBuilderId).queryBuilder('getRules');
            if(!validatedRule) return;
        }
        engineerCondition = getFormData();
        updateListEngineer(engineerCondition);
        clearEngineerOnClick();
        selectNextRow(engineers);
    }

    function engineerFormValidate() {
        var validate1 = engineerNameValidate();
        return validate1 ;
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


    function clearEngineerOnClick() {
        resetForm();
        clearFormValidate();
        $(extendMonthInputId).attr('readonly', true);
        resetEngineeTable();
    }

    function resetEngineeTable() {
        $("#" + engineerTableId).find('tr.highlight-selected').removeClass('highlight-selected');
    }

    function resetForm() {
        $(formId).trigger("reset");
        if(ruleNumberName != null && ruleNumberName != ""){
            $(hourlyMoneyBuilderId).queryBuilder('setRules', default_hourlyMoney_rules);
        }
    }

    function clearFormValidate() {
        $(formId).find(".has-error").removeClass('has-error');
    }

    function loadEngineers(callback) {
        var form = getFilterForm();
        function onSuccess(response) {
            if(response && response.status){
                loadEngineersData(engineerTableId, response.list);
                setupSelectBoxes();
                setMoneyCondition(response.list);
            }

            if(typeof callback == 'function'){
                callback(response.list);
            }
        }

        function onError(error) {
        }

        getEngineersToMatching(form, onSuccess, onError);
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
            setRowClickListener("engineerRow", function () {
                var $this = $(this);
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                selectedSourceTableRow = parseInt(index) + 1;
                var rowData = engineers[index];
                if (rowData && rowData.id) {
                    selectedRow($('#' + engineerTableId).find(' tbody tr:eq('+selectedSourceTableRow+')'));
                    setEngineer(rowData.id, rowData);
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
                }else if (cellNode.nodeName == "INPUT") {
                    var cellData = data[cellKey];
                    cellNode.value = cellData;
                    cellNode.name = "caseSelect";
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

    function setEngineer(id, data) {
        clearFormValidate();
        setFormData(id, data);
        setHourlyMoneyBuilder(data);
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
        if ($(checkboxNextSelectId).is(":checked")){
            selectedSourceTableRow = selectedSourceTableRow+1;
            selectNext(selectedSourceTableRow);
        }else{
            clearEngineerOnClick();
        }
    }

    function selectNext(index) {
        if(index>engineers.length) {
            $.alert("最終行まで更新しました");
            clearEngineerOnClick();
        } else {
            var row = $('#' + engineerTableId).find(' tbody tr:eq('+index+')');
            selectedRow(row);
            var rowData = engineers[index-1];
            setEngineer(rowData.id, rowData);
        }
    }

    function selectedRow(row) {
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
    }

    function loadDefaultSettings() {
        loadExpandCollapseSetting(destinationBuilderId);
        if(ruleNumberName != null && ruleNumberName != ""){
            loadExpandCollapseSetting(hourlyMoneyBuilderId);
        }

        var destinationConditionsStr = localStorage.getItem(destinationConditionKey);
        var destinationConditions = destinationConditionsStr == null || JSON.parse(destinationConditionsStr) == null ? default_condition_rules : JSON.parse(destinationConditionsStr);
        replaceCondition(destinationConditions);
        $(destinationBuilderId).queryBuilder('setRules', destinationConditions);
        if(ruleNumberName != null && ruleNumberName != ""){
            $(hourlyMoneyBuilderId).queryBuilder('setRules', default_hourlyMoney_rules);
        }

        var destinationConditionName = localStorage.getItem(destinationConditionNameKey) || "未登録の条件";
        setInputValue(destinationConditionNameId, destinationConditionName);
    }

    function saveDefaultSettings() {

        var destinationConditions = $(destinationBuilderId).queryBuilder('getRules');
        localStorage.setItem(destinationConditionKey, JSON.stringify(destinationConditions));
        var spaceEffective = false;
        var distinguish = false;
        localStorage.setItem("spaceEffective-email-matching-engineer", spaceEffective);
        localStorage.setItem("distinguish-email-matching-engineer", distinguish);
        localStorage.setItem(getCollapseKey(destinationBuilderId), $(destinationBuilderId).is(":hidden"));
        var destinationConditionName = getInputValue(destinationConditionNameId);
        localStorage.setItem(destinationConditionNameKey, destinationConditionName);
    }

    function loadExpandCollapseSetting(builderId) {
        var isHidden = localStorage.getItem(getCollapseKey(builderId)) === "true";
        var $builder = $(builderId);
        var $collapseView = $(builderId + collapseViewPostfix);
        var $button = $builder.parent().parent().find("button[name='builder-ec']");
        if(isHidden) {
            $button.html("＋");
            $builder.hide();
            $collapseView.show();
        } else {
            $button.html("ー");
            $collapseView.hide();
            $builder.show();
        }
    }

    function getCollapseKey(builderId) {
        return collapsedPrefixKey + "-" + builderId;
    }

    function onExpandCollapseBuilder() {
        var builderId = this.getAttribute("data");
        if(builderId) {
            var builder = $(builderId);
            $(this).html(builder.is(":visible") ? "＋" : "ー");
            if(builder.is(":visible")){
                $(builderId).slideToggle(200, function () {
                    $(builderId + collapseViewPostfix).slideToggle(200, function () {

                    })
                })
            } else {
                $(builderId + collapseViewPostfix).slideToggle(200, function () {
                    $(builderId).slideToggle(200, function () {

                    })
                });
            }
        }
    }
    function saveDestinationListData(){
        var result = $(destinationBuilderId).queryBuilder('getRules');
        var defaultName = $(destinationConditionNameId).val();
        if ($.isEmptyObject(result)) return;
        function onSuccess(response) {
            showNamePrompt(response.list, ENGINEER_CONDITIONTYPE, defaultName, function (name) {
                var data = {
                    conditionName: name,
                    condition: JSON.stringify(result),
                    conditionType: ENGINEER_CONDITIONTYPE
                }
                function onSuccess(response) {
                    if (response && response.status) {
                        $.alert("条件追加が成功しました")
                        $(destinationConditionNameId).val(name)
                    } else {
                        $.alert("条件追加が失敗しました")
                    }
                }
                function onError(response) {
                    $.alert("条件追加が失敗しました")
                }
                addConditionSaved(data, onSuccess, onError);
            })
        }
        function onError() {
        }
        getAllConditionSaved(ENGINEER_CONDITIONTYPE, onSuccess, onError)
    }


    function getDestinationListData(skip) {
        function onSuccess(response) {
            if (response && response.status) {
                showNamePrompt(response.list, ENGINEER_CONDITIONTYPE, "",function (name) {
                    if (name != null && name.length > 0) {
                        getListData(name, response.list, ENGINEER_CONDITIONTYPE,destinationBuilderId);
                    }
                    else {
                    }
                })
            }
        }
        function onError() {
            console.error("条件データロードが失敗しました")
        }
        getAllConditionSaved(ENGINEER_CONDITIONTYPE, onSuccess, onError)
    }

    function removeDataListItem(name, datalist) {
        var dataId;
        var removeCondition;
        for (var i = 0; i < datalist.length; i++) {
            if (name == datalist[i].conditionName) {
                dataId = datalist[i].id;
                removeCondition = datalist[i]
            }
            var conditionPosition = datalist.indexOf(removeCondition)
            if(conditionPosition != -1) {
                datalist.splice(conditionPosition, 1);
            }
        }
        function onSuccess() {
            $.alert("条件消除が成功しました");
            $("#dataModalName").val("");
            updateKeyList(datalist);
        }
        function onError() {
            console.error("条件消除が失敗しました")
        }
        deleteConditionSaved(dataId,onSuccess,onError);
    }
    function showNamePrompt(datalist, conditionType, defaultName, callback) {
        $('#dataModal').modal();
        $( '#dataModalName').val(defaultName);
        if(defaultName == ""){
            $('#dataModalName').attr('placeholder','')
        }
        $("input#dataModalName").css("border-color", "lightgray")
        $("#warning").addClass("warning")
        updateKeyList(datalist);
        $("#dataModalName").off("change paste keyup");
        $("#dataModalName").on("change paste keyup", disableRemoveDatalistItem);
        setInputAutoComplete("dataModalName");
        $(removeDatalistItemBtnId).off('click');
        $(removeDatalistItemBtnId).click(function (result) {
            var name = $("#dataModalName").val();
            removeDataListItem(name, datalist)
        });
        $('#dataModalName').off('input');
        $("#dataModalName").on('input', function () {
            var x = $("#dataModalName").val().length;
            if (x > 0) {
                $("input#dataModalName").css("border-color", "lightgray")
                $("#warning").addClass("warning")
            }
        })
        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {
            var name = $( '#dataModalName').val();
            if (name.length == 0) {
                $("input#dataModalName").css("border-color", "red")
                $("#warning").removeClass("warning")
                return;
            } else {
                $("input#dataModalName").css("border-color", "lightgray")
            }
            $('#dataModal').modal('hide');
            if(typeof callback === "function"){
                callback(name);
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

    function updateKeyList(datalist) {
        datalist = datalist || [];
        $('#keylist').html('');
        for(var i = 0; i < datalist.length; i++){
            $('#keylist').append("<option value='" + datalist[i].conditionName + "'>");
        }
    }

    function disableRemoveDatalistItem() {
        var name = $( '#dataModalName').val();
        if(!name || name.trim().length === 0){
            disableButton(removeDatalistItemBtnId, true);
        } else {
            disableButton(removeDatalistItemBtnId, false);
        }
    }

    function setInputAutoComplete(className) {
        $( "." + className ).off('click');
        $( "." + className ).off('mouseleave');
        $( "." + className ).on('click', function() {
            $(this).attr('placeholder',$(this).val());
            $(this).val('');
            disableRemoveDatalistItem();
        });
        $( "." + className ).on('mouseleave', function() {
            if ($(this).val() == '') {
                $(this).val($(this).attr('placeholder'));
            }
        });
    }

    function getListData(name, datalist, conditionType, builderId) {
        var data = null;
        for(var i = 0; i < datalist.length; i++){
            if(name == datalist[i].conditionName){
                data = datalist[i].condition
            }
        }
        if(data == null){
            $.alert("条件追加が失敗しました");
        } else{
            $(destinationConditionNameId).val(name)
            data = JSON.parse(data);
            replaceCondition(data);
            $(builderId).queryBuilder('setRules', data);
        }
    }

    function extractDestination() {
        var destinationConditionData = buildDataFromBuilder(destinationBuilderId);
        if(!destinationConditionData) return;
        var duplicateSettingData = getCachedDuplicationSettingData();
        var data = {
            "conditionData" : destinationConditionData,
            "distinguish": false,
            "spaceEffective": false,
            "handleDuplicateSender": duplicateSettingData.handleDuplicateSender,
            "handleDuplicateSubject": duplicateSettingData.handleDuplicateSubject,
            "type": 2,
        };
        sessionStorage.setItem("extractDestinationData", JSON.stringify(data));
        saveDefaultSettings();
        var win = window.open('/user/extractDestination', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('このサイトのポップアップを許可してください。');
        }
    }

    function getCachedDuplicationSettingData() {
        var enableDuplicateHandleData = localStorage.getItem("enableDuplicateHandle-email-matching-engineer");
        var enableDuplicateHandle = typeof enableDuplicateHandleData !== "string" ? false : !!JSON.parse(enableDuplicateHandleData);
        var handleDuplicateSenderData = localStorage.getItem("handleDuplicateSender-email-matching-engineer");
        var handleDuplicateSender = typeof handleDuplicateSenderData !== "string" ? false : !!JSON.parse(handleDuplicateSenderData);
        var handleDuplicateSubjectData = localStorage.getItem("handleDuplicateSubject-email-matching-engineer");
        var handleDuplicateSubject = typeof handleDuplicateSubjectData !== "string" ? false : !!JSON.parse(handleDuplicateSubjectData);
        return {
            enable: enableDuplicateHandle,
            sender: handleDuplicateSender,
            handleDuplicateSender: enableDuplicateHandle && handleDuplicateSender,
            subject: handleDuplicateSubject,
            handleDuplicateSubject: enableDuplicateHandle && handleDuplicateSubject,
        }
    }

    function getCachedDomainSettingData() {
        var handleDomainPartnerCurrentData = localStorage.getItem("handleDomainPartnerCurrent-email-matching-engineer");
        var handleDomainPartnerCurrent = typeof handleDomainPartnerCurrentData !== "string" ? false : !!JSON.parse(handleDomainPartnerCurrentData);
        var handleDomainPartnerGroupData = localStorage.getItem("handleDomainPartnerGroup-email-matching-engineer");
        var handleDomainPartnerGroup = typeof handleDomainPartnerGroupData !== "string" ? false : !!JSON.parse(handleDomainPartnerGroupData);
        return {
            enable: handleDomainPartnerCurrent,
            handleDomainPartnerCurrent: handleDomainPartnerCurrent,
            handleDomainPartnerGroup: handleDomainPartnerCurrent && handleDomainPartnerGroup,
        }
    }

    function buildGroupDataFromRaw(data){
        var result = {
            condition: data.condition,
            rules: buildRulesDataFromRaw(data)
        }
        return result;
    }

    function buildRulesDataFromRaw(data) {
        var result = [];
        for(var i = 0; i < data.rules.length; i++){
            var rawRule = data.rules[i];
            if(rawRule.id){
                var rule = {
                    id: rawRule.id,
                    operator: rawRule.operator,
                    type: rawRule.type,
                    value: rawRule.value
                }
                result.push(rule);
            } else if (rawRule.condition) {
                var rule = buildGroupDataFromRaw(rawRule);
                result.push(rule);
            }
        }
        return result;
    }

    function buildDataFromBuilder(builderId) {
        var result = $(builderId).queryBuilder('getRules');
        if ($.isEmptyObject(result)) return;
        return buildGroupDataFromRaw(result);
    }

    function getInputIdFromUrl(url) {
        switch (url) {
            case destinationPrefixUrlKey:
                return destinationConditionNameId;
        }
    }

    function disableButton(buttonId, disabled) {
        if(buttonId && buttonId.length > 0){
            $(buttonId).prop("disabled", disabled);
        }
    }

    function setInputValue(inputId, value) {
        $(inputId).val(value);
    }

    function getInputValue(inputId) {
        return $(inputId).val();
    }

    function replaceCondition(rule) {
        var rules = rule.rules;
        if(rules){
            for(var i=rules.length-1;i>=0;i--){
                if(rules[i].id){
                    for(var j=0;j<ruleInvalidateIds.length;j++){
                        if(rules[i].id == ruleInvalidateIds[j]){
                            rules.splice(i, 1);
                            break;
                        }
                    }
                }else{
                    replaceCondition(rules[i]);
                }
            }
        }
    }

    function showNotificationModal(title, notificationList) {
        $('#notificationModal').modal();
        $( '#notificationModalTitle').text(title);
        updateNotificationList(notificationList);
        $('#notificationModalClose').off('click');
        $("#notificationModalClose").click(function () {
            $('#notificationModal').modal('hide');
        });
        $('.notification-modal-show-more').addClass('hidden');
        $('#modal-body-content').off('scroll');
        $('#modal-body-content').scroll(function() {
            if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight) {
                if($(this).scrollTop() > 0){
                    $('.notification-modal-show-more').removeClass('hidden');
                }
            }else{
                $('.notification-modal-show-more').addClass('hidden');
            }
        });
        $('.notification-modal-show-more').off('click');
        $('.notification-modal-show-more').click(function () {
            $('.notification-modal-show-more').text('');
            $('.notification-modal-show-more').addClass('fa fa-spinner fa-spin');
            function onSuccess(response) {
                if(response && response.status) {
                    var list = response.list;
                    for(var i=0;i<list.length;i++){
                        notificationList.push(list[i]);
                    }
                    updateNotificationList(notificationList);
                } else {
                    $.alert("条件通知表示に失敗しました。");
                }
                $('.notification-modal-show-more').text('もっと見せる');
                $('.notification-modal-show-more').addClass('hidden');
                $('.notification-modal-show-more').removeClass('fa fa-spinner fa-spin');
            }

            function onError(response) {
                conditionNotification.status = NOTIFICATION_NEW;
                $.alert("条件通知表示に失敗しました。");
                $('.notification-modal-show-more').text('もっと見せる');
                $('.notification-modal-show-more').removeClass('fa fa-spinner fa-spin');
            }

            if(notificationList.length>0){
                showMoreConditionNotification(notificationList[notificationList.length - 1], onSuccess, onError)
            }
        });
    }

    function updateNotificationList(notificationList) {
        $('#modal-body-content').html("");
        for(var i=0;i<notificationList.length;i++){
            switch (notificationList[i].status) {
                case NOTIFICATION_NEW:
                    $('#modal-body-content').append(
                        '<div class="btn-notification-div">' +
                        '<span>' + notificationList[i].fromAccount + ' があなたに設定条件のレコードを送信しました。</span>' +
                        '<button class="btn btn-danger pull-right btn-notification-reject"  style="margin-left: 10px;" data-index="'+ i +'">却下</button>' +
                        '<button class="btn btn-success pull-right btn-notification-accept" style="margin-left: 10px;" data-index="'+ i +'">同意</button>' +
                        '<button class="btn btn-primary pull-right btn-notification-preview" style="margin-left: 10px;" data-index="'+ i +'">プレビュー</button>' +
                        '<br/>' +
                        '<span style="font-size: 13px; color: grey">受信日付：' + notificationList[i].sentAt + '</span>' +
                        '</div>'
                    );
                    break;
                case NOTIFICATION_ACCEPT:
                    $('#modal-body-content').append(
                        '<div class="btn-notification-div">' +
                        '<span>' + notificationList[i].fromAccount + ' からのマッチング条件のレコードを受けました。</span>' +
                        '<br/>' +
                        '<span style="font-size: 13px; color: grey">受信日付：' + notificationList[i].sentAt + '</span>' +
                        '</div>'
                    );
                    break;
                case NOTIFICATION_REJECT:
                    $('#modal-body-content').append(
                        '<div class="btn-notification-div">' +
                        '<span>' + notificationList[i].fromAccount + ' からのマッチング条件のレコードを却下しました。</span>' +
                        '<br/>' +
                        '<span style="font-size: 13px; color: grey">受信日付：' + notificationList[i].sentAt + '</span>' +
                        '</div>'
                    );
                    break;
            }

        }
        $('.btn-notification-preview').click(function () {
            var index = $(this).attr('data-index');
            showPreviewModal(notificationList[index]);
        })

        $('.btn-notification-accept').click(function () {
            var index = $(this).attr('data-index');
            $.confirm({
                title: '<b>【条件通知】</b>',
                titleClass: 'text-center',
                content: '<div class="text-center" style="font-size: 16px;">通知を許可しますか。<br/></div>',
                buttons: {
                    confirm: {
                        text: 'はい',
                        action: function(){
                            if(notificationList[index] && notificationList[index] != null){
                                notificationList[index].status = NOTIFICATION_ACCEPT;
                                editConditionNotification(notificationList[index], function () {
                                    downNotification(notificationList[index].conditionType);
                                    $('#notificationModal').modal('hide');
                                    applyCondition(notificationList[index]);
                                });
                            }
                            console.log(notificationList[index]);
                        }
                    },
                    cancel: {
                        text: 'いいえ',
                        action: function(){}
                    },
                }
            });
        })

        $('.btn-notification-reject').click(function () {
            var index = $(this).attr('data-index');
            $.confirm({
                title: '<b>【条件通知】</b>',
                titleClass: 'text-center',
                content: '<div class="text-center" style="font-size: 16px;">通知を本当に拒否したいですか。<br/></div>',
                buttons: {
                    confirm: {
                        text: 'はい',
                        action: function(){
                            if(notificationList[index] && notificationList[index] != null){
                                notificationList[index].status = NOTIFICATION_REJECT;
                                editConditionNotification(notificationList[index], function () {
                                    downNotification(notificationList[index].conditionType);
                                    $('#notificationModal').modal('hide');
                                });
                            }
                            console.log(notificationList[index]);
                        }
                    },
                    cancel: {
                        text: 'いいえ',
                        action: function(){}
                    },
                }
            });
        })
    }

    function showPreviewModal(conditionNotification) {
        $('#preview-builder').queryBuilder(default_destination_configs);

        var condition = jQuery.parseJSON(conditionNotification.condition);
        replaceCondition(condition);
        $('#preview-builder').queryBuilder('setRules', condition);
        $('#previewConditionModal').modal();
        $( '#previewConditionModalTitle').text("条件プレビュー");
        $('#previewConditionModalClose').off('click');
        $("#previewConditionModalClose").click(function () {
            $('#previewConditionModal').modal('hide');
        });
    }

    function editConditionNotification(conditionNotification, callback) {
        $('.btn-notification-preview').button('loading');
        $('.btn-notification-accept').button('loading');
        $('.btn-notification-reject').button('loading');
        function onSuccess(response) {
            if(response && response.status) {
                if(typeof callback === "function"){
                    callback();
                }
            } else {
                conditionNotification.status = NOTIFICATION_NEW;
                $.alert("条件送信に失敗しました。");
            }
            $('.btn-notification-preview').button('reset');
            $('.btn-notification-accept').button('reset');
            $('.btn-notification-reject').button('reset');
        }

        function onError(response) {
            conditionNotification.status = NOTIFICATION_NEW;
            $.alert("条件送信に失敗しました。");
            $('.btn-notification-preview').button('reset');
            $('.btn-notification-accept').button('reset');
            $('.btn-notification-reject').button('reset');
        }

        updateConditionNotification(conditionNotification, onSuccess, onError)
    }

    function downNotification(conditionType){
        var number = $('#' +destinationNotificationNewId).text();
        updateNotification(number-1, conditionType);
    }

    function applyCondition(conditionNotification){
        var queryBuilder = $(destinationBuilderId);
        var condition = jQuery.parseJSON(conditionNotification.condition);
        replaceCondition(condition);
        queryBuilder.queryBuilder('setRules', condition);
    }

})(jQuery);