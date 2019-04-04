function numberValidatorCondition(value, rule) {
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

var default_plugins = [
    'sortable',
    'filter-description',
    'unique-filter',
    'bt-tooltip-errors',
    'bt-selectpicker',
    'bt-checkbox',
    'invert',
];

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

function getDefaultConditionConfig(ruleNumberDownRateName, ruleNumberUpRateName, ruleNumberName){
    var default_filters = getDefaultFilter(ruleNumberDownRateName, ruleNumberUpRateName, ruleNumberName);
    return {
        plugins: default_plugins,
        allow_empty: true,
        filters: default_filters,
        rules: null,
        lang: globalConfig.default_lang,
    };
}

function getDefaultFilter(ruleNumberDownRateName, ruleNumberUpRateName, ruleNumberName) {
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

    if(ruleNumberDownRateName && ruleNumberDownRateName!=null){
        default_filters.splice(10,0,{
            id: RULE_NUMBER_DOWN_RATE_ID,
            label: ruleNumberDownRateName,
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
            validation: {
                callback: numberValidatorCondition
            },
        })
    }

    if(ruleNumberUpRateName && ruleNumberUpRateName!=null){
        default_filters.splice(10,0,{
            id: RULE_NUMBER_UP_RATE_ID,
            label: ruleNumberUpRateName,
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
            validation: {
                callback: numberValidatorCondition
            },
        })
    }

    if(ruleNumberName && ruleNumberName!=null){
        default_filters.splice(10,0,{
            id: RULE_NUMBER_ID,
            label: ruleNumberName,
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
            validation: {
                callback: numberValidatorCondition
            },
        })

    }

    return default_filters;
}