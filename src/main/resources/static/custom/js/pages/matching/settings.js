
(function () {
    "use strict";
    var formId = '#matchingConditionSettingsForm';
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
            if(value === "数値" || value === "数値(上代)" || value === "数値(下代)") return true;
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
        }];

        var default_lang = {
            "__locale": "English (en)",
            "__author": "Damien \"Mistic\" Sorel, http://www.strangeplanet.fr",

            "add_rule": "ルールの追加",
            "add_group": "グループの追加",
            "delete_rule": "削除",
            "delete_group": "削除",

            "conditions": {
                "AND": "AND",
                "OR": "OR"
            },

            "operators": {
                "equal": "等しい",
                "not_equal": "異なる",
                "in": "いずれか",
                "not_in": "いずれでもない",
                "less": "未満",
                "less_or_equal": "以下",
                "greater": "超",
                "greater_or_equal": "以上",
                "between": "範囲",
                "not_between": "範囲外",
                "begins_with": "開始する",
                "not_begins_with": "開始ではない",
                "contains": "含む",
                "not_contains": "含まない",
                "ends_with": "終了する",
                "not_ends_with": "終了ではない",
                "is_empty": "空",
                "is_not_empty": "空でない",
                "is_null": "空",
                "is_not_null": "空でない"
            },

            "errors": {
                "no_filter": "No filter selected",
                "empty_group": "The group is empty",
                "radio_empty": "No value selected",
                "checkbox_empty": "No value selected",
                "select_empty": "No value selected",
                "string_empty": "Empty value",
                "string_exceed_min_length": "Must contain at least {0} characters",
                "string_exceed_max_length": "Must not contain more than {0} characters",
                "string_invalid_format": "Invalid format ({0})",
                "number_nan": "Not a number",
                "number_not_integer": "Not an integer",
                "number_not_double": "Not a real number",
                "number_exceed_min": "Must be greater than {0}",
                "number_exceed_max": "Must be lower than {0}",
                "number_wrong_step": "Must be a multiple of {0}",
                "number_between_invalid": "Invalid values, {0} is greater than {1}",
                "datetime_empty": "Empty value",
                "datetime_invalid": "Invalid date format ({0})",
                "datetime_exceed_min": "Must be after {0}",
                "datetime_exceed_max": "Must be before {0}",
                "datetime_between_invalid": "Invalid values, {0} is greater than {1}",
                "boolean_not_valid": "Not a boolean",
                "operator_not_multiple": "Operator \"{1}\" cannot accept multiple values"
            },
            "invert": "Invert",
            "NOT": "NOT"
        }

        var default_source_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: null,
            lang: default_lang,
        };

        var default_destination_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: null,
            lang: default_lang,
        };

        var default_matching_configs = {
            plugins: [
                'sortable',
                'filter-description',
                'unique-filter',
                'bt-tooltip-errors',
                'bt-selectpicker',
                'bt-checkbox',
                'invert',
            ],
            allow_empty: true,
            filters: [{
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
            }, {
                id: '4',
                label: '数値',
                type: 'string',
                input: function(rule, inputName) {
                    return '<label>比較先項目または値:&nbsp;</label>' +
                    '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist2" placeholder=""/>';
                },
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: matchingMumberValidator
                },
            }, {
                id: '5',
                label: '数値(上代)',
                type: 'string',
                input: function(rule, inputName) {
                    return '<label>比較先項目または値:&nbsp;</label>' +
                    '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist2" placeholder=""/>';
                },
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: matchingMumberValidator
                },
            }, {
                id: '6',
                label: '数値(下代)',
                type: 'string',
                input: function(rule, inputName) {
                    return '<label>比較先項目または値:&nbsp;</label>' +
                    '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist2" placeholder=""/>';
                },
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: matchingMumberValidator
                },
            }],
            rules: null,
            lang: default_lang,
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

        setButtonClickListenter(saveSourceBtnId, saveSourceListData);
        setButtonClickListenter(getSourceBtnId, getSourceListData);
        setButtonClickListenter(saveDestinationBtnId, saveDestinationListData);
        setButtonClickListenter(getDestinationBtnId, getDestinationListData);
        setButtonClickListenter(saveMatchingBtnId, saveMatchingListData);
        setButtonClickListenter(getMatchingBtnId, getMatchingListData);
        setButtonClickListenter(submitFormBtnId, submit);
        setButtonClickListenter(extractSourceBtnId, extractSource);
        setButtonClickListenter(extractDestinationBtnId, extractDestination);
        initDuplicateHandle();
        initSameDomainHandle();
        loadDefaultSettings();
        $(window).on('beforeunload', saveDefaultSettings);
    });
    
    function saveDefaultSettings() {
        var sourceConditions = $(sourceBuilderId).queryBuilder('getRules');
        localStorage.setItem("sourceConditions", JSON.stringify(sourceConditions));
        var destinationConditions = $(destinationBuilderId).queryBuilder('getRules');
        localStorage.setItem("destinationConditions", JSON.stringify(destinationConditions));
        var matchingConditions = $(matchingBuilderId).queryBuilder('getRules');
        localStorage.setItem("matchingConditions", JSON.stringify(matchingConditions));
        var spaceEffective = $('input[name=spaceEffective]:checked', formId).val() === "true";
        var distinguish = $('input[name=distinguish]:checked', formId).val() === "true";
        localStorage.setItem("spaceEffective", spaceEffective);
        localStorage.setItem("distinguish", distinguish);
        var matchingWords = $(matchingWordsAreaId).val();
        matchingWords = matchingWords.toLocaleLowerCase();
        matchingWords = matchingWords.trim();
        localStorage.setItem("matchingWords", matchingWords);
    }

    function loadDefaultSettings() {
        var sourceConditionsStr = localStorage.getItem("sourceConditions");
        var sourceConditions = sourceConditionsStr == null || JSON.parse(sourceConditionsStr) == null ? default_source_rules : JSON.parse(sourceConditionsStr);
        $(sourceBuilderId).queryBuilder('setRules', sourceConditions);
        var destinationConditionsStr = localStorage.getItem("destinationConditions");
        var destinationConditions = destinationConditionsStr == null || JSON.parse(destinationConditionsStr) == null ? default_destination_rules : JSON.parse(destinationConditionsStr);
        $(destinationBuilderId).queryBuilder('setRules', destinationConditions);
        console.log("sourceConditions: ", sourceConditions);
        console.log("destinationConditions: ", destinationConditions);
        var matchingConditionsStr = localStorage.getItem("matchingConditions");
        var matchingConditions = matchingConditionsStr == null || JSON.parse(matchingConditionsStr) == null ? {condition: "AND", rules: []} : JSON.parse(matchingConditionsStr);
        $(matchingBuilderId).queryBuilder('setRules', matchingConditions);
        var spaceEffective = localStorage.getItem("spaceEffective");
        spaceEffective = spaceEffective == "true" ? true : false;
        $("#spaceEffective1").prop("checked", !spaceEffective);
        $("#spaceEffective2").prop("checked", spaceEffective);
        var distinguish = localStorage.getItem("distinguish");
        distinguish = distinguish == "true" ? true : false;
        $("#distinguish1").prop("checked", !distinguish);
        $("#distinguish2").prop("checked", distinguish);
        var matchingWords = localStorage.getItem("matchingWords");
        $(matchingWordsAreaId).val(matchingWords);
    }

    function setButtonClickListenter(id, callback) {
        $(id).off('click');
        $(id).click(function () {
            if(typeof callback === "function"){
                callback();
            }
        });
    }

    function saveSourceListData(){
        var result = $(sourceBuilderId).queryBuilder('getRules');
        if ($.isEmptyObject(result)) return;
        var datalistStr = localStorage.getItem(sourceListKey);
        var datalist = JSON.parse(datalistStr);
        datalist = datalist || [];
        showNamePrompt(datalist, sourceListKey, sourcePrefixUrlKey, function (name) {
            if (name != null && name.length > 0) {
                if(datalist.indexOf(name) < 0){
                    datalist.push(name);
                }
                localStorage.setItem(sourceListKey, JSON.stringify(datalist));
                saveListData(
                    sourcePrefixUrlKey,
                    name,
                    result
                )
            }
        })
    }
    
    function showNamePrompt(datalist, listKey, prefixUrlKey, callback) {
        $('#dataModal').modal();
        $( '#dataModalName').val('');
        updateKeyList(datalist);
        $("#dataModalName").off("change paste keyup");
        $("#dataModalName").on("change paste keyup", disableRemoveDatalistItem);
        setInputAutoComplete("dataModalName");
        $(removeDatalistItemBtnId).off('click');
        $(removeDatalistItemBtnId).click(function () {
            var name = $( '#dataModalName').val();
            removeDatalistItem(listKey, prefixUrlKey, name);
        });
        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {
            var name = $( '#dataModalName').val();
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
            $('#keylist').append("<option value='" + datalist[i] + "'>");
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
        var datalistStr = localStorage.getItem(destinationListKey);
        var datalist = JSON.parse(datalistStr);
        datalist = datalist || [];
        showNamePrompt(datalist, destinationListKey, destinationPrefixUrlKey, function (name) {
            if (name != null && name.length > 0) {
                if(datalist.indexOf(name) < 0){
                    datalist.push(name);
                }
                localStorage.setItem(destinationListKey, JSON.stringify(datalist));
                saveListData(
                    destinationPrefixUrlKey,
                    name,
                    result
                )
            }
        })
    }

    function saveMatchingListData(){
        var result = $(matchingBuilderId).queryBuilder('getRules');
        console.log("saveMatchingListData: ", result);
        if ($.isEmptyObject(result)) return;
        var datalistStr = localStorage.getItem(matchingListKey);
        var datalist = JSON.parse(datalistStr);
        datalist = datalist || [];
        showNamePrompt(datalist, matchingListKey, matchingPrefixUrlKey, function (name) {
            if (name != null && name.length > 0) {
                if(datalist.indexOf(name) < 0){
                    datalist.push(name);
                }
                localStorage.setItem(matchingListKey, JSON.stringify(datalist));
                saveListData(
                    matchingPrefixUrlKey,
                    name,
                    result
                )
            }
        })
    }

    function saveListData(url, name,  data) {
        var key = url + "@" + name;
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
    
    function getSourceListData(skip) {
        if(skip){
            getListData(sourcePrefixUrlKey, null, sourceBuilderId);
        } else {
            var datalistStr = localStorage.getItem(sourceListKey);
            var datalist = JSON.parse(datalistStr);
            datalist = datalist || [];
            showNamePrompt(datalist, sourceListKey, sourcePrefixUrlKey, function (name) {
                if (name != null && name.length > 0) {
                    getListData(sourcePrefixUrlKey, name, sourceBuilderId);
                }
            })
        }
    }

    function getDestinationListData(skip) {
        if(skip){
            getListData(destinationPrefixUrlKey, null, destinationBuilderId);
        } else {
            var datalistStr = localStorage.getItem(destinationListKey);
            var datalist = JSON.parse(datalistStr);
            datalist = datalist || [];
            showNamePrompt(datalist, destinationListKey, destinationPrefixUrlKey, function (name) {
                if (name != null && name.length > 0) {
                    getListData(destinationPrefixUrlKey, name, destinationBuilderId);
                }
            })
        }
    }

    function getMatchingListData(skip) {
        if(skip){
            getListData(matchingPrefixUrlKey, null, matchingBuilderId);
        } else {
            var datalistStr = localStorage.getItem(matchingListKey);
            var datalist = JSON.parse(datalistStr);
            datalist = datalist || [];
            showNamePrompt(datalist, matchingListKey, matchingPrefixUrlKey, function (name) {
                if (name != null && name.length > 0) {
                    getListData(matchingPrefixUrlKey, name, matchingBuilderId, true);
                }
            })
        }
    }

    function getListData(url, name, builderId, skipAddDefaultRow) {
        var data = null;
        if(name && name.length > 0){
            var key = url + "@" + name;
            data = localStorage.getItem(key) != null ? JSON.parse(localStorage.getItem(key)) : null;
        }
        if(data != null){
            var enableAddDefaultRow = !skipAddDefaultRow;
            if(enableAddDefaultRow) {
                data = addDefaultReceiveDateRow(data);
            }
            $(builderId).queryBuilder('setRules', data);
        } else {
            alert("見つけませんでした。");
        }
    }

    function addDefaultReceiveDateRow(data) {
        var receivedDateCondition = null;
        if(data.rules) {
            for(var i = 0; i < data.rules.length; i ++){
                var condition = data.rules[i];
                if(condition.id == "8"){
                    receivedDateCondition = condition;
                }
            }
        }
        if(receivedDateCondition == null){
            receivedDateCondition = {
                id: "8",
                operator: "greater_or_equal",
                type:  "string",
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
    
    function extractSource() {
        var sourceConditionData = buildDataFromBuilder(sourceBuilderId);
        if(!sourceConditionData) return;
        console.log("extractSource: ",sourceConditionData);
        const duplicateSettingData = getCachedDuplicationSettingData();
        var data = {
            "conditionData" : sourceConditionData,
            "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
            "spaceEffective": $('input[name=spaceEffective]:checked', formId).val() === "true",
            "handleDuplicateSender": duplicateSettingData.handleDuplicateSender,
            "handleDuplicateSubject": duplicateSettingData.handleDuplicateSubject,
        };
        sessionStorage.setItem("extractSourceData", JSON.stringify(data));
        saveDefaultSettings();
        var win = window.open('/user/extractSource', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('Please allow popups for this website');
        }
    }
    
    function extractDestination() {
        var destinationConditionData = buildDataFromBuilder(destinationBuilderId);
        if(!destinationConditionData) return;
        const duplicateSettingData = getCachedDuplicationSettingData();
        var data = {
            "conditionData" : destinationConditionData,
            "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
            "spaceEffective": $('input[name=spaceEffective]:checked', formId).val() === "true",
            "handleDuplicateSender": duplicateSettingData.handleDuplicateSender,
            "handleDuplicateSubject": duplicateSettingData.handleDuplicateSubject,
        };
        sessionStorage.setItem("extractDestinationData", JSON.stringify(data));
        saveDefaultSettings();
        var win = window.open('/user/extractDestination', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('Please allow popups for this website');
        }
    }
    
    function submit() {
        var sourceConditionData = buildDataFromBuilder(sourceBuilderId);
        if(!sourceConditionData) return;
        var destinationConditionData = buildDataFromBuilder(destinationBuilderId);
        if(!destinationConditionData) return;
        var matchingConditionData = buildDataFromBuilder(matchingBuilderId);
        if(!matchingConditionData) return;
        var matchingWords = $(matchingWordsAreaId).val();
        matchingWords = matchingWords.toLocaleLowerCase();
        matchingWords = matchingWords.trim();
        var spaceEffective = $('input[name=spaceEffective]:checked', formId).val() === "true";
        var distinguish = $('input[name=distinguish]:checked', formId).val() === "true";
        const duplicateSettingData = getCachedDuplicationSettingData();
        var form = {
            "sourceConditionData" : sourceConditionData,
            "destinationConditionData" : destinationConditionData,
            "matchingConditionData" : matchingConditionData,
            "matchingWords": matchingWords,
            "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
            "spaceEffective": spaceEffective,
            "handleDuplicateSender": duplicateSettingData.handleDuplicateSender,
            "handleDuplicateSubject": duplicateSettingData.handleDuplicateSubject,
            "handleSameDomain": getCachedSameDomainSettingData(),
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
            alert('Please allow popups for this website');
        }
    }
    
    
    function initDuplicateHandle() {
        const duplicateSettingData = getCachedDuplicationSettingData();
        $('#enable-duplicate-handle').prop('checked', duplicateSettingData.enable);
        duplicateSettingData.enable ? $('.duplicate-control.duplicate-control-option').show() : $('.duplicate-control.duplicate-control-option').hide();
        $('#duplicate-sender').prop('checked', duplicateSettingData.sender);
        $('#duplicate-subject').prop('checked', duplicateSettingData.subject);
        $('#enable-duplicate-handle').change(function() {
            var enable = $(this).is(":checked");
            enable ? $('.duplicate-control.duplicate-control-option').show() : $('.duplicate-control.duplicate-control-option').hide();
            localStorage.setItem("enableDuplicateHandle", enable);
        });

        $('#duplicate-sender').change(function() {
            var senderEnable = $(this).is(":checked");
            localStorage.setItem("handleDuplicateSender", senderEnable);
        });

        $('#duplicate-subject').change(function() {
            var subjectEnable = $(this).is(":checked");
            localStorage.setItem("handleDuplicateSubject", subjectEnable);
        });
    }

    function getCachedDuplicationSettingData() {
        let enableDuplicateHandleData = localStorage.getItem("enableDuplicateHandle");
        let enableDuplicateHandle = typeof enableDuplicateHandleData !== "string" ? false : !!JSON.parse(enableDuplicateHandleData);
        let handleDuplicateSenderData = localStorage.getItem("handleDuplicateSender");
        let handleDuplicateSender = typeof handleDuplicateSenderData !== "string" ? false : !!JSON.parse(handleDuplicateSenderData);
        let handleDuplicateSubjectData = localStorage.getItem("handleDuplicateSubject");
        let handleDuplicateSubject = typeof handleDuplicateSubjectData !== "string" ? false : !!JSON.parse(handleDuplicateSubjectData);
        return {
            enable: enableDuplicateHandle,
            sender: handleDuplicateSender,
            handleDuplicateSender: enableDuplicateHandle && handleDuplicateSender,
            subject: handleDuplicateSubject,
            handleDuplicateSubject: enableDuplicateHandle && handleDuplicateSubject,
        }
    }
    
    function initSameDomainHandle() {
        let enableSameDomainHandle = getCachedSameDomainSettingData();
        $('#enable-same-domain-handle').prop('checked', enableSameDomainHandle);
        $('#enable-same-domain-handle').change(function() {
            var enable = $(this).is(":checked");
            localStorage.setItem("enableSameDomainHandle", enable);
        });
    }

    function getCachedSameDomainSettingData() {
        let enableSameDomainHandleData = localStorage.getItem("enableSameDomainHandle");
        let enableSameDomainHandle = typeof enableSameDomainHandleData !== "string" ? false : !!JSON.parse(enableSameDomainHandleData);
        return enableSameDomainHandle;
    }

})(jQuery);
