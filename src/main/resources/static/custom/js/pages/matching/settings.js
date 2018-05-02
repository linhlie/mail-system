
(function () {
    "use strict";
    var formId = '#matchingConditionSettingsForm';
    var saveSourceBtnId = '#saveSourceBtn';
    var getSourceBtnId = '#getSourceBtn';
    var saveDestinationBtnId = '#saveDestinationBtn';
    var getDestinationBtnId = '#getDestinationBtn';
    var submitFormBtnId = '#submitFormBtn';
    var extractSourceBtnId = '#extractSourceBtn';
    var extractDestinationBtnId = '#extractDestinationBtn';
    var matchingWordsAreaId = '#matchingWordsArea';

    var sourceBuilderId = '#source-builder';
    var destinationBuilderId = '#destination-builder';
    var matchingBuilderId = '#matching-builder';


    $(function () {
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
                }
            ]
        };

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
            id: '4',
            label: '数値',
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
            validation: {
                callback: function(value, rule) {
                    if (!value || value.trim().length === 0) {
                        return "Value can not be empty!";
                    } else if (rule.operator.type !== 'in') {
                        //TODO: japanese number
                        var pattern = /^\d+$/;
                        var match = pattern.test(value);
                        if(!match){
                            return "Value must be a number greater than or equal to 0";
                        }
                    }
                    return true;
                }
            },
        }, {
            id: '5',
            label: '数値(上代)',
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in']
        }, {
            id: '6',
            label: '数値(下代)',
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in']
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

        var default_source_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: default_source_rules
        };

        var default_destination_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: default_destination_rules
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
                    return '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist" placeholder=""/>';
                },
                operators: ['contains', 'not_contains', 'equal', 'not_equal']
            }, {
                id: '1',
                label: '受信者',
                type: 'string',
                input: function(rule, inputName) {
                    return '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist" placeholder=""/>';
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
                    return '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist" placeholder=""/>';
                },
                operators: ['contains', 'not_contains', 'equal', 'not_equal']
            }, {
                id: '4',
                label: '数値',
                type: 'string',
                input: function(rule, inputName) {
                    return '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist2" placeholder=""/>';
                },
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in']
            }, {
                id: '5',
                label: '数値(上代)',
                type: 'string',
                input: function(rule, inputName) {
                    return '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist2" placeholder=""/>';
                },
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in']
            }, {
                id: '6',
                label: '数値(下代)',
                type: 'string',
                input: function(rule, inputName) {
                    return '<input class="matchingValue black-down-triangle" type="text" name="' + inputName + '" list="itemlist2" placeholder=""/>';
                },
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in']
            }],

            rules: null
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
        setButtonClickListenter(submitFormBtnId, submit);
        setButtonClickListenter(extractSourceBtnId, extractSource);
        setButtonClickListenter(extractDestinationBtnId, extractDestination);
    });
    
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
        var datalistKey = "/user/matchingSettings/listSourceKey";
        var datalistStr = localStorage.getItem(datalistKey);
        var datalist = JSON.parse(datalistStr);
        datalist = datalist || [];
        showNamePrompt(datalist, function (name) {
            if (name != null && name.length > 0) {
                if(datalist.indexOf(name) < 0){
                    datalist.push(name);
                }
                localStorage.setItem(datalistKey, JSON.stringify(datalist));
                saveListData(
                    "/user/matchingSettings/source",
                    name,
                    result
                )
            }
        })
    }
    
    function showNamePrompt(datalist, callback) {
        $('#dataModal').modal();
        datalist = datalist || [];
        $( '#dataModalName').val('');
        $('#keylist').html('');
        for(var i = 0; i < datalist.length; i++){
            $('#keylist').append("<option value='" + datalist[i] + "'>");
        }
        setInputAutoComplete("dataModalName");
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

    function saveDestinationListData(){
        var result = $(destinationBuilderId).queryBuilder('getRules');
        if ($.isEmptyObject(result)) return;
        var datalistKey = "/user/matchingSettings/listDestinationKey";
        var datalistStr = localStorage.getItem(datalistKey);
        var datalist = JSON.parse(datalistStr);
        datalist = datalist || [];
        showNamePrompt(datalist, function (name) {
            if (name != null && name.length > 0) {
                if(datalist.indexOf(name) < 0){
                    datalist.push(name);
                }
                localStorage.setItem(datalistKey, JSON.stringify(datalist));
                saveListData(
                    "/user/matchingSettings/destination",
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
        });
        $( "." + className ).on('mouseleave', function() {
            if ($(this).val() == '') {
                $(this).val($(this).attr('placeholder'));
            }
        });
    }
    
    function getSourceListData(skip) {
        if(skip){
            getListData("/user/matchingSettings/source", null, sourceBuilderId);
        } else {
            var datalistKey = "/user/matchingSettings/listSourceKey";
            var datalistStr = localStorage.getItem(datalistKey);
            var datalist = JSON.parse(datalistStr);
            datalist = datalist || [];
            showNamePrompt(datalist, function (name) {
                if (name != null && name.length > 0) {
                    getListData("/user/matchingSettings/source", name, sourceBuilderId);
                }
            })
        }
    }

    function getDestinationListData(skip) {
        if(skip){
            getListData("/user/matchingSettings/destination", null, destinationBuilderId);
        } else {
            var datalistKey = "/user/matchingSettings/listDestinationKey";
            var datalistStr = localStorage.getItem(datalistKey);
            var datalist = JSON.parse(datalistStr);
            datalist = datalist || [];
            showNamePrompt(datalist, function (name) {
                if (name != null && name.length > 0) {
                    getListData("/user/matchingSettings/destination", name, destinationBuilderId);
                }
            })
        }
    }

    function getListData(url, name, builderId) {
        var data = null;
        if(name && name.length > 0){
            var key = url + "@" + name;
            data = localStorage.getItem(key) != null ? JSON.parse(localStorage.getItem(key)) : null;
        }
        if(data != null){
            data = addDefaultReceiveDateRow(data);
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
        var data = {
            "conditionData" : sourceConditionData,
            "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
            "spaceEffective": $('input[name=spaceEffective]:checked', formId).val() === "true",
        };
        sessionStorage.setItem("extractSourceData", JSON.stringify(data));
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
        var data = {
            "conditionData" : destinationConditionData,
            "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
            "spaceEffective": $('input[name=spaceEffective]:checked', formId).val() === "true",
        };
        sessionStorage.setItem("extractDestinationData", JSON.stringify(data));
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
        var form = {
            "sourceConditionData" : sourceConditionData,
            "destinationConditionData" : destinationConditionData,
            "matchingConditionData" : matchingConditionData,
            "matchingWords": matchingWords,
            "distinguish": $('input[name=distinguish]:checked', formId).val() === "true",
            "spaceEffective": $('input[name=spaceEffective]:checked', formId).val() === "true"
        };
        sessionStorage.setItem("matchingConditionData", JSON.stringify(form));
        // window.location = '/user/matchingResult';
        var win = window.open('/user/matchingResult', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('Please allow popups for this website');
        }
    }

})(jQuery);
