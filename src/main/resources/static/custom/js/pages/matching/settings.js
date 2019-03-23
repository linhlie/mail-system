
(function () {
    "use strict";
    var formId = '#matchingConditionSettingsForm';
    var switchConditionsBtnId = '#switchConditions';
    var saveSourceBtnId = '#saveSourceBtn';
    var getSourceBtnId = '#getSourceBtn';
    var saveDestinationBtnId = '#saveDestinationBtn';
    var getDestinationBtnId = '#getDestinationBtn';
    var saveMatchingBtnId = '#saveMatchingBtn';
    var getMatchingBtnId = '#getMatchingBtn';
    var submitFormBtnId = '#submitFormBtn';
    var extractSourceBtnId = '#extractSourceBtn';
    var extractDestinationBtnId = '#extractDestinationBtn';
    var matchingWordsAreaId = '#matchingWordsArea';
    var checkDomainInPartnerGroupId = "#check-domain-in-group";

    var sourceBuilderId = '#source-builder';
    var destinationBuilderId = '#destination-builder';
    var matchingBuilderId = '#matching-builder';

    var removeDatalistItemBtnId = "#dataRemoveItem";

    var sourceListKey = "/user/matchingSettings/listSourceKey";
    var sourcePrefixUrlKey = "/user/matchingSettings/source";
    var destinationListKey = "/user/matchingSettings/listDestinationKey";
    var destinationPrefixUrlKey = "/user/matchingSettings/destination";
    var matchingListKey = "/user/matchingSettings/listMatchingKey";
    var matchingPrefixUrlKey = "/user/matchingSettings/matching";

    var collapsedPrefixKey = "/user/matchingSettings/collapsed";

    var collapseViewPostfix = "-collapse-view";

    var checkDomainInPartnerGroupKey = "/user/matchingSettings/checkDomainInPartnerGroup";

    var sourceConditionNameId = "#source-condition-name";
    var destinationConditionNameId = "#destination-condition-name";
    var matchingConditionNameId = "#matching-condition-name";

    var sourceNotificationId = "source-notification";
    var sourceNotificationNewId = "source-notification-new";
    var sourceNotificationAccountId = "source-notification-account";
    var sourceNotificationSentBtnId = "#source-notification-sent";

    var destinationNotificationId = "destination-notification";
    var destinationNotificationNewId = "destination-notification-new";
    var destinationNotificationAccountId = "destination-notification-account";
    var destinationNotificationSentBtnId = "#destination-notification-sent";

    var matchingNotificationId = "matching-notification";
    var matchingNotificationNewId = "matching-notification-new";
    var matchingNotificationAccountId = "matching-notification-account";
    var matchingNotificationSentBtnId = "#matching-notification-sent";

    var SOURCE_CONDITION = 0;
    var DESTINATION_CONDITION = 1;
    var MATCHING_CONDITION = 2;

    var NOTIFICATION_NEW = 0;
    var NOTIFICATION_ACCEPT = 1;
    var NOTIFICATION_REJECT = 2;

    var sourceNotificationList = [];
    var destinationNotificationList = [];
    var matchingNotificationList = [];

    var default_source_configs = {};
    var default_destination_configs = {};
    var default_matching_configs = {};

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

    var SOURCE_CONDITIONTYPE = 1;
    var DESTINATION_CONDITIONTYPE = 2;
    var MATCHING_CONDITIONTYPE = 3;

    var default_source_rules = {
        condition: "AND",
        rules: [
            {
                id: "7",
                input: "ratio",
                type: "integer",
                value: 1
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

    var default_destination_rules = {
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

    function fullWidthNumConvert(fullWidthNum){
        return fullWidthNum.replace(/[\uFF10-\uFF19]/g, function(m) {
            return String.fromCharCode(m.charCodeAt(0) - 0xfee0);
        });
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

    function matchingMumberValidator(value, rule) {
        if (!value || value.trim().length === 0) {
            return "Value can not be empty!";
        } else if (rule.operator.type !== 'in') {
            if(value === ruleNumberName || value === ruleNumberUpRateName || value === ruleNumberDownRateName){
                return true;
            }
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

        var default_filters_matching = [{
            id: '0',
            label: '送信者',
            type: 'string',
            input: function(rule, inputName) {
                return '<label>比較先項目または値:&nbsp;</label>' +
                    '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist" placeholder=""/>';
            },
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '1',
            label: '受信者',
            type: 'string',
            input: function(rule, inputName) {
                return '<label>比較先項目または値:&nbsp;</label>' +
                    '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist" placeholder=""/>';
            },
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '2',
            label: '件名',
            type: 'string',
            input: function(rule, inputName) {
                return '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist" placeholder=""/>';
            },
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '3',
            label: '本文',
            type: 'string',
            input: function(rule, inputName) {
                return '<label>比較先項目または値:&nbsp;</label>' +
                    '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist" placeholder=""/>';
            },
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }];

        ruleNumberDownRateName = $('#'+ruleNumberDownRateId).text();
        if(!ruleNumberDownRateName || ruleNumberDownRateName==null){
            ruleInvalidateIds.push(RULE_NUMBER_DOWN_RATE_ID);
        }else{
            default_filters.splice(10,0,{
                id: RULE_NUMBER_DOWN_RATE_ID,
                label: ruleNumberDownRateName,
                type: 'string',
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: numberValidator
                },
            })

            default_filters_matching.splice(4,0,{
                id: RULE_NUMBER_DOWN_RATE_ID,
                label: ruleNumberDownRateName,
                type: 'string',
                input: function(rule, inputName) {
                    return '<label>比較先項目または値:&nbsp;</label>' +
                        '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist2" placeholder=""/>';
                },
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: matchingMumberValidator
                },
            })
        }

        ruleNumberUpRateName = $('#'+ruleNumberUpRateId).text();
        if(!ruleNumberUpRateName || ruleNumberUpRateName==null){
            ruleInvalidateIds.push(RULE_NUMBER_UP_RATE_ID);
        }else{
            default_filters.splice(10,0,{
                id: RULE_NUMBER_UP_RATE_ID,
                label: ruleNumberUpRateName,
                type: 'string',
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: numberValidator
                },
            })

            default_filters_matching.splice(4,0,{
                id: RULE_NUMBER_UP_RATE_ID,
                label: ruleNumberUpRateName,
                type: 'string',
                input: function(rule, inputName) {
                    return '<label>比較先項目または値:&nbsp;</label>' +
                        '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist2" placeholder=""/>';
                },
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: matchingMumberValidator
                },
            })
        }

        ruleNumberName = $('#'+ruleNumberId).text();
        if(!ruleNumberName || ruleNumberName==null){
            ruleInvalidateIds.push(RULE_NUMBER_ID);
        }else{
            default_filters.splice(10,0,{
                id: RULE_NUMBER_ID,
                label: ruleNumberName,
                type: 'string',
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: numberValidator
                },
            })

            default_filters_matching.splice(4,0,{
                id: RULE_NUMBER_ID,
                label: ruleNumberName,
                type: 'string',
                input: function(rule, inputName) {
                    return '<label>比較先項目または値:&nbsp;</label>' +
                        '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist2" placeholder=""/>';
                },
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: matchingMumberValidator
                },
            })
        }

        default_source_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: null,
            lang: globalConfig.default_lang,
        };

        default_destination_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: null,
            lang: globalConfig.default_lang,
        };

        default_matching_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters_matching,
            rules: null,
            lang: globalConfig.default_lang,
        };

        $(sourceBuilderId).queryBuilder(default_source_configs);
        $(destinationBuilderId).queryBuilder(default_destination_configs);
        $(matchingBuilderId).queryBuilder(default_matching_configs);
        var group = $(matchingBuilderId)[0].queryBuilder.model.root;
        if(group){
            group.empty();
        }

        $(matchingBuilderId).on('afterUpdateRuleFilter.queryBuilder', function(e, group) {
            setInputAutoComplete("matchingValue");
        });

        setButtonClickListenerByName("builder-ec", onExpandCollapseBuilder);
        setButtonClickListenter(switchConditionsBtnId, switchConditions);
        setButtonClickListenter(saveSourceBtnId, saveSourceListData);
        setButtonClickListenter(getSourceBtnId, getSourceListData);
        setButtonClickListenter(saveDestinationBtnId, saveDestinationListData);
        setButtonClickListenter(getDestinationBtnId, getDestinationListData);
        setButtonClickListenter(saveMatchingBtnId, saveMatchingListData);
        setButtonClickListenter(getMatchingBtnId, getMatchingListData);
        setButtonClickListenter(submitFormBtnId, submit);
        setButtonClickListenter(extractSourceBtnId, extractSource);
        setButtonClickListenter(extractDestinationBtnId, extractDestination);
        setButtonClickListenter(sourceNotificationSentBtnId, sendSourceConditions);
        setButtonClickListenter(destinationNotificationSentBtnId, sendDestinationConditions);
        setButtonClickListenter(matchingNotificationSentBtnId, sendMatchingConditions);
        initDuplicateHandle();
        initSameDomainHandle();
        initcheckDomainInPartnerGroup();
        loadDefaultSettings();
        loadConditionNotification();
        $(window).on('beforeunload', saveDefaultSettings);
        $(document).on("keydown", keydownHandler);
    });

    function loadConditionNotification(){
        $('#'+sourceNotificationId).off('click');
        $('#'+destinationNotificationId).off('click');
        $('#'+matchingNotificationId).off('click');
        function onSuccess(response) {
            if(response && response.status) {
                var sourceNotification = response.sourceNotification;
                var destinationNotification = response.destinationNotification;
                var matchingNotification = response.matchingNotification;
                sourceNotificationList = response.sourceNotificationList;
                destinationNotificationList = response.destinationNotificationList;
                matchingNotificationList = response.matchingNotificationList;

                updateNotification(sourceNotification, SOURCE_CONDITION);
                updateNotification(destinationNotification, DESTINATION_CONDITION);
                updateNotification(matchingNotification, MATCHING_CONDITION);

                $('#'+sourceNotificationId).click(function () {
                    showNotificationModal("メール元抽条件通知", sourceNotificationList);
                });

                $('#'+destinationNotificationId).click(function () {
                    showNotificationModal("メール先抽出条件通知", destinationNotificationList);
                });

                $('#'+matchingNotificationId).click(function () {
                    showNotificationModal("マッチング条件通知", matchingNotificationList);
                });
            }
        }
        function onError() {
            alert('マッチング条件ロードに失敗しました。');
        }

        getMatchingConditionNotification(onSuccess, onError);
    }

    function updateNotification(notificationNumber, notificationType) {
        var notification = null;
        switch (notificationType) {
            case SOURCE_CONDITION:
                notification = $('#' + sourceNotificationNewId);
                break;
            case DESTINATION_CONDITION:
                notification = $('#' + destinationNotificationNewId);
                break;
            case MATCHING_CONDITION:
                notification = $('#' + matchingNotificationNewId);
                break;
        }
        if(notificationNumber>0){
            notificationNumber = notificationNumber>99? 99 : notificationNumber;
            notification.text(notificationNumber);
            notification.removeClass('hidden');
        }else{
            notification.addClass('hidden');
        }
    }

    function sendSourceConditions() {
        var toAccount = $('#' + sourceNotificationAccountId).val();
        if(!toAccount || toAccount==null){
            $.alert("アカウントを最初に選択してください。");
            return;
        }
        var sourceConditions = $(sourceBuilderId).queryBuilder('getRules');
        if(!sourceConditions) return;
        sendConditions(toAccount, sourceConditions, SOURCE_CONDITION);
    }

    function sendDestinationConditions() {
        var toAccount = $('#' + destinationNotificationAccountId).val();
        if(!toAccount || toAccount==null){
            $.alert("アカウントを最初に選択してください。");
            return;
        }
        var destinationConditions = $(destinationBuilderId).queryBuilder('getRules');
        if(!destinationConditions) return;
        sendConditions(toAccount, destinationConditions, DESTINATION_CONDITION);
    }

    function sendMatchingConditions() {
        var toAccount = $('#' + matchingNotificationAccountId).val();
        if(!toAccount || toAccount==null){
            $.alert("アカウントを最初に選択してください。");
            return;
        }
        var matchingConditions = $(matchingBuilderId).queryBuilder('getRules');
        if(!matchingConditions) return;
        sendConditions(toAccount, matchingConditions, MATCHING_CONDITION);
    }


    function sendConditions(toAccount, conditions, conditionType) {
        var condition = JSON.stringify(conditions);
        var sendBtn = null;
        switch (conditionType) {
            case SOURCE_CONDITION:
                sendBtn = $(sourceNotificationSentBtnId);
                break;
            case DESTINATION_CONDITION:
                sendBtn = $(destinationNotificationSentBtnId);
                break;
            case MATCHING_CONDITION:
                sendBtn = $(matchingNotificationSentBtnId);
                break;
        }
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

    function switchConditions() {
        var sourceConditions = $(sourceBuilderId).queryBuilder('getRules');
        var destinationConditions = $(destinationBuilderId).queryBuilder('getRules');
        if ($.isEmptyObject(sourceConditions)) return;
        if ($.isEmptyObject(destinationConditions)) return;
        replaceCondition(destinationConditions);
        $(sourceBuilderId).queryBuilder('setRules', destinationConditions);
        replaceCondition(sourceConditions);
        $(destinationBuilderId).queryBuilder('setRules', sourceConditions);
        var sourceConditionName = getInputValue(sourceConditionNameId);
        var destinationConditionName = getInputValue(destinationConditionNameId);
        setInputValue(sourceConditionNameId, destinationConditionName);
        setInputValue(destinationConditionNameId, sourceConditionName);
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
        var sourceConditions = $(sourceBuilderId).queryBuilder('getRules');
        localStorage.setItem("sourceConditions", JSON.stringify(sourceConditions));
        var destinationConditions = $(destinationBuilderId).queryBuilder('getRules');
        localStorage.setItem("destinationConditions", JSON.stringify(destinationConditions));
        var matchingConditions = $(matchingBuilderId).queryBuilder('getRules');
        localStorage.setItem("matchingConditions", JSON.stringify(matchingConditions));
        // var spaceEffective = $('input[name=spaceEffective]:checked', formId).val() === "true";
        // var distinguish = $('input[name=distinguish]:checked', formId).val() === "true";
        var spaceEffective = false;
        var distinguish = false;
        localStorage.setItem("spaceEffective", spaceEffective);
        localStorage.setItem("distinguish", distinguish);
        var matchingWords = $(matchingWordsAreaId).val();
        matchingWords = matchingWords.toLocaleLowerCase();
        matchingWords = matchingWords.trim();
        localStorage.setItem("matchingWords", matchingWords);
        localStorage.setItem(getCollapseKey(sourceBuilderId), $(sourceBuilderId).is(":hidden"));
        localStorage.setItem(getCollapseKey(destinationBuilderId), $(destinationBuilderId).is(":hidden"));
        localStorage.setItem(getCollapseKey(matchingBuilderId), $(matchingBuilderId).is(":hidden"));
        var sourceConditionName = getInputValue(sourceConditionNameId);
        var destinationConditionName = getInputValue(destinationConditionNameId);
        var matchingConditionName = getInputValue(matchingConditionNameId);
        localStorage.setItem("sourceConditionName", sourceConditionName);
        localStorage.setItem("destinationConditionName", destinationConditionName);
        localStorage.setItem("matchingConditionName", matchingConditionName);
    }

    function getCollapseKey(builderId) {
        return collapsedPrefixKey + "-" + builderId;
    }

    function loadDefaultSettings() {
        loadExpandCollapseSetting(sourceBuilderId);
        loadExpandCollapseSetting(destinationBuilderId);
        loadExpandCollapseSetting(matchingBuilderId);
        var sourceConditionsStr = localStorage.getItem("sourceConditions");
        var sourceConditions = sourceConditionsStr == null || JSON.parse(sourceConditionsStr) == null ? default_source_rules : JSON.parse(sourceConditionsStr);
        replaceCondition(sourceConditions);
        $(sourceBuilderId).queryBuilder('setRules', sourceConditions);
        var destinationConditionsStr = localStorage.getItem("destinationConditions");
        var destinationConditions = destinationConditionsStr == null || JSON.parse(destinationConditionsStr) == null ? default_destination_rules : JSON.parse(destinationConditionsStr);
        replaceCondition(destinationConditions);
        $(destinationBuilderId).queryBuilder('setRules', destinationConditions);
        var matchingConditionsStr = localStorage.getItem("matchingConditions");
        var matchingConditions = matchingConditionsStr == null || JSON.parse(matchingConditionsStr) == null ? {condition: "AND", rules: []} : JSON.parse(matchingConditionsStr);
        replaceCondition(matchingConditions);
        $(matchingBuilderId).queryBuilder('setRules', matchingConditions);
        // var spaceEffective = localStorage.getItem("spaceEffective");
        // spaceEffective = spaceEffective == "true" ? true : false;
        // $("#spaceEffective1").prop("checked", !spaceEffective);
        // $("#spaceEffective2").prop("checked", spaceEffective);
        // var distinguish = localStorage.getItem("distinguish");
        // distinguish = distinguish == "true" ? true : false;
        // $("#distinguish1").prop("checked", !distinguish);
        // $("#distinguish2").prop("checked", distinguish);
        var sourceConditionName = localStorage.getItem("sourceConditionName") || "未登録の条件";
        var destinationConditionName = localStorage.getItem("destinationConditionName") || "未登録の条件";
        var matchingConditionName = localStorage.getItem("matchingConditionName") || "未登録の条件";
        setInputValue(sourceConditionNameId, sourceConditionName);
        setInputValue(destinationConditionNameId, destinationConditionName);
        setInputValue(matchingConditionNameId, matchingConditionName);
        var matchingWords = localStorage.getItem("matchingWords");
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

    var SOURCE_CONDITION;
    var DESTINATION_CONDITION;
    var MATCHING_CONDITION;

    function saveSourceListData() {
        var result = $(sourceBuilderId).queryBuilder('getRules');
        if ($.isEmptyObject(result)) return;

        function onSuccess(response) {
            showNamePrompt(response.list, SOURCE_CONDITIONTYPE, "", function (name) {
                var data = {
                    conditionName: name,
                    condition: JSON.stringify(result),
                    conditionType: SOURCE_CONDITIONTYPE
                }

                function onSuccess(response) {
                    if (response && response.status) {

                    }
                }

                function onError(response) {
                }

                addConditionSaved(data, onSuccess, onError);

            })
        }

        function onError() {
        }

        getAllConditionSaved(SOURCE_CONDITIONTYPE, onSuccess, onError)
    }


    function showNamePrompt(datalist, conditionType, defaultName, callback) {
        $('#dataModal').modal();
        $( '#dataModalName').val(defaultName);
        $("input#dataModalName").css("border-color", "lightgray")
        $("#warning").remove()
        updateKeyList(datalist);
        $("#dataModalName").off("change paste keyup");
        $("#dataModalName").on("change paste keyup", disableRemoveDatalistItem);
        setInputAutoComplete("dataModalName");
        $(removeDatalistItemBtnId).off('click');
        $(removeDatalistItemBtnId).click(function () {
            var name = $( '#dataModalName').val();

            removeDatalistItem(name);
        });
        $('#dataModalOk').off('click');
        $("#dataModalName").on('input', function () {
            var x = $("#dataModalName").val().length;
            if (x > 0) {
                $("input#dataModalName").css("border-color", "lightgray")
                $("#warning").remove()
            }
        })
        $("#dataModalOk").click(function () {
            var name = $( '#dataModalName').val();
            if (name.length == 0) {
                $("input#dataModalName").css("border-color", "red")
                $(".col-sm-8").append("<div id='warning'></div>")
                $("#warning").html("<div style='color:red'>Required Condition</div>")
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

    function removeDatalistItem(listKey, prefixUrlKey, name){
        var datalistStr = localStorage.getItem(listKey);
        var datalist = JSON.parse(datalistStr);
        var index = datalist.indexOf(name);
        if (index > -1) {
            datalist.splice(index, 1);
        }
        localStorage.setItem(listKey, JSON.stringify(datalist));
        localStorage.removeItem(prefixUrlKey + "@" + name);
        $( '#dataModalName').val('');
        updateKeyList(datalist);
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

    function saveDestinationListData(){
        var result = $(destinationBuilderId).queryBuilder('getRules');
        if ($.isEmptyObject(result)) return;

        function onSuccess(response) {
            showNamePrompt(response.list, DESTINATION_CONDITIONTYPE, "", function (name) {
                var data = {
                    conditionName: name,
                    condition: JSON.stringify(result),
                    conditionType: DESTINATION_CONDITIONTYPE
                }

                function onSuccess(response) {
                    if (response && response.status) {

                    }
                }

                function onError(response) {
                }

                addConditionSaved(data, onSuccess, onError);

            })
        }

        function onError() {
        }

        getAllConditionSaved(DESTINATION_CONDITIONTYPE, onSuccess, onError)
    }

    function saveMatchingListData() {
        var result = $(matchingBuilderId).queryBuilder('getRules');
        if ($.isEmptyObject(result)) return;

        function onSuccess(response) {
            showNamePrompt(response.list, MATCHING_CONDITIONTYPE, "", function (name) {
                var data = {
                    conditionName: name,
                    condition: JSON.stringify(result),
                    conditionType: MATCHING_CONDITIONTYPE
                }

                function onSuccess(response) {
                    if (response && response.status) {

                    }
                }

                function onError(response) {
                }

                addConditionSaved(data, onSuccess, onError);

            })
        }

        function onError() {
        }

        getAllConditionSaved(MATCHING_CONDITIONTYPE, onSuccess, onError)
    }

    function saveListData(url, name, data) {
        var key = url + "@" + name;
        var inputId = getInputIdFromUrl(url);
        setInputValue(inputId, name);
        localStorage.setItem(key, JSON.stringify(data));
    }

    function setInputAutoComplete(className) {
        $("." + className).off('click');
        $("." + className).off('mouseleave');
        $("." + className).on('click', function () {
            $(this).attr('placeholder', $(this).val());
            $(this).val('');
            disableRemoveDatalistItem();
        });
        $("." + className).on('mouseleave', function () {
            if ($(this).val() == '') {
                $(this).val($(this).attr('placeholder'));
            }
        });
    }


    function getSourceListData() {

        function onSuccess(response) {
            showNamePrompt(response.list, SOURCE_CONDITIONTYPE, "", function (name) {
                if (name != null && name.length > 0) {
                    getListData(name, response, SOURCE_CONDITIONTYPE, sourceBuilderId);
                    $("input#dataModalName").css("border-color", "lightgray")

                } else {
                    $("input#dataModalName").css("border-color", "red")
                }
            })
        }

        function onError() {
            console.error("load condition data fail")
        }

        getAllConditionSaved(SOURCE_CONDITIONTYPE, onSuccess, onError)

    }


    function getDestinationListData(skip) {
        function onSuccess(response) {
            showNamePrompt(response.list, DESTINATION_CONDITIONTYPE, "", function (name) {
                if (name != null && name.length > 0) {
                    getListData(name, response, DESTINATION_CONDITIONTYPE, destinationBuilderId);
                    $("input#dataModalName").css("border-color", "lightgray")

                } else {
                    $("input#dataModalName").css("border-color", "red")
                }
            })
        }

        function onError() {
            console.error("load condition data fail")
        }

        getAllConditionSaved(DESTINATION_CONDITIONTYPE, onSuccess, onError)
    }

    function getMatchingListData(skip) {
        function onSuccess(response) {
            showNamePrompt(response.list, MATCHING_CONDITIONTYPE, "", function (name) {
                if (name != null && name.length > 0) {
                    getListData(name, response, MATCHING_CONDITIONTYPE, matchingBuilderId);
                    $("input#dataModalName").css("border-color", "lightgray")

                } else {
                    $("input#dataModalName").css("border-color", "red")
                }
            })
        }

        function onError() {
            console.error("load condition data fail")
        }

        getAllConditionSaved(MATCHING_CONDITIONTYPE, onSuccess, onError)
    }

    function getListData(name, response, conditionType, builderId) {
        var data = null;
        for (var i = 0; i < response.list.length; i++) {
            if (name == response.list[i].conditionName) {
                data = response.list[i].condition
            }
        }
        if (data == null) {
            $.alert("add condition fail");
        } else {
            if (name && name.length > 0) {
                function onSuccess(response) {
                    if (response && response.status) {
                        $.alert({
                            title: "",
                            content: "add condition success",
                        });
                        if (conditionType == 1){
                            $(sourceConditionNameId).val(name)
                        }
                        else if(conditionType == 2){
                            $(destinationConditionNameId).val(name)
                        }
                        else if (conditionType == 3){
                            $(matchingConditionNameId).val(name)
                        }
                    } else {
                        $.alert("add condition fail");
                    }
                }

                function onError(response) {
                    $.alert("add condition fail");
                }

                getAllConditionSaved(conditionType, onSuccess, onError);
                data = JSON.parse(data);

            }
        }
        if (data != null) {
            replaceCondition(data);
            $(builderId).queryBuilder('setRules', data);
        } else {
            alert("見つけませんでした。");
        }
    }

    function addDefaultReceiveDateRow(data) {
        var receivedDateCondition = null;
        if (data.rules) {
            for (var i = 0; i < data.rules.length; i++) {
                var condition = data.rules[i];
                if (condition.id == "8") {
                    receivedDateCondition = condition;
                }
            }
        }
        if (receivedDateCondition == null) {
            receivedDateCondition = {
                id: "8",
                operator: "greater_or_equal",
                type: "string",
                value: "-7"
            }
            data.rules = data.rules ? data.rules : [];
            data.rules.push(receivedDateCondition);
        }
        return data;
    }

    function buildDataFromBuilder(builderId) {
        var result = $(builderId).queryBuilder('getRules');
        if ($.isEmptyObject(result)) return;
        return buildGroupDataFromRaw(result);
    }

    function buildGroupDataFromRaw(data) {
        var result = {
            condition: data.condition,
            rules: buildRulesDataFromRaw(data)
        }
        return result;
    }

    function buildRulesDataFromRaw(data) {
        var result = [];
        for (var i = 0; i < data.rules.length; i++) {
            var rawRule = data.rules[i];
            if (rawRule.id) {
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

    function extractSource() {
        var sourceConditionData = buildDataFromBuilder(sourceBuilderId);
        if (!sourceConditionData) return;
        var duplicateSettingData = getCachedDuplicationSettingData();
        var data = {
            "conditionData": sourceConditionData,
            // "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
            // "spaceEffective": $('input[name=spaceEffective]:checked', formId).val() === "true",
            "distinguish": false,
            "spaceEffective": false,
            "handleDuplicateSender": duplicateSettingData.handleDuplicateSender,
            "handleDuplicateSubject": duplicateSettingData.handleDuplicateSubject,
            "type": 1,
        };
        sessionStorage.setItem("extractSourceData", JSON.stringify(data));
        saveDefaultSettings();
        var win = window.open('/user/extractSource', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('このサイトのポップアップを許可してください');
        }
    }

    function extractDestination() {
        var destinationConditionData = buildDataFromBuilder(destinationBuilderId);
        if (!destinationConditionData) return;
        var duplicateSettingData = getCachedDuplicationSettingData();
        var data = {
            "conditionData": destinationConditionData,
            // "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
            // "spaceEffective": $('input[name=spaceEffective]:checked', formId).val() === "true",
            "distinguish": false,
            "Please allow popups for this websitespaceEffective": false,
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
            alert('このサイトのポップアップを許可してください');
        }
    }

    function submit() {
        var sourceConditionData = buildDataFromBuilder(sourceBuilderId);
        if (!sourceConditionData) return;
        var destinationConditionData = buildDataFromBuilder(destinationBuilderId);
        if (!destinationConditionData) return;
        var matchingConditionData = buildDataFromBuilder(matchingBuilderId);
        if (!matchingConditionData) return;
        var matchingWords = $(matchingWordsAreaId).val();
        matchingWords = matchingWords.toLocaleLowerCase();
        matchingWords = matchingWords.trim();
        // var spaceEffective = $('input[name=spaceEffective]:checked', formId).val() === "true";
        // var distinguish = $('input[name=distinguish]:checked', formId).val() === "true";
        var spaceEffective = false;
        var distinguish = false;
        var duplicateSettingData = getCachedDuplicationSettingData();
        var form = {
            "sourceConditionData": sourceConditionData,
            "destinationConditionData": destinationConditionData,
            "matchingConditionData": matchingConditionData,
            "matchingWords": matchingWords,
            "distinguish": distinguish,
            "spaceEffective": spaceEffective,
            "handleDuplicateSender": duplicateSettingData.handleDuplicateSender,
            "handleDuplicateSubject": duplicateSettingData.handleDuplicateSubject,
            "handleSameDomain": getCachedSameDomainSettingData(),
            "checkDomainInPartnerGroup": getCachedCheckDomainInPartnerGroupSettingData(),
        };
        sessionStorage.setItem("distinguish", distinguish);
        sessionStorage.setItem("spaceEffective", spaceEffective);
        sessionStorage.setItem("matchingConditionData", JSON.stringify(form));
        // window.location = '/user/matchingResult';
        saveDefaultSettings();
        var win = window.open('/user/matchingResult', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('このサイトのポップアップを許可してください');
        }
    }


    function initDuplicateHandle() {
        var duplicateSettingData = getCachedDuplicationSettingData();
        $('#enable-duplicate-handle').prop('checked', duplicateSettingData.enable);
        duplicateSettingData.enable ? $('.duplicate-control.duplicate-control-option').show() : $('.duplicate-control.duplicate-control-option').hide();
        $('#duplicate-sender').prop('checked', duplicateSettingData.sender);
        $('#duplicate-subject').prop('checked', duplicateSettingData.subject);
        $('#enable-duplicate-handle').change(function () {
            var enable = $(this).is(":checked");
            enable ? $('.duplicate-control.duplicate-control-option').show() : $('.duplicate-control.duplicate-control-option').hide();
            localStorage.setItem("enableDuplicateHandle", enable);
        });

        $('#duplicate-sender').change(function () {
            var senderEnable = $(this).is(":checked");
            localStorage.setItem("handleDuplicateSender", senderEnable);
        });

        $('#duplicate-subject').change(function () {
            var subjectEnable = $(this).is(":checked");
            localStorage.setItem("handleDuplicateSubject", subjectEnable);
        });
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

    function initSameDomainHandle() {
        var enableSameDomainHandle = getCachedSameDomainSettingData();
        $('#enable-same-domain-handle').prop('checked', enableSameDomainHandle);
        $('#enable-same-domain-handle').change(function () {
            var enable = $(this).is(":checked");
            localStorage.setItem("enableSameDomainHandle", enable);
        });
    }

    function getCachedSameDomainSettingData() {
        var enableSameDomainHandleData = localStorage.getItem("enableSameDomainHandle");
        var enableSameDomainHandle = typeof enableSameDomainHandleData !== "string" ? false : !!JSON.parse(enableSameDomainHandleData);
        return enableSameDomainHandle;
    }

    function initcheckDomainInPartnerGroup() {
        var checkDomainInPartnerGroup = getCachedCheckDomainInPartnerGroupSettingData();
        $(checkDomainInPartnerGroupId).prop('checked', checkDomainInPartnerGroup);
        $(checkDomainInPartnerGroupId).change(function () {
            var enable = $(this).is(":checked");
            localStorage.setItem(checkDomainInPartnerGroupKey, enable);
        });
    }

    function getCachedCheckDomainInPartnerGroupSettingData() {
        var checkDomainInPartnerGroupData = localStorage.getItem(checkDomainInPartnerGroupKey);
        var enablecheckDomainInPartnerGroup = typeof checkDomainInPartnerGroupData !== "string" ? false : !!JSON.parse(checkDomainInPartnerGroupData);
        return enablecheckDomainInPartnerGroup;
    }

    function getInputIdFromUrl(url) {
        switch (url) {
            case sourcePrefixUrlKey:
                return sourceConditionNameId;
            case destinationPrefixUrlKey:
                return destinationConditionNameId;
            case matchingPrefixUrlKey:
                return matchingConditionNameId;
            default:
                return sourceConditionNameId;
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
        if (rules) {
            for (var i = rules.length - 1; i >= 0; i--) {
                if (rules[i].id) {
                    for (var j = 0; j < ruleInvalidateIds.length; j++) {
                        if (rules[i].id == ruleInvalidateIds[j]) {
                            rules.splice(i, 1);
                            break;
                        }
                    }
                } else {
                    replaceCondition(rules[i]);
                }
            }
        }
    }

    function showNotificationModal(title, notificationList) {
        $('#notificationModal').modal();
        $('#notificationModalTitle').text(title);
        updateNotificationList(notificationList);
        $('#notificationModalClose').off('click');
        $("#notificationModalClose").click(function () {
            $('#notificationModal').modal('hide');
        });
        $('.notification-modal-show-more').addClass('hidden');
        $('#modal-body-content').off('scroll');
        $('#modal-body-content').scroll(function () {
            if ($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight) {
                if ($(this).scrollTop() > 0) {
                    $('.notification-modal-show-more').removeClass('hidden');
                }
            } else {
                $('.notification-modal-show-more').addClass('hidden');
            }
        });
        $('.notification-modal-show-more').off('click');
        $('.notification-modal-show-more').click(function () {
            $('.notification-modal-show-more').text('');
            $('.notification-modal-show-more').addClass('fa fa-spinner fa-spin');

            function onSuccess(response) {
                if (response && response.status) {
                    var list = response.list;
                    for (var i = 0; i < list.length; i++) {
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

            if (notificationList.length > 0) {
                showMoreConditionNotification(notificationList[notificationList.length - 1], onSuccess, onError)
            }
        });
    }

    function updateNotificationList(notificationList) {
        $('#modal-body-content').html("");
        for (var i = 0; i < notificationList.length; i++) {
            switch (notificationList[i].status) {
                case NOTIFICATION_NEW:
                    $('#modal-body-content').append(
                        '<div style="border: grey solid 1px; padding: 10px; margin-bottom: 10px;">' +
                        '<span>' + notificationList[i].fromAccount + ' があなたに設定条件のレコードを送信しました。</span>' +
                        '<button class="btn btn-danger pull-right btn-notification-reject"  style="margin-right: 10px;" data-index="' + i + '">却下</button>' +
                        '<button class="btn btn-success pull-right btn-notification-accept" style="margin-right: 10px;" data-index="' + i + '">同意</button>' +
                        '<button class="btn btn-primary pull-right btn-notification-preview" style="margin-right: 10px;" data-index="' + i + '">プレビュー</button>' +
                        '<br/>' +
                        '<span style="font-size: 13px; color: grey">受信日付：' + notificationList[i].sentAt + '</span>' +
                        '</div>'
                    );
                    break;
                case NOTIFICATION_ACCEPT:
                    $('#modal-body-content').append(
                        '<div style="border: grey solid 1px; padding: 10px; margin-bottom: 10px;">' +
                        '<span>' + notificationList[i].fromAccount + ' からのマッチング条件のレコードを受けました。</span>' +
                        '<br/>' +
                        '<span style="font-size: 13px; color: grey">受信日付：' + notificationList[i].sentAt + '</span>' +
                        '</div>'
                    );
                    break;
                case NOTIFICATION_REJECT:
                    $('#modal-body-content').append(
                        '<div style="border: grey solid 1px; padding: 10px; margin-bottom: 10px;">' +
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
                        action: function () {
                            if (notificationList[index] && notificationList[index] != null) {
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
                        action: function () {
                        }
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
                        action: function () {
                            if (notificationList[index] && notificationList[index] != null) {
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
                        action: function () {
                        }
                    },
                }
            });
        })
    }

    function showPreviewModal(conditionNotification) {
        switch (conditionNotification.conditionType) {
            case SOURCE_CONDITION:
                $('#preview-builder').queryBuilder(default_source_configs);
                break;
            case DESTINATION_CONDITION:
                $('#preview-builder').queryBuilder(default_destination_configs);
                break;
            case MATCHING_CONDITION:
                $('#preview-builder').queryBuilder(default_matching_configs);
                break;
        }
        var condition = jQuery.parseJSON(conditionNotification.condition);
        replaceCondition(condition);
        $('#preview-builder').queryBuilder('setRules', condition);
        $('#previewConditionModal').modal();
        $('#previewConditionModalTitle').text("条件プレビュー");
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
            if (response && response.status) {
                if (typeof callback === "function") {
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

    function downNotification(conditionType) {
        var number = 0;
        switch (conditionType) {
            case SOURCE_CONDITION:
                number = $('#' + sourceNotificationNewId).text();
                break;
            case DESTINATION_CONDITION:
                number = $('#' + destinationNotificationNewId).text();
                break;
            case MATCHING_CONDITION:
                number = $('#' + matchingNotificationNewId).text();
                break;
        }
        updateNotification(number - 1, conditionType);
    }

    function applyCondition(conditionNotification) {
        var queryBuilder = null;
        switch (conditionNotification.conditionType) {
            case SOURCE_CONDITION:
                queryBuilder = $(sourceBuilderId);
                break;
            case DESTINATION_CONDITION:
                queryBuilder = $(destinationBuilderId);
                break;
            case MATCHING_CONDITION:
                queryBuilder = $(matchingBuilderId);
                break;
        }
        var condition = jQuery.parseJSON(conditionNotification.condition);
        replaceCondition(condition);
        queryBuilder.queryBuilder('setRules', condition);
    }

})(jQuery);
