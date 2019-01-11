
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

    var ruleInvalidateIds = [];

    var RULE_NUMBER_ID = 4;
    var RULE_NUMBER_UP_RATE_ID = 5;
    var RULE_NUMBER_DOWN_RATE_ID = 6;

    var ruleNumberId = "ruleNumber";
    var ruleNumberUpRateId = "ruleNumberUpRate";
    var ruleNumberDownRateId = "ruleNumberDownRate";

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

        var ruleNumberDownRate = $('#'+ruleNumberDownRateId).text();
        console.log(ruleNumberDownRate);
        if(!ruleNumberDownRate || ruleNumberDownRate==null){
            ruleInvalidateIds.push(RULE_NUMBER_DOWN_RATE_ID);
        }else{
            default_filters.splice(10,0,{
                id: RULE_NUMBER_DOWN_RATE_ID,
                label: ruleNumberDownRate,
                type: 'string',
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: numberValidator
                },
            })
        }

        var ruleNumberUpRate = $('#'+ruleNumberUpRateId).text();
        console.log(ruleNumberUpRate);
        if(!ruleNumberUpRate || ruleNumberUpRate==null){
            ruleInvalidateIds.push(RULE_NUMBER_UP_RATE_ID);
        }else{
            default_filters.splice(10,0,{
                id: RULE_NUMBER_UP_RATE_ID,
                label: ruleNumberUpRate,
                type: 'string',
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: numberValidator
                },
            })
        }

        var ruleNumber = $('#'+ruleNumberId).text();
        console.log(ruleNumber);
        if(!ruleNumber || ruleNumber==null){
            ruleInvalidateIds.push(RULE_NUMBER_ID);
        }else{
            default_filters.splice(10,0,{
                id: RULE_NUMBER_ID,
                label: ruleNumber,
                type: 'string',
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: numberValidator
                },
            })
        }
        console.log(ruleInvalidateIds);
        console.log(default_filters);

        var default_source_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: null,
            lang: globalConfig.default_lang,
        };

        var default_destination_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: null,
            lang: globalConfig.default_lang,
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
        initDuplicateHandle();
        initSameDomainHandle();
        initcheckDomainInPartnerGroup();
        loadDefaultSettings();
        $(window).on('beforeunload', saveDefaultSettings);
        $(document).on("keydown", keydownHandler);
    });

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
        console.log("sourceConditions: ", sourceConditions);
        console.log("destinationConditions: ", destinationConditions);
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

    function saveSourceListData(){
        var result = $(sourceBuilderId).queryBuilder('getRules');
        if ($.isEmptyObject(result)) return;
        var datalistStr = localStorage.getItem(sourceListKey);
        var datalist = JSON.parse(datalistStr);
        datalist = datalist || [];
        var defaultPromptName = getInputValue(sourceConditionNameId);
        showNamePrompt(datalist, sourceListKey, sourcePrefixUrlKey, defaultPromptName, function (name) {
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
    
    function showNamePrompt(datalist, listKey, prefixUrlKey, defaultName, callback) {
        $('#dataModal').modal();
        $( '#dataModalName').val(defaultName);
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
        var defaultPromptName = getInputValue(destinationConditionNameId);
        showNamePrompt(datalist, destinationListKey, destinationPrefixUrlKey, defaultPromptName, function (name) {
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
        var defaultPromptName = getInputValue(matchingConditionNameId);
        showNamePrompt(datalist, matchingListKey, matchingPrefixUrlKey, defaultPromptName, function (name) {
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
        var inputId = getInputIdFromUrl(url);
        setInputValue(inputId, name);
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
    
    function getSourceListData() {
        var datalistStr = localStorage.getItem(sourceListKey);
        var datalist = JSON.parse(datalistStr);
        datalist = datalist || [];
        showNamePrompt(datalist, sourceListKey, sourcePrefixUrlKey, "", function (name) {
            if (name != null && name.length > 0) {
                getListData(sourcePrefixUrlKey, name, sourceBuilderId);
            }
        })
    }

    function getDestinationListData(skip) {
        var datalistStr = localStorage.getItem(destinationListKey);
        var datalist = JSON.parse(datalistStr);
        datalist = datalist || [];
        showNamePrompt(datalist, destinationListKey, destinationPrefixUrlKey, "", function (name) {
            if (name != null && name.length > 0) {
                getListData(destinationPrefixUrlKey, name, destinationBuilderId);
            }
        })
    }

    function getMatchingListData(skip) {
        var datalistStr = localStorage.getItem(matchingListKey);
        var datalist = JSON.parse(datalistStr);
        datalist = datalist || [];
        showNamePrompt(datalist, matchingListKey, matchingPrefixUrlKey, "", function (name) {
            if (name != null && name.length > 0) {
                getListData(matchingPrefixUrlKey, name, matchingBuilderId, true);
            }
        })
    }

    function getListData(url, name, builderId, skipAddDefaultRow) {
        var data = null;
        if(name && name.length > 0){
            var key = url + "@" + name;
            data = localStorage.getItem(key) != null ? JSON.parse(localStorage.getItem(key)) : null;
        }
        if(data != null){
            // var enableAddDefaultRow = !skipAddDefaultRow;
            // if(enableAddDefaultRow) {
            //     data = addDefaultReceiveDateRow(data);
            // }
            var inputId = getInputIdFromUrl(url);
            setInputValue(inputId, name);
            replaceCondition(data);
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
            alert('Please allow popups for this website');
        }
    }
    
    function extractDestination() {
        var destinationConditionData = buildDataFromBuilder(destinationBuilderId);
        if(!destinationConditionData) return;
        const duplicateSettingData = getCachedDuplicationSettingData();
        var data = {
            "conditionData" : destinationConditionData,
            // "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
            // "spaceEffective": $('input[name=spaceEffective]:checked', formId).val() === "true",
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
        // var spaceEffective = $('input[name=spaceEffective]:checked', formId).val() === "true";
        // var distinguish = $('input[name=distinguish]:checked', formId).val() === "true";
        var spaceEffective = false;
        var distinguish = false;
        const duplicateSettingData = getCachedDuplicationSettingData();
        var form = {
            "sourceConditionData" : sourceConditionData,
            "destinationConditionData" : destinationConditionData,
            "matchingConditionData" : matchingConditionData,
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
    
    function initcheckDomainInPartnerGroup() {
        let checkDomainInPartnerGroup = getCachedCheckDomainInPartnerGroupSettingData();
        $(checkDomainInPartnerGroupId).prop('checked', checkDomainInPartnerGroup);
        $(checkDomainInPartnerGroupId).change(function() {
            var enable = $(this).is(":checked");
            localStorage.setItem(checkDomainInPartnerGroupKey, enable);
        });
    }
    
    function getCachedCheckDomainInPartnerGroupSettingData() {
        let checkDomainInPartnerGroupData = localStorage.getItem(checkDomainInPartnerGroupKey);
        let enablecheckDomainInPartnerGroup = typeof checkDomainInPartnerGroupData !== "string" ? false : !!JSON.parse(checkDomainInPartnerGroupData);
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

})(jQuery);
