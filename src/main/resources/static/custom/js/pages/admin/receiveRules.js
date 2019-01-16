
(function () {
    "use strict";
    var formId = '#receiveRuleSettingsForm';

    var receiveBuilderId = '#receive-builder';
    var markABuilderId = '#mark-a-builder';
    var markBBuilderId = '#mark-b-builder';
    var saveReceiveBtnId = '#saveReceiveBtn';
    var getReceiveBtnId = '#getReceiveBtn';
    var saveToTrashBoxId = '#saveToTrashBox';

    var saveMarkABtnId = '#saveMarkABtn';
    var getMarkABtnId = '#getMarkABtn';

    var saveMarkBBtnId = '#saveMarkBBtn';
    var getMarkBBtnId = '#getMarkBBtn';

    var collapsedPrefixKey = "/user/matchingSettings/collapsed";
    var collapseViewPostfix = "-collapse-view";

    var saveReceiveRuleBtnId = "#saveReceiveRuleBtn";
    var saveMarkConditionsBtnId = "#saveMarkConditionsBtn";

    var RuleTypes = {
        RECEIVE: 0,
        MARKA: 1,
        MARKB: 2,
    }
    var ruleNameList = {
        "0" : [],
        "1" : [],
        "2" : [],
    }

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
        }

        var default_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: null,
            lang: globalConfig.default_lang,
        };

        $(receiveBuilderId).queryBuilder(default_configs);
        $(markABuilderId).queryBuilder(default_configs);
        $(markBBuilderId).queryBuilder(default_configs);
        var group = $(receiveBuilderId)[0].queryBuilder.model.root;
        if(group){
            group.empty();
        }
        group = $(markABuilderId)[0].queryBuilder.model.root;
        if(group){
            group.empty();
        }

        group = $(markBBuilderId)[0].queryBuilder.model.root;
        if(group){
            group.empty();
        }

        setButtonClickListenerByName("builder-ec", onExpandCollapseBuilder);
        loadDefaultSettings();
        $(window).on('beforeunload', saveDefaultSettings);
        $(document).on("keydown", keydownHandler);
        setButtonClickListenter(saveReceiveRuleBtnId, saveReceiveRule);
        setButtonClickListenter(saveMarkConditionsBtnId, saveMarkConditions);
        setButtonClickListenter(saveReceiveBtnId, saveReceive);
        setButtonClickListenter(getReceiveBtnId, getReceive);
        setButtonClickListenter(saveMarkABtnId, saveMarkA);
        setButtonClickListenter(getMarkABtnId, getMarkA);
        setButtonClickListenter(saveMarkBBtnId, saveMarkB);
        setButtonClickListenter(getMarkBBtnId, getMarkB);
        $(':radio[name="receiveType"]').change(function() {
            var type = $(this).filter(':checked').val();
            updateReceiveMailBlocker(type);
        });
        loadData();
    });

    function keydownHandler(e) {
        if((e.which || e.keyCode) == 116) {
            e.preventDefault();
            $(saveReceiveRuleBtnId).click();
        } else if((e.which || e.keyCode) == 117) {
            e.preventDefault();
            $(saveMarkConditionsBtnId).click();
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

    function saveReceiveRule() {
        var rules = $(receiveBuilderId).queryBuilder('getRules');
        if ($.isEmptyObject(rules)) return;
        var data = {
            receiveMailType: $('input[name=receiveType]:checked', formId).val(),
            receiveMailRule: JSON.stringify(rules),
            saveToTrashBox: $(saveToTrashBoxId).is(":checked") ? "1" : "0",
        };

        save("/admin/receiveRuleSettings/saveReceiveReceiveRuleBundle", JSON.stringify(data))
    }

    function saveMarkConditions() {
        var markAConditions = $(markABuilderId).queryBuilder('getRules');
        if ($.isEmptyObject(markAConditions)) return;
        var markBConditions = $(markBBuilderId).queryBuilder('getRules');
        if ($.isEmptyObject(markBConditions)) return;
        var data = {
            markReflectionScope: $('input[name=markReflectionScope]:checked', formId).val(),
            markAConditions: JSON.stringify(markAConditions),
            markBConditions: JSON.stringify(markBConditions),
        };
        save("/admin/receiveRuleSettings/saveMarkReflectionScopeBundle", JSON.stringify(data))
    }
    
    function saveReceive() {
        saveRule(receiveBuilderId, RuleTypes.RECEIVE);
    }
    
    function getReceive() {
        getRule(RuleTypes.RECEIVE, function (rule) {
            if(rule) {
                setReceiveMailRule(rule);
            }
        })
    }

    function showRuleNotFoundError() {
        setTimeout(function () {
            alert("データが見つかりません");
        }, 10);
    }

    function showCannotLoadDataError() {
        setTimeout(function () {
            alert("エラーが発生しました。データをロードできませんでした。");
        }, 10);
    }
    
    function saveMarkA() {
        saveRule(markABuilderId, RuleTypes.MARKA);
    }
    
    function getMarkA() {
        getRule(RuleTypes.MARKA, function (rule) {
            if(rule) {
                setMarkARule(rule);
            }
        })
    }

    function saveMarkB() {
        saveRule(markBBuilderId, RuleTypes.MARKB);
    }
    
    function getMarkB() {
        getRule(RuleTypes.MARKB, function (rule) {
            if(rule) {
                setMarkBRule(rule);
            }
        })
    }
    
    function saveRule(builderId, type) {
        var rule = $(builderId).queryBuilder('getRules');
        if ($.isEmptyObject(rule)) return;
        showNamePrompt(ruleNameList[type], function (name) {
            if (name == null || name.length == 0) return;
            if(ruleNameList[type].indexOf(name) < 0){
                ruleNameList[type].push(name);
            }
            var data = {
                type: type,
                name: name,
                rule: JSON.stringify(rule)
            };
            save("/admin/receiveRuleSettings/saveRule", JSON.stringify(data), "保存中...");
        })
    }

    function getRule(type, callback) {
        showNamePrompt(ruleNameList[type], function (name) {
            if (name == null || name.length == 0) return;
            getRuleHandle(name, type,
                function onSuccess(response) {
                    if(!response || !response.list || response.list.length < 1) {
                        callback();
                        showRuleNotFoundError();
                    } else {
                        var item = response.list[0];
                        callback(item.rule);
                    }
                },
                function onError(e) {
                    callback();
                    showCannotLoadDataError();
                }
            );
        });
    }

    function getRuleHandle(name, type, onSuccess, onError) {
        var data = {
            type: type,
            name: name,
        };
        post("/admin/receiveRuleSettings/getRule", JSON.stringify(data), "ローディング...", onSuccess, onError);
    }
    
    function showNamePrompt() {
        
    }
    
    function showPrompt() {
        var name = prompt("保存された名前を入力してください", "");
        return name;
    }

    function loadData() {
        $('body').loadingModal('destroy');
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
            url: "/admin/receiveRuleSettings/load",
            cache: false,
            timeout: 600000,
            success: function (data) {
                $('body').loadingModal('hide');
                if (data && data.status) {
                    handleRuleNameList(data.list);
                    var jsonData = getJson(data.json);
                    setSettingsData(jsonData);
                } else {
                    console.error("[ERROR] dashboard load data failed: ");
                }
            },
            error: function (e) {
                $('body').loadingModal('hide');
                console.error("[ERROR] dashboard load data error: ", e);
            }
        });
    }

    function handleRuleNameList(list) {
        for(var i = 0; i < list.length; i++) {
            var item = list[i];
            ruleNameList[item.type].push(item.name);
        }
    }
    
    function getJson(rawStr) {
        var json = null;
        try {
            return JSON.parse(rawStr);
        } catch (e) {
            console.warn("getJson error: ", e);
        }
        return json;
    }
    
    function setSettingsData(data) {
        setReceiveMailType(data.receiveMailType);
        setReceiveMailRule(data.receiveMailRule);
        setSaveToTrashBox(data.saveToTrashBox);
        setMarkARule(data.markAConditions);
        setMarkBRule(data.markBConditions);
        setMarkReflectionScope(data.markReflectionScope);
    }

    function setReceiveMailType(type) {
        $("input[name=receiveType][value=" + type + "]").attr('checked', 'checked');
        updateReceiveMailBlocker(type)
    }

    function updateReceiveMailBlocker(type) {
        if(type == "1") {
            $("#receive-rule-blocker").show();
        } else if(type == "2") {
            $("#receive-rule-blocker").hide();
        }
    }
    
    function setSaveToTrashBox(value) {
        $(saveToTrashBoxId).prop('checked', value == "1");
    }

    function setReceiveMailRule(ruleStr) {
        var rule = getJson(ruleStr);
        setBuilderRules(receiveBuilderId, rule);
    }

    function setMarkARule(ruleStr) {
        var rule = getJson(ruleStr);
        setBuilderRules(markABuilderId, rule);
    }

    function setMarkBRule(ruleStr) {
        var rule = getJson(ruleStr);
        setBuilderRules(markBBuilderId, rule);
    }

    function setMarkReflectionScope(scope) {
        $("input[name=markReflectionScope][value=" + scope + "]").attr('checked', 'checked');
    }

    function setBuilderRules(builderId, rules) {
        if(!rules) return;
        replaceCondition(rules);
        $(builderId).queryBuilder('setRules', rules);
    }

    function setButtonClickListenerByName(name, callback) {
        $("button[name='"+name+"']").off('click');
        $("button[name='"+name+"']").click(function () {
            if(typeof callback == "function"){
                callback.apply(this);
            }
        })
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

    function loadDefaultSettings() {
        loadExpandCollapseSetting(receiveBuilderId);
        loadExpandCollapseSetting(markABuilderId);
        loadExpandCollapseSetting(markBBuilderId);
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

    function saveDefaultSettings() {
        localStorage.setItem(getCollapseKey(receiveBuilderId), $(receiveBuilderId).is(":hidden"));
        localStorage.setItem(getCollapseKey(markABuilderId), $(markABuilderId).is(":hidden"));
        localStorage.setItem(getCollapseKey(markBBuilderId), $(markBBuilderId).is(":hidden"));
    }

    function getCollapseKey(builderId) {
        return collapsedPrefixKey + "-" + builderId;
    }
    
    function save(url, data, text) {
        post(url, data, text, function() {}, function () {})
    }
    
    function post(url, data, text, onSuccess, onError) {
        text = text || "更新中...";
        $('body').loadingModal('destroy');
        $('body').loadingModal({
            position: 'auto',
            text: text,
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: url,
            data: data,
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function () {
                $('body').loadingModal('hide');
                if(typeof onSuccess === "function"){
                    onSuccess.apply(this, arguments);
                }
            },
            error: function () {
                $('body').loadingModal('hide');
                if(typeof onError === "function"){
                    onError.apply(this, arguments);
                }
            }
        });
    }

    function showNamePrompt(datalist, callback) {
        $('#dataModal').modal();
        $( '#dataModalName').val('');
        updateKeyList(datalist);
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

    function updateKeyList(datalist) {
        datalist = datalist || [];
        $('#keylist').html('');
        for(var i = 0; i < datalist.length; i++){
            $('#keylist').append("<option value='" + datalist[i] + "'>");
        }
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
