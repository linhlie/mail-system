
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
    var partners = null;
    var engineerCondition = null;
    var hourlyMonneyCondition = null;
    var listEngineerCondition = [];
    
    var collapsedPrefixKey = "/user/emailMatchingEngineer/collapsed";
    var destinationListKey = "/user/emailMatchingEngineer/listDestinationKey";
    var destinationPrefixUrlKey = "/user/emailMatchingEngineer/destination";
    var destinationConditionKey = "destinationCondition-email-matching-engineer";
    var destinationConditionNameKey = "destinationConditionName-email-matching-engineer";
    var matchingConditionEmailMatchingEngineerKey = "matchingConditionData-email-matching-engineer";
    var distinguishEmailMatchingEngineerKey = "distinguish-email-matching-engineer";
    var spaceEffectiveEmailMatchingEngineerKey = "spaceEffective-email-matching-engineer";
    
    var collapseViewPostfix = "-collapse-view-email-matching-engineer";

    var formFields = [
        {type: "input", name: "id"},
        {type: "input", name: "name"},
        {type: "textarea", name: "matchingWord"},
        {type: "textarea", name: "notGoodWord"},
    ];

    var GroupPartnerRowTypes = {
        ORIGINAL: "original",
        NEW: "add",
    }

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
    var default_hourlyMoney_rules = {
            condition: "AND",
            rules: [
                {
                    id: "4",
                    operator: "greater_or_equal",
                    type: "String",
                    value: "",
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
    	    }, {
    	        id: '15',
    	    label: 'マーク',
    	        type: 'string',
    	        operators: ['equal', 'not_equal']
    	 }];
    	

    	 var default_filters_houtlyMoney = [{
    	    id: '4',
    	    label: '数値',
    	    type: 'string',
    	    operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less'],
    	       validation: {
    	          callback: numberValidator
    	       },  	 
    	 }];
    	 
 		var default_rules_houtlyMoney = {
    			"condition":"AND",
    			"rules":[],
    			"valid":true};

    	 var default_destination_configs = {
    	    plugins: default_plugins,
    	    allow_empty: true,
    	    filters: default_filters,
    	    rules: null,
    	    lang: globalConfig.default_lang,
    	 };
    	 
    	 var default_hourlyMoney_configs = {
    	    plugins: default_plugins,
    	    allow_empty: true,
    	    filters: default_filters_houtlyMoney,
    	    rules: null,
    	    lang: globalConfig.default_lang,
    	 };

    	$(destinationBuilderId).queryBuilder(default_destination_configs);
    	$(hourlyMoneyBuilderId).queryBuilder(default_hourlyMoney_configs);
    	    
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
        draggingSetup();
        loadDefaultSettings();
        loadEngineers();
    });
    
    function initDuplicateHandle() {
        const duplicateSettingData = getCachedDuplicationSettingData();
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
        const domainSettingData = getCachedDomainSettingData();
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
        $("input[name='engineerFilter']").click(function() {
            var disabled = this.value !== "4";
            $(lastMonthActiveId).MonthPicker('option', 'Disabled', disabled);
        });
    }

    function initLastMonthActive() {
        var now = new Date();
        var selectedMonth = now.getFullYear() + "年" + (now.getMonth() + 1) + "月";
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
            Disabled: true,
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
    	if(data.moneyCondition == null){
        	var money = data.monetaryMoney;
    		if(money==null){
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
           				"operator":"equal",
           				"value":money
           					}],
           			"valid":true};
           		$(hourlyMoneyBuilderId).queryBuilder('setRules', rules);
        	}
    	}else{
    		$(hourlyMoneyBuilderId).queryBuilder('setRules', data.moneyCondition);
    	}
    }
    
    function setMoneyCondition(listData){
    	for(var i=0;i<listData.length;i++){
        	if(listData[i].moneyCondition == null){
            	var money = listData[i].monetaryMoney;
        		if(money!=null){
               		var rules = {
               			"condition":"AND",
               			"rules":[{
               				"id":"4",
               				"field":"1",
               				"type":"string",
               				"input":"text",
               				"operator":"equal",
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
        const duplicateSettingData = getCachedDuplicationSettingData();
        const domainSettingData = getCachedDomainSettingData();
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
        console.log(form);
        saveDefaultSettings();
        var win = window.open('/user/emailMatchingEngineerResult', '_blank');
        if (win) {
            //Browser has allowed it to be opened
            win.focus();
        } else {
            //Browser has blocked it
            alert('Please allow popups for this website');
        }
    }
    
    function getCachedSameDomainSettingData() {
        let enableSameDomainHandleData = localStorage.getItem("enableSameDomainHandle-email-matching-engineer");
        let enableSameDomainHandle = typeof enableSameDomainHandleData !== "string" ? false : !!JSON.parse(enableSameDomainHandleData);
        return enableSameDomainHandle;
    }
    
    function updateListEngineer(engineerCondition){
    	if(engineerCondition==null) return;   	
    	for(var i=0;i<engineers.length;i++){
    		if(engineers[i]!=null){
    			if(engineerCondition.id == engineers[i].id){
    				engineers[i].matchingWord = engineerCondition.matchingWord;
    				engineers[i].notGoodWord = engineerCondition.notGoodWord;
    		        var monneyCondition = $(hourlyMoneyBuilderId).queryBuilder('getRules');
    				engineers[i].moneyCondition = monneyCondition;
    		        console.log(engineers[i]);
    			}
    		}
    	}
    }

    function applyConditionOnClick() {
        clearFormValidate();
        var validated = engineerFormValidate();
        if(!validated) return;
        var validatedRule = $(hourlyMoneyBuilderId).queryBuilder('getRules');
        if(!validatedRule) return;
        engineerCondition = getFormData();
        updateListEngineer(engineerCondition);
        clearEngineerOnClick();
        selectNextRow(engineers);
    }
    
    function disableApplyConditionEnginer(disable) {
        $(applyConditionBtnId).prop('disabled', disable);
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
        $(hourlyMoneyBuilderId).queryBuilder('setRules', default_hourlyMoney_rules);
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
        return {
            filterType: filterType,
            filterDate: filterDate,
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
        loadExpandCollapseSetting(hourlyMoneyBuilderId);
        
        var destinationConditionsStr = localStorage.getItem(destinationConditionKey);
        var destinationConditions = destinationConditionsStr == null || JSON.parse(destinationConditionsStr) == null ? default_destination_rules : JSON.parse(destinationConditionsStr);
        $(destinationBuilderId).queryBuilder('setRules', destinationConditions);
        $(hourlyMoneyBuilderId).queryBuilder('setRules', default_hourlyMoney_rules);
        
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
    
    function setInputValue(inputId, value) {
        $(inputId).val(value);
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
    
    function saveListData(url, name,  data) {
        var key = url + "@" + name;
        var inputId = getInputIdFromUrl(url);
        setInputValue(inputId, name);
        localStorage.setItem(key, JSON.stringify(data));
    }
    
    function getListData(url, name, builderId, skipAddDefaultRow) {
        var data = null;
        if(name && name.length > 0){
            var key = url + "@" + name;
            data = localStorage.getItem(key) != null ? JSON.parse(localStorage.getItem(key)) : null;
        }
        if(data != null){
            var inputId = getInputIdFromUrl(url);
            setInputValue(inputId, name);
            $(builderId).queryBuilder('setRules', data);
        } else {
            alert("見つけませんでした。");
        }
    }
    
    function extractDestination() {
        var destinationConditionData = buildDataFromBuilder(destinationBuilderId);
        if(!destinationConditionData) return;
        const duplicateSettingData = getCachedDuplicationSettingData();
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
            alert('Please allow popups for this website');
        }
    }
    
    function getCachedDuplicationSettingData() {
        let enableDuplicateHandleData = localStorage.getItem("enableDuplicateHandle-email-matching-engineer");
        let enableDuplicateHandle = typeof enableDuplicateHandleData !== "string" ? false : !!JSON.parse(enableDuplicateHandleData);
        let handleDuplicateSenderData = localStorage.getItem("handleDuplicateSender-email-matching-engineer");
        let handleDuplicateSender = typeof handleDuplicateSenderData !== "string" ? false : !!JSON.parse(handleDuplicateSenderData);
        let handleDuplicateSubjectData = localStorage.getItem("handleDuplicateSubject-email-matching-engineer");
        let handleDuplicateSubject = typeof handleDuplicateSubjectData !== "string" ? false : !!JSON.parse(handleDuplicateSubjectData);
        return {
            enable: enableDuplicateHandle,
            sender: handleDuplicateSender,
            handleDuplicateSender: enableDuplicateHandle && handleDuplicateSender,
            subject: handleDuplicateSubject,
            handleDuplicateSubject: enableDuplicateHandle && handleDuplicateSubject,
        }
    }
    
    function getCachedDomainSettingData() {
        let handleDomainPartnerCurrentData = localStorage.getItem("handleDomainPartnerCurrent-email-matching-engineer");
        let handleDomainPartnerCurrent = typeof handleDomainPartnerCurrentData !== "string" ? false : !!JSON.parse(handleDomainPartnerCurrentData);
        let handleDomainPartnerGroupData = localStorage.getItem("handleDomainPartnerGroup-email-matching-engineer");
        let handleDomainPartnerGroup = typeof handleDomainPartnerGroupData !== "string" ? false : !!JSON.parse(handleDomainPartnerGroupData);
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
    
})(jQuery);
