
(function () {
    "use strict";
    var saveConditionBtnId = '#saveConditionBtnId';
    var getConditionBtnId = '#getConditionBtnId';
    var extractConditionBtnId = '#extractConditionBtn';
    var submitFormBtnId = '#submitFormBtn';
    var matchingWordsAreaId = '#matchingWordsArea';

    var statisticConditionBuilderId = '#statistic-condition-builder';

    var STATISTIC_CONDITION_KEY = "statistic-condition";
    var STATISTIC_CONDITION_NAME_KEY = "statistic-condition-name";
    var STATISTIC_MATCHING_WORD_KEY = "statistic-matching-word";
    var STATISTIC_CONDITION_DATA_KEY = "statistic-condition-data";


    var removeDatalistItemBtnId = "#dataRemoveItem";

    var STATISTIC_CONDITION_LIST_KEY = "/user/statisticSettings/listCondition";
    var STATISTIC_PRE_FIX_URL_KEY = "/user/statisticSettings/statistic";

    var collapsedPrefixKey = "/user/statisticSettings/collapsed";
    var collapseViewPostfix = "-collapse-view";

    var statisticConditionNameId = "#statistic-condition-name";

    var conditionNotificationId = "condition-notification";
    var conditionNotificationNewId = "condition-notification-new";
    var conditionNotificationAccountId = "condition-notification-account";
    var conditionNotificationSentBtnId = "#condition-notification-sent";

    var STATISTIC_CONDITION = 4;

    var NOTIFICATION_NEW = 0;
    var NOTIFICATION_ACCEPT = 1;
    var NOTIFICATION_REJECT = 2;

    var conditionNotificationList = [];

    var default_condition_configs = {};

    var ruleInvalidateIds = [];

    var RULE_NUMBER_ID = 4;
    var RULE_NUMBER_UP_RATE_ID = 5;
    var RULE_NUMBER_DOWN_RATE_ID = 6;

    var STATISTIC_CONDITIONTYPE=5;

    var ruleNumberId = "ruleNumber";
    var ruleNumberUpRateId = "ruleNumberUpRate";
    var ruleNumberDownRateId = "ruleNumberDownRate";

    var ruleNumberDownRateName = "";
    var ruleNumberUpRateName = "";
    var ruleNumberName = "";


    var default_condition_rules = {
        condition: "AND",
        rules: [
            {
                id: "7",
                input: "ratio",
                type: "integer",
                value: 0
            },
            {
                id: "8",
                operator: "greater_or_equal",
                type:  "string",
                value: "-7"
            },
            {
                id: "2",
                operator: "not_contains",
                type:  "string",
                value: "Re:"
            },
            {
                id: "0",
                operator: "not_contains",
                type:  "string",
                value: "@world-link-system.com"
            }
        ]
    };

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
        }

        var default_filters = getDefaultFilter(ruleNumberDownRateName, ruleNumberUpRateName, ruleNumberName);

        default_condition_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: null,
            lang: globalConfig.default_lang,
        };


        $(statisticConditionBuilderId).queryBuilder(default_condition_configs);

        setButtonClickListenerByName("builder-ec", onExpandCollapseBuilder);
        setButtonClickListenter(saveConditionBtnId, saveStatisticConditionListData);
        setButtonClickListenter(getConditionBtnId, getStaContisticditionListData);
        setButtonClickListenter(extractConditionBtnId, extractStatisticCondition);
        setButtonClickListenter(submitFormBtnId, submit);
        setButtonClickListenter(conditionNotificationSentBtnId, sendStatisticConditions);
        loadDefaultSettings();
        loadConditionNotification();
        $(window).on('beforeunload', saveDefaultSettings);
        $(document).on("keydown", keydownHandler);
    });

    function loadConditionNotification(){
        $('#'+conditionNotificationId).off('click');
        function onSuccess(response) {
            if(response && response.status) {
                var conditionNotification = response.destinationNotification;
                conditionNotificationList = response.destinationNotificationList;

                updateNotification(conditionNotification);

                $('#'+conditionNotificationId).click(function () {
                    showNotificationModal("メール先抽出条件通知", conditionNotificationList);
                });
            }
        }
        function onError() {
            alert('マッチング条件ロードに失敗しました。');
        }

        getStatisticConditionNotification(onSuccess, onError);
    }

    function updateNotification(notificationNumber) {
        var notification = $('#' + conditionNotificationNewId);;
        if(notificationNumber>0){
            notificationNumber = notificationNumber>99? 99 : notificationNumber;
            notification.text(notificationNumber);
            notification.removeClass('hidden');
        }else{
            notification.addClass('hidden');
        }
    }

    function sendStatisticConditions() {
        var toAccount = $('#' + conditionNotificationAccountId).val();
        if(!toAccount || toAccount==null){
            $.alert("アカウントを最初に選択してください。");
            return;
        }
        var conditions = $(statisticConditionBuilderId).queryBuilder('getRules');
        if(!conditions) return;
        sendConditions(toAccount, conditions, STATISTIC_CONDITION);
    }


    function sendConditions(toAccount, conditions, conditionType) {
        var condition = JSON.stringify(conditions);
        var sendBtn = $(conditionNotificationSentBtnId);;
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

    function keydownHandler(e) {
        if((e.which || e.keyCode) == 114) {
            e.preventDefault();
            $(extractSourceBtnId).click();
        } else if((e.which || e.keyCode) == 115) {
            e.preventDefault();
            $(extractDestinationBtnId).click();
        } else if((e.which || e.keyCode) == 116) {
            e.preventDefault();
            $(submitFormBtnId).click();
        }
    }

    function saveDefaultSettings() {
        var conditions = $(statisticConditionBuilderId).queryBuilder('getRules');
        localStorage.setItem(STATISTIC_CONDITION_KEY, JSON.stringify(conditions));
        var matchingWords = $(matchingWordsAreaId).val();
        matchingWords = matchingWords.toLocaleLowerCase();
        matchingWords = matchingWords.trim();
        localStorage.setItem(STATISTIC_MATCHING_WORD_KEY, matchingWords);
        localStorage.setItem(getCollapseKey(statisticConditionBuilderId), $(statisticConditionBuilderId).is(":hidden"));
        var conditionName = getInputValue(statisticConditionNameId);
        localStorage.setItem(STATISTIC_CONDITION_NAME_KEY, conditionName);
    }

    function getCollapseKey(builderId) {
        return collapsedPrefixKey + "-" + builderId;
    }

    function loadDefaultSettings() {
        loadExpandCollapseSetting(statisticConditionBuilderId);
        var conditionsStr = localStorage.getItem(STATISTIC_CONDITION_KEY);
        var conditions = conditionsStr == null || JSON.parse(conditionsStr) == null ? default_condition_rules : JSON.parse(conditionsStr);
        replaceCondition(conditions);
        $(statisticConditionBuilderId).queryBuilder('setRules', conditions);
        var conditionName = localStorage.getItem(STATISTIC_CONDITION_NAME_KEY) || "未登録の条件";
        setInputValue(statisticConditionNameId, conditionName);
        var matchingWords = localStorage.getItem(STATISTIC_MATCHING_WORD_KEY);
        $(matchingWordsAreaId).val(matchingWords);
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

    function setButtonClickListenter(id, callback) {
        $(id).off('click');
        $(id).click(function () {
            if(typeof callback === "function"){
                callback();
            }
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

    function showNamePrompt(datalist, conditionType, defaultName, callback) {
        $('#dataModal').modal();
        $( '#dataModalName').val(defaultName);
        if(defaultName == ""){
            $('#dataModalName').attr('placeholder','')
        }
        console.log($('#dataModalName'))
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

    function disableButton(buttonId, disabled) {
        if(buttonId && buttonId.length > 0){
            $(buttonId).prop("disabled", disabled);
        }
    }
    function saveStatisticConditionListData(){
        var result = $(statisticConditionBuilderId).queryBuilder('getRules');
        if ($.isEmptyObject(result)) return;
        var defaultPromptName = getInputValue(statisticConditionNameId);
        function onSuccess(response) {
            if (response && response.status) {
                showNamePrompt(response.list, STATISTIC_CONDITIONTYPE, defaultPromptName,function (name)  {
                    var data = {
                        conditionName: name,
                        condition: JSON.stringify(result),
                        conditionType: STATISTIC_CONDITIONTYPE
                    }
                    function onSuccess(response) {
                        if (response && response.status) {
                            $.alert("条件追加が成功しました")
                            $(statisticConditionNameId).val(name)
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
        }
        function onError() {
            console.error("load condition data fail")
        }
        getAllConditionSaved(STATISTIC_CONDITIONTYPE, onSuccess, onError)
    }

    function saveListData(url, name,  data) {
        var key = url + "@" + name;
        var inputId = getInputIdFromUrl(url);
        setInputValue(inputId, name);
        console.log(key);
        console.log(data);
        localStorage.setItem(key, JSON.stringify(data));
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

    function getStaContisticditionListData() {
        function onSuccess(response) {
            if (response && response.status) {
                showNamePrompt(response.list, STATISTIC_CONDITIONTYPE, "", function (name) {
                    if (name != null && name.length > 0) {
                        getListData(name, response.list, STATISTIC_CONDITIONTYPE, statisticConditionBuilderId);
                        $("input#dataModalName").css("border-color", "lightgray");
                    } else {
                        $("input#dataModalName").css("border-color", "red")
                    }
                })
            }
        }
        function onError() {
            console.error("条件データロードが失敗しました")
        }
        getAllConditionSaved(STATISTIC_CONDITIONTYPE, onSuccess, onError)
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
        } else {
            $(statisticConditionNameId).val(name)
            data = JSON.parse(data);
            replaceCondition(data);
            $(builderId).queryBuilder('setRules', data);
        }
    }

    function extractStatisticCondition() {
        var destinationConditionData = buildDataFromBuilder(statisticConditionBuilderId);
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
        sessionStorage.setItem("extractEmailStatisticData", JSON.stringify(data));
        saveDefaultSettings();
        var win = window.open('/user/extractEmailStatistic', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('このサイトのポップアップを許可してください。');
        }
    }

    function buildDataFromBuilder(builderId) {
        var result = $(builderId).queryBuilder('getRules');
        if ($.isEmptyObject(result)) return;
        return buildGroupDataFromRaw(result);
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


    function submit() {
        var statisticConditionData = buildDataFromBuilder(statisticConditionBuilderId);
        if(!statisticConditionData) return;;
        var matchingWords = $(matchingWordsAreaId).val();
        matchingWords = matchingWords.toLocaleLowerCase();
        matchingWords = matchingWords.trim();
        var form = {
            "statisticConditionData" : statisticConditionData,
            "matchingWords": matchingWords,
        };
        sessionStorage.setItem(STATISTIC_CONDITION_DATA_KEY, JSON.stringify(form));
        // window.location = '/user/matchingResult';
        saveDefaultSettings();
        var win = window.open('/user/emailStatisticResult', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('このサイトのポップアップを許可してください');
        }
    }

    function getCachedDuplicationSettingData() {
        var enableDuplicateHandleData = localStorage.getItem("enableDuplicateHandle");
        var enableDuplicateHandle = typeof enableDuplicateHandleData !== "string" ? false : !!JSON.parse(enableDuplicateHandleData);
        var handleDuplicateSenderData = localStorage.getItem("handleDuplicateSender");
        var handleDuplicateSender = typeof handleDuplicateSenderData !== "string" ? false : !!JSON.parse(handleDuplicateSenderData);
        var handleDuplicateSubjectData = localStorage.getItem("handleDuplicateSubject");
        var handleDuplicateSubject = typeof handleDuplicateSubjectData !== "string" ? false : !!JSON.parse(handleDuplicateSubjectData);
        return {
            enable: enableDuplicateHandle,
            sender: handleDuplicateSender,
            handleDuplicateSender: enableDuplicateHandle && handleDuplicateSender,
            subject: handleDuplicateSubject,
            handleDuplicateSubject: enableDuplicateHandle && handleDuplicateSubject,
        }
    }

    function getInputIdFromUrl(url) {
        return statisticConditionNameId;
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

        $('#preview-builder').queryBuilder(default_condition_configs);

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
        var number = $('#' +conditionNotificationNewId).text();;
        updateNotification(number-1, conditionType);
    }

    function applyCondition(conditionNotification){
        var queryBuilder = $(statisticConditionBuilderId);
        var condition = jQuery.parseJSON(conditionNotification.condition);
        replaceCondition(condition);
        queryBuilder.queryBuilder('setRules', condition);
    }

})(jQuery);
