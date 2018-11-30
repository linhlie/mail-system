
(function () {
    var partnerComboBoxId = "partnerComboBox";
    var partnerIdComboBoxId = "partnerIdComboBox";
    var partnerAddBtnId = "#partnerAdd";
    var partnerUpdateBtnId = "#partnerUpdate";
    var partnerClearBtnId = "#partnerClear";
    var formId = "#partnerForm";
    var checkboxNextSelectId = "#checkboxNext"
    var partnerTableId = "partner";
    var styleShowTableId = "#styleShowTable";
    var countDomain = "#countDomain";
    var partnerGroupTableId = "partnerGroup";
    var partners = null;
    var domains = null;
    var updatingPartnerId = null;
    var updatingDomainId = null;
    var alertContentValue = "";
    var alertLevelValue = "";

    var currentSortOrder;
    var selectedSourceTableRow;
    var idPartnerNextSelect = -1;

    var formFields = [
        {type: "input", name: "name"},
        {type: "input", name: "kanaName"},
        {type: "input", name: "partnerCode"},
        {type: "input", name: "domain1"},
        {type: "input", name: "domain2"},
        {type: "input", name: "domain3"},
        {type: "checkbox", name: "ourCompany"},
        {type: "radio", name: "companyType"},
        {type: "input", name: "companySpecificType"},
        {type: "radio", name: "stockShare"},
        {type: "radio", name: "alertLevel"},
        {type: "textarea", name: "alertContent"},
    ];

    var GroupPartnerRowTypes = {
        ORIGINAL: "original",
        NEW: "add",
    }

    var CompanyTypes = {
        LTD: 1,
        LIMITED: 2,
        GROUP: 3,
        JOINT_STOCK: 4,
        FOUNDATION: 5,
        CORPORATION: 6,
        OTHER: 7,
    };
    
	var partnerReplaceHead = '<tr>' +
		'<th class="dark">取引先名</th>' +
		'<th class="fit dark" style="text-align: center">識別ID</th>' +
        '<th class="fit dark" style="text-align: center">アラート</th>' +
		'<th colspan="1"></th>' +
		'</tr>';

    var partnerReplaceRow = '<tr role="row" class="hidden">' +
        '<td name="editPartner" rowspan="1" colspan="1" data="name" style="cursor: pointer;"><span></span></td>' +
        '<td name="editPartner" rowspan="1" colspan="1" data="partnerCode" style="cursor: pointer;"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="alertLevel" style="text-align: center"><span></span></td>' +
        '<td name="deletePartner" class="fit action deletePartnerRow" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '</tr>';
    
	var domainReplaceHead = '<tr>' +
		'<th class="dark">Domain</th>' +
		'<th colspan="2"></th>' +
		'</tr>';
	
    var domainReplaceRow = '<tr role="row" class="hidden">' +
    	'<td name="editDomain" owspan="1" colspan="1" data="domain" style="cursor: pointer;"><span></span></td>' +
    	'<td name="avoidRegister" class="fit action" rowspan="1" colspan="1" data="id">' +
    	'<button type="button">無視</button>' +
    	'</td>' +
    	'<td name="deleteDomain" class="fit action" rowspan="1" colspan="1" data="id">' +
    	'<button type="button">削除</button>' +
    	'</td>' +
    	'</tr>';

    var groupReplaceRow = '<tr name="group-partner" data-type="original" role="row" class="hidden">' +
        '<td rowspan="1" colspan="1" data="name"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="partnerCode"><span></span></td>' +
        '<td name="deleteGroupPartner" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '</tr>';

    $(function () {
        initStickyHeader();
        partnerComboBoxListener();
        partnerIdComboBoxListener();
        setButtonClickListenter(partnerAddBtnId, addPartnerOnClick);
        setButtonClickListenter(partnerUpdateBtnId, updateBtnOnClick);
        setButtonClickListenter(partnerClearBtnId, clearPartnerOnClick);
        companyTypeChangeListener();
        styleShowTableChangeListener();
        loadBusinessPartners();
        draggingSetup();
        showAlertLevelListener();
        setVisibleCountDomain("hidden");
    });

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

    function initSortPartner() {
        if(currentSortOrder){
            $("#" + partnerTableId).tablesorter(
                {
                    headers: {
                        3: {
                            sorter: false
                        }
                    },
                    sortList: currentSortOrder
                })
                .bind('sortEnd', function(event) {
                    currentSortOrder = event.target.config.sortList;
                    setNextRow(selectedSourceTableRow);
                });
        }else{
            $("#" + partnerTableId).tablesorter(
                {
                    headers: {
                        3: {
                            sorter: false
                        }
                    },
                    sortList: [[0,0]]
                })
                .bind('sortEnd', function(event) {
                    currentSortOrder = event.target.config.sortList;
                    setNextRow(selectedSourceTableRow);
                });
        }
    }
    
    function addPartnerComboBox() {
        var tr = '<tr role="row">' +
            '<td rowspan="1" colspan="1">' +
            '<select id="partnerComboBox" style="width: 100%; border: none; padding: 2px;"></select>' +
            '</td>' +
            '<td rowspan="1" colspan="1">'+
            '<select id="partnerIdComboBox" style="width: 100%; border: none; padding: 2px;"></select>' +
            '</td>' +
            '<td class="fit" rowspan="1" colspan="1">' +
            '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
            '</td>' +
            '</tr>';
        $("#" + partnerGroupTableId).append(tr);
    }
    
    function updatePartnerComboBox(options) {
        options = options ? options.slice(0) : [];
        options.sort(comparePartner);
        $('#' + partnerComboBoxId).empty();
        $('#' + partnerIdComboBoxId).empty();
        $('#' + partnerComboBoxId).append($('<option>', {
            selected: true,
            disabled: true,
            value: "",
            text : "選んでください",
        }));
        
        $('#' + partnerIdComboBoxId).append($('<option>', {
            selected: true,
            disabled: true,
            value: "",
            text : "識別ID",
        }));
        $.each(options, function (i, item) {
            $('#' + partnerComboBoxId).append($('<option>', {
                value: item.partnerCode,
                text : item.name,
            }).attr('data-id',item.id));
            
            $('#' + partnerIdComboBoxId).append($('<option>', {
                value: item.name,
                text : item.partnerCode,
            }).attr('data-id',item.id));
        });
    }
    
    function partnerComboBoxListener() {
        $('#' + partnerComboBoxId).off('change');
        $('#' + partnerComboBoxId).change(function() {
            var selected = $(this).find("option:selected");
            var name = selected.text();
            var id = selected.attr("data-id");
            var code = this.value;
            addPartnerToGroup.apply(this, [id, name, code]);
            $('#' + partnerComboBoxId).prop('selectedIndex',0);
        });
    }
    
    function partnerIdComboBoxListener() {
        $('#' + partnerIdComboBoxId).off('change');
        $('#' + partnerIdComboBoxId).change(function() {
            var selected = $(this).find("option:selected");
            var name = selected.text();
            var id = selected.attr("data-id");
            var code = this.value;
            addPartnerToGroup.apply(this, [id, code, name]);
            $('#' + partnerIdComboBoxId).prop('selectedIndex',0);
        });
    }
    
    function addPartnerToGroup(id, name, code) {
        var tr = '<tr name="group-partner" data-id="' + id + '" data-type="add" role="row">' +
            '<td rowspan="1" colspan="1" data="name"><span>' + name + '</span></td>' +
            '<td rowspan="1" colspan="1" data="partnerCode"><span>' + code + '</span></td>' +
            '<td name="deleteGroupPartner" class="fit action" rowspan="1" colspan="1" data="id">' +
            '<button type="button">削除</button>' +
            '</td> </tr>';
        $(this).closest('table').find('tr:last').before(tr);
        setDeleteGroupPartnerListener();
    }
    
    function addPartnerOnClick() {
        clearFormValidate();
        var validated = partnerFormValidate();
        if(!validated) return;
        var data = getFormData();
        var addRemoveGroupPartnerIds = getAddRemoveGroupPartnerIds();
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    title: "",
                    content: "保存に成功しました",
                    onClose: function () {
                        loadBusinessPartners();
                        clearPartnerOnClick();
                    }
                });
            } else {
                $.alert("保存に失敗しました");
            }
        }
        
        function onError(response) {
            $.alert("保存に失敗しました");
        }
        addPartner({
            builder: data,
            groupAddIds: addRemoveGroupPartnerIds.add,
            groupRemoveIds: addRemoveGroupPartnerIds.remove,
        }, onSuccess, onError)
    }
    
    function addPartnerFromDomain() {
        clearFormValidate();
        var validated = partnerFormValidate();
        if(!validated) return;
        var data = getFormData();
        var addRemoveGroupPartnerIds = getAddRemoveGroupPartnerIds();
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                	title: '',
                    content: "保存に成功しました",
                    onClose: function () {
                    	loadDomainUnregisters(selectNextRow);
                        clearPartnerOnClick();
                    }
                });
            } else {
                $.alert("Update false");
            }
        }
        
        function onError(response) {
        	$.alert("Update false");
        }
        addPartner({
            builder: data,
            groupAddIds: addRemoveGroupPartnerIds.add,
            groupRemoveIds: addRemoveGroupPartnerIds.remove,
        }, onSuccess, onError)
    }
    
    function getAddRemoveGroupPartnerIds() {
        var data = {
            add: [],
            remove: [],
        };
        $('#'+ partnerGroupTableId + ' > tbody  > tr').each(function(i, row) {
            var $row = $(row)
            var type = $row.attr("data-type");
            var id = $row.attr("data-id");
            id = parseInt(id);
            if(type == GroupPartnerRowTypes.NEW) {
                if(!isNaN(id) && data.add.indexOf(id) < 0) {
                    data.add.push(id);
                }
            } else if (type == GroupPartnerRowTypes.ORIGINAL) {
                if($row.hasClass("hidden")) {
                    if(!isNaN(id) && data.remove.indexOf(id) < 0) {
                        data.remove.push(id);
                    }
                }
            }
        });
        return data;
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
    
    function setFormData(form) {
        for (var i = 0; i < formFields.length; i++) {
            var field = formFields[i];
            if(field.type == "checkbox"){
                $("input" + "[name='" + field.name + "']").prop('checked', form[field.name]);
            } else if (field.type == "radio") {
                $("input[name=" + field.name + "][value=" + form[field.name] + "]").prop('checked', true);
            } else {
                $("" + field.type + "[name='" + field.name + "']").val(form[field.name]);
            }
        }
        alertLevelValue = form.alertLevel;
        alertContentValue = form.alertContent;
        var alertType = form.alertLevel > 0? 1 : 0;
        var showAlertLevel = form.alertLevel > 0? "visible" : "hidden";
        var disibaleAlertContent = form.alertLevel > 0? false : true;
        $("input[name = alertType][value=" + alertType +"]").prop('checked', true);
        $(".showAlertLevel").css("visibility", showAlertLevel);
        $("#alertContent").prop("disabled", disibaleAlertContent);
    }
    
    function setFormDomainUpdate(form) {
    	$('#domain1').val(form.domain);
    }
    
    function updateBtnOnClick(){
    	var type = $(styleShowTableId + ' option:selected').text();
		//Update Partner
		if(type == '取引先一覧'){
			updatePartnerOnClick();
		}
		
		//Add new Partner
		if(type == '未登録取引先一覧'){
			addPartnerFromDomain();
		}
    }
    
    function updatePartnerOnClick() {
        clearFormValidate();
        var validated = partnerFormValidate();
        if(!validated) return;
        var data = getFormData();
        var addRemoveGroupPartnerIds = getAddRemoveGroupPartnerIds();
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    title: "",
                    content: "保存に成功しました",
                    onClose: function () {
                        loadBusinessPartners(selectNextRow);
                    }
                });
            } else {
                $.alert("保存に失敗しました");
            }
        }

        function onError(response) {
            $.alert("保存に失敗しました");
        }

        updatePartner(
            updatingPartnerId,
            {
                builder: data,
                groupAddIds: addRemoveGroupPartnerIds.add,
                groupRemoveIds: addRemoveGroupPartnerIds.remove,
            },
            onSuccess,
            onError
        );
    }
    
    function partnerFormValidate() {
        var validate1 = partnerNameValidate();
        var validate2 = partnerKanaNameValidate();
        var validate3 = partnerPartnerCodeValidate();
        var validate4 = partnerDomainValidate();
        return validate1 && validate2 && validate3 && validate4;
    }
    
    function partnerNameValidate() {
        var input = $("input[name='name']");
        if(!input.val()) {
            showError.apply(input, ["必須"]);
            return false;
        }
        return true;
    }
    
    function showError(error) {
        var container = $(this).closest("div.form-group.row");
        container.addClass("has-error");
        container.find("span.form-error").text(error);
    }
    
    function partnerKanaNameValidate() {
        var input = $("input[name='kanaName']");
        if(!input.val()) {
            showError.apply(input, ["必須"]);
            return false;
        }
        return true;
    }
    
    function partnerPartnerCodeValidate() {
        var input = $("input[name='partnerCode']");
        if(!input.val()) {
            showError.apply(input, ["必須"]);
            return false;
        }
        return true;
    }
    
    function partnerDomainValidate() {
        var domain1 = $("#domain1");
        var domain2 = $("#domain2");
        var domain3 = $("#domain3");
        if(!domain1.val() && !domain2.val() && !domain3.val()) {
            showError.apply(domain1, ["最小限1つのドメインを入力して下さい"]);
            return false;
        }
        return true;
    }
    
    function clearPartnerOnClick() {
        resetForm();
        clearFormValidate();
        disableUpdatePartner(true);
        updateCompanySpecificType();
        clearUpdatingParterId();
        loadBusinessPartnerGroupData(partnerGroupTableId, []);
        resetPartnerTable();
    }
    
    function resetPartnerTable() {
        $("#" + partnerTableId).find('tr.highlight-selected').removeClass('highlight-selected');
    }
    
    function clearUpdatingParterId() {
        updatingPartnerId = null;
    }

    function resetForm() {
        $(formId).trigger("reset");
        $(".showAlertLevel").css("visibility", "hidden");
        $("#alertContent").prop("disabled", true);
        alertLevelValue=0;
        alertContentValue="";
    }
    
    function clearFormValidate() {
        $(formId).find(".has-error").removeClass('has-error');
    }
    
    function loadBusinessPartners(callback) {
        selectedSourceTableRow=null;
        function onSuccess(response) {
            if(response && response.status){
                if(typeof callback == 'function'){
                    loadBusinessPartnersData(partnerTableId, response.list, callback);
                }else{
                    loadBusinessPartnersData(partnerTableId, response.list);
                }
                updatePartnerComboBox(response.list);
            }
        }
        
        function onError(error) {

        }
        getBusinessPartners(onSuccess, onError);
    }
    
    function loadBusinessPartnersData(tableId, data, callback) {
        $("#" + tableId).css("transform", "translateY(-10px)");
        $("#" + tableId).trigger("destroy");
        partners = data;
        removeAllRow(tableId, partnerReplaceRow, partnerReplaceHead);
        if (partners.length > 0) {
            var html = partnerReplaceRow;
            for (var i = 0; i < partners.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> thead").html(partnerReplaceHead);
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("deletePartner", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = partners[index];
                if (rowData && rowData.id) {
                    doDeletePartner(rowData.id);
                }
            });
            setRowClickListener("editPartner", function () {
                var rowSelected = $(this);
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = partners[index];
                if (rowData && rowData.id) {
                    doEditPartner(rowData);
                    selectedRow(rowSelected.closest('tr'));
                }
            });
            initSortPartner();
            if(typeof callback == 'function'){
                callback();
            }
        }
    }

    function removeAllRow(tableId, replaceHtml, replaceHead) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
        if(replaceHead){
            $("#" + tableId + "> thead").html(replaceHead);
        }
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
            var cellKeysData = cell.getAttribute("data");
            if (!cellKeysData || cellKeysData.length == 0) continue;
            var cellKeys = cellKeysData.split(".");
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if (cellNode) {
                if (cellNode.nodeName == "SPAN") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    if (Array.isArray(cellData)) {
                        cellNode.textContent = cellData.length;
                    } else {
                        if(cellKeysData === "alertLevel"){
                            switch (cellData) {
                                case 1:cellData = "底";
                                    break;
                                case 2:cellData = "中";
                                    break;
                                case 3:cellData = "高";
                                    break;
                                default: cellData="";
                            }
                        }
                        cellNode.textContent = cellData;
                    }
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

    function doDeletePartner(id) {
        function onSuccess() {
            loadBusinessPartners();
            clearPartnerOnClick();
        }
        function onError() {
            $.alert("取引先の削除に失敗しました。");
        }
        $.confirm({
            title: '<b>【取引先の削除】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deletePartner(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }
    
    function doEditPartner(data) {
        clearFormValidate();
        clearPartnerOnClick();
        loadBusinessPartnerGroup(data.id);
        disableUpdatePartner(false);
        setFormData(data);
        updateCompanySpecificType(data.companyType);
        updatingPartnerId = data.id;
        
    }
    
    function doEditDomain(data) {
        clearFormValidate();
        clearPartnerOnClick();
        updatingDomainId = data.id;
        disableAddPartner(false);
        setFormDomainUpdate(data);
    }
    
    function loadBusinessPartnerGroup(partnerId) {
        function onSuccess(response) {
            if(response && response.status) {
                loadBusinessPartnerGroupData(partnerGroupTableId, response.list);
            }
        }
        function onError() {}

        getBusinessPartnerGroup(partnerId, onSuccess, onError);
    }
    
    function loadBusinessPartnerGroupData(tableId, data) {
        removeAllRow(tableId, groupReplaceRow);
        if (data.length > 0) {
            var html = groupReplaceRow;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i].withPartner, i);
            }
            $("#" + tableId + "> tbody").html(html);
            setDeleteGroupPartnerListener();
        }
        addPartnerComboBox();
        updatePartnerComboBox(partners);
        partnerComboBoxListener();
        partnerIdComboBoxListener();
    }
    
    function setDeleteGroupPartnerListener() {
        setRowClickListener("deleteGroupPartner", function () {
            //TODO:
            var tr = $(this).closest('tr');
            var type = tr.attr("data-type");
            if(type == GroupPartnerRowTypes.NEW) {
                tr.remove();
            } else if (type == GroupPartnerRowTypes.ORIGINAL) {
                tr.addClass("hidden");
            }
        });
    }

    function disableUpdatePartner(disable) {
        $(partnerUpdateBtnId).prop('disabled', disable);
    }
    
    function disableAddPartner(disable) {
        $(partnerAddBtnId).prop('disabled', disable);
    }
    
    function companyTypeChangeListener() {
        $("input[name='companyType']").click(function() {
            updateCompanySpecificType(this.value);
        });
    }

    function updateCompanySpecificType(companyType) {
        var input = $("#companySpecificType");
        if(parseInt(companyType) == CompanyTypes.OTHER) {
            input.css('visibility', 'visible');
        } else {
            input.css('visibility', 'hidden');
        }
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
                var container = $('#partnerBox');
                var topHeight = (e.pageY - container.offset().top);
                var tableHeight = Math.floor(topHeight - 10);
                tableHeight = tableHeight > 60 ? tableHeight : 60;
                tableHeight = tableHeight < 400 ? tableHeight : 400;
                $('#partnerBox').css("height", tableHeight + "px");
                $('#ghostbar2').remove();
                $(document).unbind('mousemove');
                dragging = false;
            }
        });
    }
    
    function selectNextRow(){
        if ($(checkboxNextSelectId).is(":checked") && idPartnerNextSelect>0){
            selectedSourceTableRow  = null;
            $( ".deletePartnerRow" ).each(function() {
                var rowSelected = $(this);
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = partners[index];
                if(rowData){
                    if(rowData.id == idPartnerNextSelect){
                        selectedSourceTableRow = rowSelected.closest('tr');
                    }
                }
            });
            if(selectedSourceTableRow != null){
                selectNext(selectedSourceTableRow);
            }
    	}else{
    		clearPartnerOnClick();
    	}
    }

    function selectNext(rowSelect) {
        var index = rowSelect ? rowSelect.index() : -1;
        if(index>partners.length) {
        	$.alert("最終行まで更新しました");
        	clearPartnerOnClick();
        } else {
            var findRow = rowSelect.find(".deletePartnerRow");
            var row = findRow[0].parentNode;
            var index = row.getAttribute("data");
            var rowData = partners[index];
           
            var type = $(styleShowTableId + ' option:selected').text();
    		if(type == '取引先一覧'){
    			 doEditPartner(rowData);
    		}
    		
    		if(type == '未登録取引先一覧'){
    			doEditDomain(rowData);
    		}
            selectedRow(rowSelect);
        }
    }
    
    function selectedRow(row) {
        setNextRow(row);
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
    }

    function setNextRow(row){
        if(!row){
            return;
        }
        var indexArray = row.index();
        selectedSourceTableRow = row;
        if(indexArray<partners.length) {
            var findRow = row.next().find(".deletePartnerRow");
            var rowtmp = findRow[0].parentNode;
            var index = rowtmp.getAttribute("data");
            var rowData = partners[index];
            if(rowData){
                idPartnerNextSelect = rowData.id;
            }else{
                idPartnerNextSelect = -1;
            }
        }else{
            idPartnerNextSelect = -1;
        }
    }
    
    function styleShowTableChangeListener(){
    	$(styleShowTableId).change(function(){
    	    $("#" + partnerTableId).parent().scrollTop(0);
    		var type = $(styleShowTableId + ' option:selected').text();
    		//Show list partner table
    		if(type == '取引先一覧'){
    			disableAddPartner(false);
    			loadBusinessPartners();
    			clearPartnerOnClick();
    			setVisibleCountDomain("hidden")
    		}
    		
    		//Show list domainUnregister table
    		if(type == '未登録取引先一覧'){
    			disableAddPartner(true);
    			loadDomainUnregisters();
    			clearPartnerOnClick();
    		}
    		
    	});
    }
    
    function setVisibleCountDomain(visibility){
    	$(countDomain).css('visibility', visibility);
    }
    
    function loadDomainUnregisters(callback) {
        function onSuccess(response) {
            if(response && response.status){
            	loadDomainUnregisterData(partnerTableId, response.list); 
            }
        	if(typeof callback == 'function'){
            	callback(response.list);
            }
        }
        
        function onError(error) {

        }
        getDomainUnregisters(onSuccess, onError);
        
        
        function onSuccessPartner(response) {
            if(response && response.status){
                updatePartnerComboBox(response.list);
            }
        }
        
        function onErrorPartner(error) {

        }
        getBusinessPartners(onSuccessPartner, onErrorPartner);
    }
    
    function loadDomainUnregisterData(tableId, data) {
        domains = data;
        $(countDomain).html("<u>未登録の取引先が"+data.length+"件あります</u>");
        setVisibleCountDomain("visible")
        removeAllRow(tableId, domainReplaceRow, domainReplaceHead);
        if (domains.length > 0) {
            var html = partnerReplaceRow;
            for (var i = 0; i < domains.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> thead").html(domainReplaceHead);
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("deleteDomain", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = domains[index];
                if (rowData && rowData.id) {
                	doDeleteDomain(rowData.id);
                }
            });
            setRowClickListener("avoidRegister", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = domains[index];
                if (rowData && rowData.id) {
                	avoidRegister(rowData.id);
                }
            });
            setRowClickListener("editDomain", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                selectedSourceTableRow = parseInt(index) + 1;
                var rowData = domains[index];
                if (rowData && rowData.id) {
                    doEditDomain(rowData);
                    $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
                }
            });
        }
    }
    
    function doDeleteDomain(id) {
        function onSuccess() {
            loadDomainUnregisters()
            clearPartnerOnClick();
        }
        function onError() {
            $.alert("取引先の削除に失敗しました。");
        }
        $.confirm({
            title: '',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                    	deleteDomain(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }
    
    function avoidRegister(id) {
        function onSuccess() {
            loadDomainUnregisters()
            clearPartnerOnClick();
        }
        function onError() {
            $.alert("取引先の削除に失敗しました。");
        }
        $.confirm({
            title: '',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">本当にこのドメインを無視しますか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                    	avoidRegisterDomain(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }

    function showAlertLevelListener() {
        $('input[type=radio][name=alertType]').change(function() {
            showAlertPartner(this.value);
        });
    }

    function showAlertPartner(value){
        if(value == 0){
            alertLevelValue = $("input[name = alertLevel]:checked").val();
            $("input[name = alertLevel][value=" + 0 +"]").prop('checked', true);
            $(".showAlertLevel").css("visibility", "hidden");
            alertContentValue = $("#alertContent").val();
            $("#alertContent").val("");
            $("#alertContent").prop("disabled", true);
        }else{
            if(!alertLevelValue || alertLevelValue == 0){
                $("input[name = alertLevel][value=" + 3 +"]").prop('checked', true);
                alertContentValue = "";
            }else{
                $("input[name = alertLevel][value=" + alertLevelValue +"]").prop('checked', true);
            }
            $(".showAlertLevel").css("visibility", "visible");
            $("#alertContent").prop("disabled", false);
            $("#alertContent").val(alertContentValue);
        }
    }

})(jQuery);
