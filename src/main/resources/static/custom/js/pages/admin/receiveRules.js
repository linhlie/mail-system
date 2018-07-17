
(function () {
    "use strict";
    var formId = '#receiveRuleSettingsForm';

    var receiveBuilderId = '#receive-builder';
    var markABuilderId = '#mark-a-builder';
    var markBBuilderId = '#mark-b-builder';

    var collapsedPrefixKey = "/user/matchingSettings/collapsed";
    var collapseViewPostfix = "-collapse-view";

    var saveReceiveRuleBtnId = "#saveReceiveRuleBtn";
    var saveMarkConditionsBtnId = "#saveMarkConditionsBtn";

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
                "equal": "等しい ＝",
                "not_equal": "異なる ≠",
                "in": "いずれか ...",
                "not_in": "いずれでもない",
                "less": "未満 ＜",
                "less_or_equal": "以下 ≦",
                "greater": "超 ＞",
                "greater_or_equal": "以上 ≧",
                "between": "範囲",
                "not_between": "範囲外",
                "begins_with": "開始する",
                "not_begins_with": "開始ではない",
                "contains": "含む 〇",
                "not_contains": "含まない ×",
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

        var default_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: null,
            lang: default_lang,
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
        };

        save("/admin/receiveRuleSettings/save", JSON.stringify(data))
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
        save("/admin/receiveRuleSettings/saveMarkReflectionScope", JSON.stringify(data))
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
        setMarkARule(data.markAConditions);
        setMarkBRule(data.markBConditions);
        setMarkReflectionScope(data.markReflectionScope);
    }

    function setReceiveMailType(type) {
        $("input[name=receiveType][value=" + type + "]").attr('checked', 'checked');
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
    
    function save(url, data) {
        $('body').loadingModal('destroy');
        $('body').loadingModal({
            position: 'auto',
            text: '更新中...',
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
            success: function (response) {
                $('body').loadingModal('hide');
            },
            error: function (e) {
                $('body').loadingModal('hide');
            }
        });
    }

})(jQuery);
